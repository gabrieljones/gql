package gql

import cats.implicits._
import io.circe._
import cats._
import cats.effect._
import cats.data._
import gql.resolver._
import gql.Value._
import fs2.Pure
import java.util.UUID

object ast extends AstImplicits.Implicits {
  sealed trait Out[F[_], A] {
    def mapK[G[_]: Functor](fk: F ~> G): Out[G, A]
  }

  sealed trait In[A]

  sealed trait InLeaf[A] extends In[A]

  sealed trait Toplevel[+A] {
    def name: String
    def description: Option[String]
  }

  sealed trait OutToplevel[F[_], A] extends Out[F, A] with Toplevel[A] {
    override def mapK[G[_]: Functor](fk: F ~> G): OutToplevel[G, A]
  }

  sealed trait InToplevel[A] extends In[A] with Toplevel[A]

  sealed trait Selectable[F[_], A] extends OutToplevel[F, A] {
    def fieldsList: List[(String, Field[F, A, _, _])]

    def fieldMap: Map[String, Field[F, A, _, _]]

    override def mapK[G[_]: Functor](fk: F ~> G): Selectable[G, A]

    def contramap[B](f: B => A): Out[F, B]
  }

  sealed trait ObjectLike[F[_], A] extends Selectable[F, A] {
    def implementsMap: Map[String, Implementation[F, A, _]]
  }

  final case class Type[F[_], A](
      name: String,
      fields: NonEmptyList[(String, Field[F, A, _, _])],
      implementations: List[Implementation[F, A, _]],
      description: Option[String] = None
  ) extends ObjectLike[F, A] {
    def document(description: String): Type[F, A] = copy(description = Some(description))

    lazy val fieldsList: List[(String, Field[F, A, _, _])] = fields.toList

    override def contramap[B](f: B => A): Type[F, B] =
      Type(name, fields.map { case (k, v) => k -> v.contramap(f) }, implementations.map(_.contramap(f)), description)

    lazy val fieldMap = fields.toNem.toSortedMap.toMap

    lazy val implementsMap = implementations.map(i => i.implementation.value.name -> i).toMap

    def mapK[G[_]: Functor](fk: F ~> G): Type[G, A] =
      Type(name, fields.map { case (k, v) => k -> v.mapK(fk) }, implementations.map(_.mapK(fk)), description)
  }

  final case class Input[A](
      name: String,
      fields: NonEmptyArg[A],
      description: Option[String] = None
  ) extends InToplevel[A] {
    def document(description: String): Input[A] = copy(description = Some(description))
  }

  final case class Union[F[_], A](
      name: String,
      types: NonEmptyList[Variant[F, A, _]],
      description: Option[String] = None
  ) extends Selectable[F, A] {
    def document(description: String): Union[F, A] = copy(description = Some(description))

    override def contramap[B](f: B => A): Union[F, B] =
      Union(name, types.map(_.contramap(f)), description)

    lazy val instanceMap = types.map(i => i.tpe.value.name -> i).toList.toMap

    lazy val fieldMap = Map.empty

    lazy val fieldsList: List[(String, Field[F, A, _, _])] = Nil

    def mapK[G[_]: Functor](fk: F ~> G): Union[G, A] =
      Union(
        name,
        types.map(_.mapK(fk)),
        description
      )
  }

  final case class Implementation[F[_], A, B](implementation: Eval[Interface[F, B]])(implicit val specify: B => Option[A]) {
    def mapK[G[_]: Functor](fk: F ~> G): Implementation[G, A, B] =
      Implementation(implementation.map(_.mapK(fk)))

    def contramap[C](g: C => A): Implementation[F, C, B] = ???
    // Implementation[F, C, B](implementation)(c => specify(g(c)))
  }

  final case class Interface[F[_], A](
      name: String,
      fields: NonEmptyList[(String, Field[F, A, _, _])],
      implementations: List[Implementation[F, A, _]],
      description: Option[String] = None
  ) extends ObjectLike[F, A] {
    def document(description: String): Interface[F, A] = copy(description = Some(description))

    override def mapK[G[_]: Functor](fk: F ~> G): Interface[G, A] =
      copy[G, A](
        implementations = implementations.map(_.mapK(fk)),
        fields = fields.map { case (k, v) => k -> v.mapK(fk) }
      )

    lazy val fieldsList = fields.toList

    lazy val fieldMap = fields.toNem.toSortedMap.toMap

    lazy val implementsMap = implementations.map(i => i.implementation.value.name -> i).toMap

    def contramap[B](g: B => A): Interface[F, B] =
      Interface(
        name,
        fields.map { case (k, v) => k -> v.contramap(g) },
        implementations.map(_.contramap(g)),
        description
      )
  }

