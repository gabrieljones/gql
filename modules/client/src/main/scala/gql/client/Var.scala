/*
 * Copyright 2023 Valdemar Grange
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gql.client

import io.circe.syntax._
import cats.implicits._
import cats.data._
import io.circe._
import gql.parser.{Value => V, AnyValue}
import cats.arrow.Profunctor

final case class VariableClosure[Vars, A](
    variables: Var.Impl[Vars],
    query: SelectionSet[A]
) {
  def ~[C, D](that: VariableClosure[D, C]): VariableClosure[(Vars, D), (A, C)] =
    VariableClosure(Var.Impl.product(variables, that.variables), (query, that.query).tupled)

  def modify[B](f: SelectionSet[A] => SelectionSet[B]): VariableClosure[Vars, B] =
    VariableClosure(variables, f(query))
}

object VariableClosure {
  implicit lazy val profunctorForVariableClosure: Profunctor[VariableClosure] = new Profunctor[VariableClosure] {
    override def dimap[A, B, C, D](fab: VariableClosure[A, B])(f: C => A)(g: B => D): VariableClosure[C, D] =
      VariableClosure(fab.variables.map(_.contramapObject(f)), fab.query.map(g))
  }
}

// Don't construct such an instance directly
final case class VariableName[A](name: String) extends AnyVal {
  def asValue: V[AnyValue, Unit] = V.VariableValue(name)
}

final case class Var[Vars, B](
    impl: Var.Impl[Vars],
    variableNames: B
) {
  def ~[C, D](that: Var[C, D]): Var[(Vars, C), (B, D)] =
    Var(Var.Impl.product(impl, that.impl), (variableNames, that.variableNames))

  def contramap[C](f: C => Vars): Var[C, B] =
    Var(impl.map(_.contramapObject(f)), variableNames)

  def introduce[A](f: B => SelectionSet[A]): VariableClosure[Vars, A] =
    VariableClosure(impl, f(variableNames))

  def flatIntroduce[A, V2](f: B => VariableClosure[V2, A]): VariableClosure[(Vars, V2), A] = {
    val vc = f(variableNames)
    VariableClosure(Var.Impl.product(impl, vc.variables), vc.query)
  }
}

object Var {
  type Impl[A] = Writer[NonEmptyChain[One[?]], Encoder.AsObject[A]]
  object Impl {
    def product[A, B](fa: Impl[A], fb: Impl[B]): Impl[(A, B)] =
      fa.flatMap(aenc =>
        fb.map(benc =>
          Encoder.AsObject.instance[(A, B)] { case (a, b) =>
            JsonObject.fromMap(aenc.encodeObject(a).toMap ++ benc.encodeObject(b).toMap)
          }
        )
      )
  }

  // Why does this not work?
  // implicit def contravariantForVar[B]: Contravariant[Var[*, B]] = {
  //   type G[A] = Var[A, B]
  //   new Contravariant[G] {
  //     override def contramap[A, B](fa: G[A])(f: B => A): G[B] =
  //       Var(fa.impl.map(_.contramapObject(f)), fa.variableNames)
  //   }
  // }

  final case class One[A](
      name: VariableName[A],
      tpe: String,
      default: Option[V[AnyValue, Unit]]
  )

  def apply[A](name: String, tpe: String)(implicit
      encoder: io.circe.Encoder[A]
  ): Var[A, VariableName[A]] = {
    val vn = VariableName[A](name)
    val enc = Encoder.AsObject.instance[A](a => JsonObject(name -> a.asJson))
    new Var(Writer(NonEmptyChain.one(One(vn, tpe, None)), enc), vn)
  }

  def apply[A](name: String, tpe: String, default: Option[V[AnyValue, Unit]])(implicit
      encoder: io.circe.Encoder[A]
  ): Var[Option[A], VariableName[A]] = {
    val vn = VariableName[A](name)
    val enc = Encoder.AsObject.instance[Option[A]] {
      case None     => JsonObject.empty
      case Some(oa) => JsonObject(name -> oa.asJson)
    }
    new Var(Writer(NonEmptyChain.one(One(vn, tpe, default)), enc), vn)
  }
}
