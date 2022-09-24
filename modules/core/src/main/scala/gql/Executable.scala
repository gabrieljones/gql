package gql

import cats.Monoid
import gql.parser._
import gql.parser.{QueryParser => P}
import fs2.Stream
import cats.implicits._
import cats.effect._
import io.circe._
import io.circe.syntax._
import cats.data._
import gql.interpreter._
import gql.parser.ParserUtil

sealed trait ExecutableQuery[F[_], Q, M, S]

object ExecutableQuery {
  case class Result(
      errors: Chain[EvalFailure],
      data: JsonObject
  ) {
    lazy val asGraphQL =
      JsonObject(
        "errors" -> errors.map(_.asGraphQL).asJson,
        "data" -> data.asJson
      )
  }

  final case class ValidationError[F[_], Q, M, S](msg: PreparedQuery.PositionalError) extends ExecutableQuery[F, Q, M, S]
  final case class Query[F[_], Q, M, S](run: Q => F[Result]) extends ExecutableQuery[F, Q, M, S]
  final case class Mutation[F[_], Q, M, S](run: M => F[Result]) extends ExecutableQuery[F, Q, M, S]
  final case class Subscription[F[_], Q, M, S](run: S => fs2.Stream[F, Result]) extends ExecutableQuery[F, Q, M, S]

  def assemble[F[_]: Statistics, Q, M, S](
      query: NonEmptyList[P.ExecutableDefinition],
      schema: Schema[F, Q, M, S],
      variables: Map[String, Json]
  )(implicit F: Async[F]): ExecutableQuery[F, Q, M, S] = {
    PreparedQuery.prepare2(query, schema, variables) match {
      case Left(err) => ExecutableQuery.ValidationError(err)
      case Right(x) =>
        x match {
          case (P.OperationType.Query, rootFields) =>
            ExecutableQuery.Query[F, Q, M, S](
              Interpreter.runSync(_, rootFields, schema.state).map { case (e, d) => Result(e, d) }
            )
          case (P.OperationType.Mutation, rootFields) =>
            ExecutableQuery.Mutation[F, Q, M, S](
              Interpreter.runSync(_, rootFields, schema.state).map { case (e, d) => Result(e, d) }
            )
          case (P.OperationType.Subscription, rootFields) =>
            ExecutableQuery.Subscription[F, Q, M, S](
              Interpreter.runStreamed(_, rootFields, schema.state).map { case (e, d) => Result(e, d) }
            )
        }
    }
  }
}