  final case class Scalar[F[_], A](
      name: String,
      encoder: A => Value,
      decoder: Value => Either[String, A],
      description: Option[String] = None
  ) extends OutToplevel[F, A]
      with InLeaf[A]
      with InToplevel[A] {
    def document(description: String): Scalar[F, A] = copy(description = Some(description))

    override def mapK[G[_]: Functor](fk: F ~> G): Scalar[G, A] =
      Scalar(name, encoder, decoder, description)

    def eimap[B](f: A => Either[String, B])(g: B => A): Scalar[F, B] =
      Scalar(name, encoder.compose(g), decoder.andThen(_.flatMap(f)), description)

    def rename(newName: String): Scalar[F, A] = copy(name = newName)
  }

  final case class EnumValue[A](
      value: A,
      description: Option[String] = None
  ) {
    def document(description: String): EnumValue[A] = copy(description = Some(description))
  }

  final case class Enum[F[_], A](
      name: String,
      mappings: NonEmptyList[(String, EnumValue[A])],
      description: Option[String] = None
  ) extends OutToplevel[F, A]
      with InLeaf[A]
      with InToplevel[A] {

    def document(description: String): Enum[F, A] = copy(description = Some(description))

    override def mapK[G[_]: Functor](fk: F ~> G): Enum[G, A] =
      Enum(name, mappings, description)

    lazy val kv = mappings.map { case (k, v) => k -> v.value }

    lazy val m = kv.toNem

    lazy val revm = kv.map(_.swap).toList.toMap
  }

  final case class Field[F[_], -I, T, A](
      args: Arg[A],
      resolve: Resolver[F, (I, A), T],
      output: Eval[Out[F, T]],
      description: Option[String] = None
  ) {
    def document(description: String): Field[F, I, T, A] = copy(description = Some(description))

    type A0 = A

    def mapK[G[_]: Functor](fk: F ~> G): Field[G, I, T, A] =
      Field[G, I, T, A](
        args,
        resolve.mapK(fk),
        output.map(_.mapK(fk)),
        description
      )

    def contramap[B](g: B => I): Field[F, B, T, A] =
      Field(
        args,
        resolve.contramap[(B, A)] { case (b, a) => (g(b), a) },
        output,
        description
      )
  }

  final case class Variant[F[_], A, B](tpe: Eval[Type[F, B]])(implicit val specify: A => Option[B]) {
    def mapK[G[_]: Functor](fk: F ~> G): Variant[G, A, B] =
      Variant(tpe.map(_.mapK(fk)))

    def contramap[C](g: C => A): Variant[F, C, B] =
      Variant[F, C, B](tpe)(c => specify(g(c)))
  }

  final case class OutOpt[F[_], A](of: Out[F, A]) extends Out[F, Option[A]] {
    def mapK[G[_]: Functor](fk: F ~> G): OutOpt[G, A] = OutOpt(of.mapK(fk))
  }
  object OutOpt {
    def unapply[G[_], A](p: Out[G, A]): Option[Out[G, A]] =
      p.asInstanceOf[Out[G, Option[A]]] match {
        case x: OutOpt[G, A] => Some(x.of.asInstanceOf[Out[G, A]])
        case _               => None
      }
  }

  final case class OutArr[F[_], A, C[_] <: Seq[_]](of: Out[F, A]) extends Out[F, C[A]] {
    def mapK[G[_]: Functor](fk: F ~> G): OutArr[G, A, C] = OutArr(of.mapK(fk))
  }
  object OutArr {
    def unapply[G[_], A](p: Out[G, A]): Option[Out[G, A]] =
      p.asInstanceOf[Out[G, Seq[A]]] match {
        case x: OutArr[G, _, Seq] => Some(x.of.asInstanceOf[Out[G, A]])
        case _                    => None
      }
  }

  final case class InOpt[A](of: In[A]) extends In[Option[A]]
  object InOpt {
    def unapply[A](p: In[A]): Option[In[A]] =
      p.asInstanceOf[In[Option[A]]] match {
        case x: InOpt[A] => Some(x.of.asInstanceOf[In[A]])
        case _           => None
      }
  }

  final case class InArr[A, G[_] <: Seq[_]](of: In[A]) extends In[G[A]]
  object InArr {
    def unapply[A](p: In[A]): Option[In[A]] =
      p.asInstanceOf[In[Seq[A]]] match {
        case x: InArr[A, Seq] => Some(x.of.asInstanceOf[In[A]])
        case _                => None
      }
  }

