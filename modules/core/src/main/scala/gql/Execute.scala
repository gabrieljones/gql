package gql

import gql.parser.{QueryParser => P}
import fs2.Stream
import cats.implicits._
import cats.effect._
import io.circe._
import io.circe.syntax._
import cats.data._
import gql.interpreter._

object Execute {

  type Output = (Chain[EvalFailure], JsonObject)

  sealed trait ExecutorOutcome[F[_], Q, M, S]
  object ExecutorOutcome {
    final case class ValidationError[F[_], Q, M, S](msg: PreparedQuery.PositionalError) extends ExecutorOutcome[F, Q, M, S]
    final case class Query[F[_], Q, M, S](run: Q => F[Output]) extends ExecutorOutcome[F, Q, M, S]
    final case class Mutation[F[_], Q, M, S](run: M => F[Output]) extends ExecutorOutcome[F, Q, M, S]
    final case class Stream[F[_], Q, M, S](run: S => fs2.Stream[F, Output]) extends ExecutorOutcome[F, Q, M, S]
  }

  def executor[F[_]: Statistics, Q, M, S](
      query: NonEmptyList[P.ExecutableDefinition],
      schema: Schema[F, Q],
      variables: Map[String, Json]
  )(implicit F: Async[F]): ExecutorOutcome[F, Q, M, S] = {
    PreparedQuery.prepare2(query, schema, variables) match {
      case Left(err) => ExecutorOutcome.ValidationError(err)
      case Right(x) =>
        x match {
          case PreparedQuery.OperationType.Query(rootFields) =>
            ExecutorOutcome.Query[F, Q, M, S](Interpreter.runSync(_, rootFields, schema.state))
          case PreparedQuery.OperationType.Mutation(rootFields) =>
            ExecutorOutcome.Mutation[F, Q, M, S](Interpreter.runSync(_, rootFields, schema.state))
          case PreparedQuery.OperationType.Subscription(dataStream, root) =>
            ExecutorOutcome.Stream[F, Q, M, S] { s =>
              dataStream(s).switchMap(Interpreter.runStreamed[F](_, NonEmptyList.one(root), schema.state))
            }
        }
    }
  }

  def formatErrors(xs: Chain[EvalFailure]) = {
    final case class GQLError(
        message: String,
        path: CursorGroup
    )
    def formatEither(e: Either[Throwable, String]) =
      e.swap.map { e =>
        e.printStackTrace()
        "internal error"
      }.merge

    import EvalFailure._
    val errors =
      xs.flatMap { ef =>
        ef match {
          case EffectResolution(path, error, _) =>
            Chain(GQLError(formatEither(error), path))
          case SignalHeadResolution(path, error, _) =>
            Chain(GQLError(formatEither(error), path))
          case BatchMissingKey(path, _, _, _) =>
            Chain(GQLError("internal error", path))
          case SignalTailResolution(path, error, input) =>
            Chain(GQLError(formatEither(error), path))
          case BatchPartitioning(path, error, input) =>
            Chain(GQLError(formatEither(error), path))
          case BatchPostProcessing(path, error, resultMap) =>
            Chain(GQLError(formatEither(error), path))
          case BatchResolution(paths, exception, keys) =>
            paths.map(path => GQLError("internal error", path))
          case StreamHeadResolution(path, err, _) =>
            Chain(GQLError(formatEither(err), path))
          case StreamTailResolution(path, err) =>
            Chain(GQLError(formatEither(err), path))
        }
      }
    errors.map { err =>
      Json.obj(
        "message" -> Json.fromString(err.message),
        "path" -> err.path.absolutePath.path.map {
          case GraphArc.Field(_, name)    => Json.fromString(name)
          case GraphArc.Index(idx)        => Json.fromInt(idx)
          case GraphArc.Fragment(_, name) => Json.fromString(s"fragment:$name")
        }.asJson
      )
    }.asJson
  }
}