  final case class ID[A](value: A) extends AnyVal
  implicit def idTpe[F[_], A](implicit s: Scalar[F, A]): Scalar[F, ID[A]] =
    Scalar[F, ID[A]]("ID", x => s.encoder(x.value), v => s.decoder(v).map(ID(_)))
      .document(
        "The `ID` scalar type represents a unique identifier, often used to refetch an object or as key for a cache. The ID type appears in a JSON response as a String; however, it is not intended to be human-readable. When expected as an input type, any string (such as `\"4\"`) or integer (such as `4`) input value will be accepted as an ID."
      )

  object Scalar {
    def fromCirce[F[_], A](name: String)(implicit enc: Encoder[A], dec: Decoder[A]): Scalar[F, A] =
      Scalar(
        name,
        a => Value.fromJson(enc(a)),
        value =>
          dec.decodeJson(value.asJson).leftMap { case df: io.circe.DecodingFailure =>
            val maybeAt = if (df.history.size > 1) s" at ${io.circe.CursorOp.opsToPath(df.history)}" else ""
            s"decoding failure for type $name$maybeAt with message ${df.message}"
          }
      )

    implicit def invariantForScalar[F[_]] = new Invariant[Scalar[F, *]] {
      override def imap[A, B](fa: Scalar[F, A])(f: A => B)(g: B => A): Scalar[F, B] =
        Scalar(fa.name, fa.encoder.compose(g), fa.decoder.andThen(_.map(f)), fa.description)
    }
  }
}

object AstImplicits {
  import ast._

  trait Implicits extends LowPriorityImplicits {
    implicit def stringScalar[F[_]]: Scalar[F, String] = Scalar
      .fromCirce[F, String]("String")
      .document("The `String` is a UTF-8 character sequence usually representing human-readable text.")

    implicit def intScalar[F[_]]: Scalar[F, Int] = Scalar
      .fromCirce[F, Int]("Int")
      .document(
        "The `Int` scalar type represents non-fractional signed whole numeric values. Int can represent values between -(2^31) and 2^31 - 1."
      )

    implicit def longScalar[F[_]]: Scalar[F, Long] = Scalar
      .fromCirce[F, Long]("Long")
      .document(
        "The `Long` scalar type represents non-fractional signed whole numeric values. Long can represent values between -(2^63) and 2^63 - 1."
      )

    implicit def floatScalar[F[_]]: Scalar[F, Float] = Scalar
      .fromCirce[F, Float]("Float")
      .document(
        "The `Float` scalar type represents signed double-precision fractional values as specified by [IEEE 754](http://en.wikipedia.org/wiki/IEEE_floating_point)."
      )

    implicit def doubleScalar[F[_]]: Scalar[F, Double] = Scalar
      .fromCirce[F, Double]("Double")
      .document(
        "The `Double` scalar type represents signed double-precision fractional values as specified by [IEEE 754](http://en.wikipedia.org/wiki/IEEE_floating_point)."
      )

    implicit def bigIntScalar[F[_]]: Scalar[F, BigInt] = Scalar
      .fromCirce[F, BigInt]("BigInt")
      .document(
        "The `BigInt` scalar type represents non-fractional signed whole numeric values. BigInt can represent values of arbitrary size."
      )

    implicit def bigDecimalScalar[F[_]]: Scalar[F, BigDecimal] = Scalar
      .fromCirce[F, BigDecimal]("BigDecimal")
      .document(
        "The `BigDecimal` scalar type represents signed double-precision fractional values as specified by [IEEE 754](http://en.wikipedia.org/wiki/IEEE_floating_point)."
      )

    implicit def booleanScalar[F[_]]: Scalar[F, Boolean] = Scalar
      .fromCirce[F, Boolean]("Boolean")
      .document("The `Boolean` scalar type represents `true` or `false`.")

    implicit def uuidScalar[F[_]]: Scalar[F, UUID] = Scalar
      .fromCirce[F, UUID]("UUID")
      .document(
        "The `UUID` scalar type represents a UUID v4 as specified by [RFC 4122](https://tools.ietf.org/html/rfc4122)."
      )

    implicit def gqlInOption[A](implicit tpe: In[A]): In[Option[A]] = InOpt(tpe)
    implicit def gqlOutOption[F[_], A](implicit tpe: Out[F, A]): Out[F, Option[A]] = OutOpt(tpe)
  }

  trait LowPriorityImplicits {
    // implicit def liftIdToAnyFWithApplicative[F[_], A](implicit o: Out[Id, A], F: Applicative[F]): Out[F, A] =
    //   o.mapK(new (Id ~> F) { def apply[A](fa: Id[A]): F[A] = F.pure(fa) })
    implicit def gqlOutSeq[F[_], A, G[_] <: Seq[_]](implicit tpe: Out[F, A]): Out[F, G[A]] = OutArr(tpe)
    implicit def gqlInSeq[A](implicit tpe: In[A]): In[Seq[A]] = InArr(tpe)
  }
}
