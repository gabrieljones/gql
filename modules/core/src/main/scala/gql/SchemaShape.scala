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
package gql

import cats._
import cats.implicits._
import cats.mtl._
import cats.data._
import gql.ast._
import org.typelevel.paiges.Doc
import gql.parser.{Value => V, AnyValue}
import gql.util.SchemaUtil

/** The underlying graph that compiles into a GraphQL schema. Provides a plethora of methods to derive information, perform validation,
  * render, introspect and generate stub implementations.
  */
final case class SchemaShape[F[_], Q, M, S](
    query: Type[F, Q],
    mutation: Option[Type[F, M]] = Option.empty[Type[F, Unit]],
    subscription: Option[Type[F, S]] = Option.empty[Type[F, Unit]],
    outputTypes: List[OutToplevel[F, ?]] = Nil,
    inputTypes: List[InToplevel[?]] = Nil,
    positions: List[Position[F, ?]] = Directive.skipPositions[F] ++ Directive.includePositions[F]
) {
  def addOutputTypes(t: OutToplevel[F, ?]*): SchemaShape[F, Q, M, S] =
    copy(outputTypes = t.toList ++ outputTypes)

  def addInputTypes(t: InToplevel[?]*): SchemaShape[F, Q, M, S] =
    copy(inputTypes = t.toList ++ inputTypes)

  def foldMapK[G[_]: Monad: Defer](
      inPf: PartialFunction[In[?], G[Unit]] = PartialFunction.empty
  )(
      outPf: PartialFunction[Out[F, ?], G[Unit]] = PartialFunction.empty
  ): G[Unit] = SchemaShape.foldMapK[F, G](this)(inPf)(outPf)

  lazy val discover = SchemaShape.discover[F](this)

  lazy val validate = Validation.validate[F](this)

  lazy val render = SchemaShape.render[F](this)

  lazy val introspection = SchemaShape.introspect[F](this)

  lazy val ast = SchemaUtil.toAst[F](this)

  // This is safe by construction
  lazy val stub = SchemaUtil.stubSchema(ast).toOption.get

  lazy val stubInputs = SchemaShape.discover(stub).inputs
}

object SchemaShape {
  final class PartiallyAppliedSchemaShape[F[_]](val dummy: Boolean = false) extends AnyVal {
    def apply[Q, M, S](
        query: NonEmptyList[(String, Field[F, Q, ?])],
        mutation: Option[NonEmptyList[(String, Field[F, M, ?])]] = None,
        subscription: Option[NonEmptyList[(String, Field[F, S, ?])]] = None,
        outputTypes: List[OutToplevel[F, ?]] = Nil,
        inputTypes: List[InToplevel[?]] = Nil
    ): SchemaShape[F, Q, M, S] =
      SchemaShape(
        gql.dsl.tpe("Query", query.head, query.tail: _*),
        mutation.map(x => gql.dsl.tpe("Mutation", x.head, x.tail: _*)),
        subscription.map(x => gql.dsl.tpe("Subscription", x.head, x.tail: _*)),
        outputTypes,
        inputTypes
      )
  }

  def make[F[_]] = new PartiallyAppliedSchemaShape[F]

  def unit[F[_]](
      query: NonEmptyList[(String, Field[F, Unit, ?])],
      mutation: Option[NonEmptyList[(String, Field[F, Unit, ?])]] = None,
      subscription: Option[NonEmptyList[(String, Field[F, Unit, ?])]] = None,
      outputTypes: List[OutToplevel[F, ?]] = Nil,
      inputTypes: List[InToplevel[?]] = Nil
  ) = make[F](query, mutation, subscription, outputTypes, inputTypes)

  sealed trait InterfaceImpl[+F[_], A]
  object InterfaceImpl {
    final case class OtherInterface[F[_], A](i: Interface[F, A]) extends InterfaceImpl[F, A]
    final case class TypeImpl[F[_], A, B](t: Type[F, B], specify: A => Option[B]) extends InterfaceImpl[F, A]
  }

  // Key is the interface
  // Values:
  //   Key is the typename of the object
  //   Values:
  //     1. The object like type that extends the interface
  //     2. The function to map from the interface to the object)
  //
  // There is no implicit interface implementations; all transitive implementations should be explicit
  // (https://spec.graphql.org/draft/#IsValidImplementation())
  type Implementations[F[_]] = Map[String, Map[String, InterfaceImpl[F, ?]]]

  final case class DiscoveryState[F[_]](
      toplevels: Map[String, Toplevel[F, ?]],
      implementations: Implementations[F],
      positions: Map[String, List[Position[F, ?]]]
  ) {
    lazy val inputs: Map[String, InToplevel[?]] = toplevels.collect { case (k, v: InToplevel[?]) => k -> v }

    lazy val outputs: Map[String, OutToplevel[F, ?]] = toplevels.collect { case (k, v: OutToplevel[F, ?]) => k -> v }

    def addToplevel(name: String, tl: Toplevel[F, ?]): DiscoveryState[F] =
      copy(toplevels = toplevels + (name -> tl))

    def addImplementation(name: String, impl: Map[String, InterfaceImpl[F, ?]]): DiscoveryState[F] =
      copy(implementations = implementations + (name -> impl))
  }

  def visit[F[_], G[_]: Monad](
      root: SchemaShape[F, ?, ?, ?]
  )(pf: PartialFunction[Either[In[?], Out[F, ?]], G[Unit] => G[Unit]])(implicit D0: Defer[G]): G[Unit] = {
    type H[A] = Kleisli[G, Set[String], A]
    val H = Monad[H]
    val L = Local[H, Set[String]]
    val D = Defer[H]

    def runPf(e: Either[In[?], Out[F, ?]]) = pf.lift(e).getOrElse[G[Unit] => G[Unit]](ga => ga)

    def nextIfNotSeen(tl: Toplevel[F, ?])(ha: => H[Unit]): H[Unit] =
      L.ask[Set[String]].flatMap { seen =>
        if (seen.contains(tl.name)) H.unit
        else L.local(ha)(_ + tl.name)
      }

    def goOutput(out: Out[F, ?]): H[Unit] = D.defer {
      lazy val lifted = runPf(Right(out))
      out match {
        case o: OutArr[?, ?, ?, ?] => goOutput(o.of).mapF(lifted)
        case o: OutOpt[?, ?, ?]    => goOutput(o.of).mapF(lifted)
        case t: OutToplevel[F, ?] =>
          nextIfNotSeen(t) {
            lazy val nextF = t match {
              case ol: ObjectLike[F, ?] =>
                ol.abstractFields.traverse_ { case (_, af) =>
                  goOutput(af.output.value) >>
                    af.arg.traverse_(_.entries.traverse_(x => goInput(x.input.value)))
                } >> ol.implementsMap.values.toList.map(_.value).traverse_(goOutput)
              case Union(_, instances, _) =>
                instances.traverse_(inst => goOutput(inst.tpe.value))
              case _ => H.unit
            }

            nextF.mapF(lifted)
          }
      }
    }

    def goInput(in: In[?]): H[Unit] = D.defer {
      lazy val lifted = runPf(Left(in))
      in match {
        case InArr(of, _) => goInput(of).mapF(lifted)
        case InOpt(of)    => goInput(of).mapF(lifted)
        case t: InToplevel[?] =>
          nextIfNotSeen(t) {
            val nextF = t match {
              case Input(_, fields, _) =>
                fields.entries.traverse_(x => goInput(x.input.value))
              case _ => H.unit
            }

            nextF.mapF(lifted)
          }
      }
    }

    val outs = root.query :: root.mutation.toList ++ root.subscription.toList ++ root.outputTypes

    val outsF = outs.traverse_(goOutput)

    val insF = root.inputTypes.traverse_(goInput)

    (outsF >> insF).run(Set.empty)
  }

  def foldMapK[F[_], G[_]: Monad: Defer](root: SchemaShape[F, ?, ?, ?])(inPf: PartialFunction[In[?], G[Unit]])(
      outPf: PartialFunction[Out[F, ?], G[Unit]]
  ): G[Unit] = {
    type H[A] = StateT[G, Set[String], A]
    val S = Stateful[H, Set[String]]
    val H = Monad[H]

    def nextIfNotSeen(tl: Toplevel[F, ?])(ha: => H[Unit]): H[Unit] =
      S.get.flatMap { seen =>
        if (seen.contains(tl.name)) H.unit
        else S.modify(_ + tl.name) >> ha
      }

    visit[F, H](root) { e =>
      lazy val cont = e match {
        case Left(inPf(g1))   => (g2: H[Unit]) => StateT.liftF(g1).flatMap(_ => g2)
        case Right(outPf(g1)) => (g2: H[Unit]) => StateT.liftF(g1).flatMap(_ => g2)
        case _                => (g2: H[Unit]) => g2
      }

      e match {
        case Left(t: InToplevel[?])      => cont.andThen(nextIfNotSeen(t)(_))
        case Right(t: OutToplevel[F, ?]) => cont.andThen(nextIfNotSeen(t)(_))
        case _                           => cont
      }
    }.runA(Set.empty)
  }

  def discover[F[_]](shape: SchemaShape[F, ?, ?, ?]): DiscoveryState[F] = {
    type Effect[A] = State[DiscoveryState[F], A]
    def modify(f: DiscoveryState[F] => DiscoveryState[F]): Effect[Unit] =
      State.modify[DiscoveryState[F]](f)

    type InterfaceName = String
    def addValues(values: List[(InterfaceName, InterfaceImpl[F, ?])]): Effect[Unit] = {
      modify { s =>
        val withNew = values.foldLeft(s.implementations) { case (accum, (interfaceName, next)) =>
          val name = next match {
            case InterfaceImpl.OtherInterface(i) => i.name
            case InterfaceImpl.TypeImpl(t, _)    => t.name
          }
          val entry = name -> next
          accum.get(interfaceName) match {
            case None    => accum + (interfaceName -> Map(entry))
            case Some(m) => accum + (interfaceName -> (m + entry))
          }
        }

        s.copy(implementations = withNew)
      }
    }

    val program = shape.foldMapK[Effect] { case t: InToplevel[?] =>
      modify(_.addToplevel(t.name, t))
    } { case t: OutToplevel[F, ?] =>
      val modF = modify(_.addToplevel(t.name, t))
      val fa = t match {
        case ol: ObjectLike[F, ?] =>
          val values: List[(Interface[F, ?], InterfaceImpl[F, ?])] = ol match {
            case t: Type[F, a] =>
              t.implementations.map(x => x.implementation.value -> InterfaceImpl.TypeImpl(t, x.specify))
            case i: Interface[F, ?] =>
              i.implementations.map(x => x.value -> InterfaceImpl.OtherInterface(i))
          }
          addValues(values.map { case (i, ii) => i.name -> ii })
        case _ => modify(identity)
      }
      modF >> fa
    }

    val positionGroups = shape.positions.groupBy(_.directive.name)

    program.runS(DiscoveryState[F](Map.empty, Map.empty, positionGroups)).value
  }

  def renderValueDoc[C](v: V[AnyValue, C]): Doc = {
    import V._
    v match {
      case IntValue(v, _)     => Doc.text(v.toString)
      case StringValue(v, _)  => Doc.text(s""""$v"""")
      case FloatValue(v, _)   => Doc.text(v.toString)
      case NullValue(_)       => Doc.text("null")
      case BooleanValue(v, _) => Doc.text(v.toString)
      case ListValue(v, _) =>
        Doc.intercalate(Doc.comma + Doc.line, v.map(renderValueDoc)).tightBracketBy(Doc.char('['), Doc.char(']'))
      case ObjectValue(fields, _) =>
        Doc
          .intercalate(
            Doc.comma + Doc.line,
            fields.map { case (k, v) => Doc.text(k) + Doc.text(": ") + renderValueDoc(v) }
          )
          .bracketBy(Doc.char('{'), Doc.char('}'))
      case EnumValue(v, _)     => Doc.text(v)
      case VariableValue(v, _) => Doc.text(v)
    }
  }

  def render[F[_]](shape: SchemaShape[F, ?, ?, ?]) = {
    lazy val triple = Doc.text("\"\"\"")

    def doc(d: Option[String]) =
      d match {
        case None => Doc.empty
        case Some(x) =>
          val o =
            if (x.contains("\n")) {
              triple + Doc.hardLine + Doc.text(x) + Doc.hardLine + triple
            } else {
              Doc.text("\"") + Doc.text(x) + Doc.text("\"")
            }
          o + Doc.hardLine
      }

    def renderModifierStack[G[_]](ms: ModifierStack[Toplevel[G, ?]]) =
      ms.modifiers.foldLeft(Doc.text(ms.inner.name)) {
        case (accum, Modifier.List)    => accum.tightBracketBy(Doc.char('['), Doc.char(']'))
        case (accum, Modifier.NonNull) => accum + Doc.char('!')
      }

    def renderArgValueDoc(av: ArgValue[?]): Doc = {
      val o = av.defaultValue.map(dv => Doc.text(" = ") + renderValueDoc(dv)).getOrElse(Doc.empty)
      doc(av.description) +
        Doc.text(av.name) + Doc.text(": ") + renderModifierStack(ModifierStack.fromIn(av.input.value)) + o
    }

    def renderFieldDoc[G[_]](name: String, field: AbstractField[G, ?]): Doc = {
      val args = field.arg
        .map(_.entries)
        .map(nec =>
          Doc.intercalate(Doc.comma + Doc.lineOrSpace, nec.toList.map(renderArgValueDoc)).tightBracketBy(Doc.char('('), Doc.char(')'))
        )
        .getOrElse(Doc.empty)

      doc(field.description) +
        Doc.text(name) + args + Doc.text(": ") + renderModifierStack(ModifierStack.fromOut(field.output.value))
    }

    lazy val discovery: DiscoveryState[F] = shape.discover

    lazy val all = discovery.inputs ++ discovery.outputs

    lazy val exclusion = Set("String", "Int", "Float", "ID", "Boolean")

    val docs =
      all.values.toList
        .filterNot(x => exclusion.contains(x.name))
        .map { tl =>
          tl match {
            case e: Enum[?] =>
              doc(e.description) +
                Doc.text(s"enum ${e.name} {") + Doc.hardLine +
                Doc.intercalate(
                  Doc.hardLine,
                  e.mappings.toList.map { case (name, value) => doc(value.description) + Doc.text(name) }
                ) +
                Doc.hardLine + Doc.text("}")
            case Input(name, fields, desc) =>
              doc(desc) +
                Doc.text(s"input $name") + (Doc.text(" {") + Doc.hardLine + Doc
                  .intercalate(Doc.hardLine, fields.entries.toList.map(renderArgValueDoc))
                  .indent(2) + Doc.hardLine + Doc.text("}"))
            // Dont render built-in scalars
            case Scalar(name, _, _, desc) => doc(desc) + Doc.text(s"scalar $name")
            case ol @ Interface(name, fields, _, desc) =>
              val fieldsDoc = Doc
                .intercalate(
                  Doc.hardLine,
                  fields.toList.map { case (name, field) => renderFieldDoc(name, field.asAbstract) }
                )
                .indent(2)

              val interfaces = ol.implementsMap.keySet.toList.toNel
                .map { nel => Doc.text(" implements ") + Doc.intercalate(Doc.text(" & "), nel.toList.map(Doc.text)) }
                .getOrElse(Doc.empty)

              doc(desc) +
                (Doc.text(s"interface $name") + interfaces + Doc.text(" {") + Doc.hardLine +
                  fieldsDoc +
                  Doc.hardLine + Doc.text("}"))
            case ol @ Type(name, fields, _, desc, _) =>
              val fieldsDoc = Doc
                .intercalate(
                  Doc.hardLine,
                  fields.toList.map { case (name, field) => renderFieldDoc(name, field.asAbstract) }
                )
                .indent(2)

              val interfaces = ol.implementsMap.keySet.toList.toNel
                .map { nel => Doc.text(" implements ") + Doc.intercalate(Doc.text(" & "), nel.toList.map(Doc.text)) }
                .getOrElse(Doc.empty)

              doc(desc) +
                Doc.text(s"type $name") + interfaces + (Doc.text(" {") + Doc.hardLine +
                  fieldsDoc +
                  Doc.hardLine + Doc.text("}"))
            case Union(name, types, desc) =>
              val names = types.toList.map(x => Doc.text(x.tpe.value.name))
              val xs =
                if (names.size <= 3) Doc.intercalate(Doc.text(" | "), names)
                else Doc.hardLine + Doc.intercalate(Doc.hardLine, names.map(d => Doc.text("| ").indent(2) + d))

              doc(desc) + (Doc.text(s"union $name = ") + xs)
          }
        }

    Doc.intercalate(Doc.hardLine + Doc.hardLine, docs).render(80)
  }

  sealed trait __TypeKind extends Product with Serializable
  object __TypeKind {
    case object SCALAR extends __TypeKind
    case object OBJECT extends __TypeKind
    case object INTERFACE extends __TypeKind
    case object UNION extends __TypeKind
    case object ENUM extends __TypeKind
    case object INPUT_OBJECT extends __TypeKind
    case object LIST extends __TypeKind
    case object NON_NULL extends __TypeKind
  }

  def introspect[F[_]](ss: SchemaShape[F, ?, ?, ?]): NonEmptyList[(String, Field[F, Unit, ?])] = {
    import dsl._

    // We do a little lazy evaluation trick to include the introspection schema in itself
    lazy val d = {
      val ds = ss.discover
      val introspectionDiscovery = discover[F](SchemaShape.make[F](rootFields))
      // Omit Query
      val withoutQuery = introspectionDiscovery.copy(toplevels = introspectionDiscovery.toplevels - "Query")
      DiscoveryState[F](
        ds.toplevels ++ withoutQuery.toplevels,
        ds.implementations ++ withoutQuery.implementations,
        ds.positions ++ withoutQuery.positions
      )
    }

    implicit lazy val __typeKind: Enum[__TypeKind] = enumType[__TypeKind](
      "__TypeKind",
      "SCALAR" -> enumVal(__TypeKind.SCALAR),
      "OBJECT" -> enumVal(__TypeKind.OBJECT),
      "INTERFACE" -> enumVal(__TypeKind.INTERFACE),
      "UNION" -> enumVal(__TypeKind.UNION),
      "ENUM" -> enumVal(__TypeKind.ENUM),
      "INPUT_OBJECT" -> enumVal(__TypeKind.INPUT_OBJECT),
      "LIST" -> enumVal(__TypeKind.LIST),
      "NON_NULL" -> enumVal(__TypeKind.NON_NULL)
    )

    implicit lazy val __inputValue: Type[F, ArgValue[?]] = tpe[F, ArgValue[?]](
      "__InputValue",
      "name" -> lift(_.name),
      "description" -> lift(_.description),
      "type" -> lift(x => TypeInfo.fromInput(x.input.value)),
      "defaultValue" -> lift(x => x.defaultValue.map(renderValueDoc(_).render(80))),
      "isDeprecated" -> lift(_ => false),
      "deprecationReason" -> lift(_ => Option.empty[String])
    )

    final case class NamedField(
        name: String,
        field: AbstractField[F, ?]
    )

    def inclDeprecated = arg[Boolean]("includeDeprecated", value.scalar(false))

    implicit lazy val namedField: Type[F, NamedField] = tpe[F, NamedField](
      "__Field",
      "name" -> lift(_.name),
      "description" -> lift(_.field.description),
      "args" -> lift(inclDeprecated)((_, x) => x.field.arg.toList.flatMap(_.entries.toList)),
      "type" -> lift(x => TypeInfo.fromOutput(x.field.output.value)),
      "isDeprecated" -> lift(_ => false),
      "deprecationReason" -> lift(_ => Option.empty[String])
    )

    sealed trait TypeInfo extends Product with Serializable {
      def asToplevel: Option[Toplevel[F, ?]]
      def next: Option[TypeInfo]
    }
    sealed trait InnerTypeInfo extends TypeInfo {
      def next: Option[TypeInfo] = None
    }
    object TypeInfo {
      final case class OutInfo(t: OutToplevel[F, ?]) extends InnerTypeInfo {
        def asToplevel: Option[Toplevel[F, ?]] = Some(t)
      }
      final case class InInfo(t: InToplevel[?]) extends InnerTypeInfo {
        def asToplevel: Option[Toplevel[F, ?]] = Some(t)
      }

      final case class NEModifierStack(modifiers: NonEmptyList[Modifier], inner: InnerTypeInfo) extends TypeInfo {
        def asToplevel = None
        def head = modifiers.head
        def next = Some {
          modifiers.tail match {
            case Nil    => inner
            case h :: t => NEModifierStack(NonEmptyList(h, t), inner)
          }
        }
      }

      def fromOutput(o: Out[F, ?]): TypeInfo = {
        val ms = ModifierStack.fromOut[F](o)
        ms.modifiers match {
          case Nil    => OutInfo(ms.inner)
          case h :: t => NEModifierStack(NonEmptyList(h, t), OutInfo(ms.inner))
        }
      }

      def fromInput(i: In[?]): TypeInfo = {
        val ms = ModifierStack.fromIn(i)
        ms.modifiers match {
          case Nil    => InInfo(ms.inner)
          case h :: t => NEModifierStack(NonEmptyList(h, t), InInfo(ms.inner))
        }
      }
    }

    implicit lazy val __type: Type[F, TypeInfo] = tpe[F, TypeInfo](
      "__Type",
      "kind" -> lift {
        case m: TypeInfo.NEModifierStack =>
          m.head match {
            case Modifier.List    => __TypeKind.LIST
            case Modifier.NonNull => __TypeKind.NON_NULL
          }
        case oi: TypeInfo.OutInfo =>
          oi.t match {
            case _: Scalar[?]       => __TypeKind.SCALAR
            case _: Enum[?]         => __TypeKind.ENUM
            case _: Type[?, ?]      => __TypeKind.OBJECT
            case _: Interface[?, ?] => __TypeKind.INTERFACE
            case _: Union[?, ?]     => __TypeKind.UNION
          }
        case ii: TypeInfo.InInfo =>
          ii.t match {
            case Scalar(_, _, _, _) => __TypeKind.SCALAR
            case Enum(_, _, _)      => __TypeKind.ENUM
            case _: Input[?]        => __TypeKind.INPUT_OBJECT
          }
      },
      "name" -> lift(_.asToplevel.map(_.name)),
      "description" -> lift(_.asToplevel.flatMap(_.description)),
      "fields" -> lift(inclDeprecated) {
        case (_, oi: TypeInfo.OutInfo) =>
          oi.t match {
            case Type(_, fields, _, _, _)   => Some(fields.toList.map { case (k, v) => NamedField(k, v.asAbstract) })
            case Interface(_, fields, _, _) => Some(fields.toList.map { case (k, v) => NamedField(k, v.asAbstract) })
            case _                          => None
          }
        case _ => None
      },
      "interfaces" -> lift {
        case oi: TypeInfo.OutInfo =>
          oi.t match {
            case Type(_, _, impls, _, _)   => impls.map[TypeInfo](impl => TypeInfo.OutInfo(impl.implementation.value)).some
            case Interface(_, _, impls, _) => impls.map[TypeInfo](impl => TypeInfo.OutInfo(impl.value)).some
            case _                         => None
          }
        case _ => None
      },
      "possibleTypes" -> lift {
        case oi: TypeInfo.OutInfo =>
          oi.t match {
            case Interface(name, _, _, _) =>
              d.implementations
                .get(name)
                .toList
                .flatMap(_.values.toList)
                .map {
                  case InterfaceImpl.TypeImpl(ol, _)   => TypeInfo.OutInfo(ol)
                  case InterfaceImpl.OtherInterface(i) => TypeInfo.OutInfo(i)
                }
                .some
            case Union(_, instances, _) => instances.toList.map[TypeInfo](x => TypeInfo.OutInfo(x.tpe.value)).some
            case _                      => None
          }
        case _ => None
      },
      "enumValues" -> lift(inclDeprecated) { case (_, ti) =>
        ti.asToplevel.collect { case Enum(_, m, _) => m.toList.map { case (k, v) => NamedEnumValue(k, v) } }
      },
      "inputFields" -> lift(inclDeprecated) {
        case (_, ii: TypeInfo.InInfo) =>
          ii.t match {
            case Input(_, fields, _) => Some(fields.entries.toList)
            case _                   => None
          }
        case _ => None
      },
      "ofType" -> lift(_.next)
    )

    final case class NamedEnumValue(
        name: String,
        value: EnumValue[?]
    )
    implicit lazy val enumValue: Type[F, NamedEnumValue] = tpe[F, NamedEnumValue](
      "__EnumValue",
      "name" -> lift(_.name),
      "description" -> lift(_.value.description),
      "isDeprecated" -> lift(_ => false),
      "deprecationReason" -> lift(_ => Option.empty[String])
    )

    import gql.parser.TypeSystemAst.DirectiveLocation
    implicit lazy val directiveLocation: Enum[DirectiveLocation] = enumType[DirectiveLocation](
      "__DirectiveLocation",
      "QUERY" -> enumVal(DirectiveLocation.QUERY),
      "MUTATION" -> enumVal(DirectiveLocation.MUTATION),
      "SUBSCRIPTION" -> enumVal(DirectiveLocation.SUBSCRIPTION),
      "FIELD" -> enumVal(DirectiveLocation.FIELD),
      "FRAGMENT_DEFINITION" -> enumVal(DirectiveLocation.FRAGMENT_DEFINITION),
      "FRAGMENT_SPREAD" -> enumVal(DirectiveLocation.FRAGMENT_SPREAD),
      "INLINE_FRAGMENT" -> enumVal(DirectiveLocation.INLINE_FRAGMENT),
      "VARIABLE_DEFINITION" -> enumVal(DirectiveLocation.VARIABLE_DEFINITION),
      "SCHEMA" -> enumVal(DirectiveLocation.SCHEMA),
      "SCALAR" -> enumVal(DirectiveLocation.SCALAR),
      "OBJECT" -> enumVal(DirectiveLocation.OBJECT),
      "FIELD_DEFINITION" -> enumVal(DirectiveLocation.FIELD_DEFINITION),
      "ARGUMENT_DEFINITION" -> enumVal(DirectiveLocation.ARGUMENT_DEFINITION),
      "INTERFACE" -> enumVal(DirectiveLocation.INTERFACE),
      "UNION" -> enumVal(DirectiveLocation.UNION),
      "ENUM" -> enumVal(DirectiveLocation.ENUM),
      "ENUM_VALUE" -> enumVal(DirectiveLocation.ENUM_VALUE),
      "INPUT_OBJECT" -> enumVal(DirectiveLocation.INPUT_OBJECT),
      "INPUT_FIELD_DEFINITION" -> enumVal(DirectiveLocation.INPUT_FIELD_DEFINITION)
    )

    implicit lazy val directive: Type[F, Directive[?]] = tpe[F, Directive[?]](
      "__Directive",
      "name" -> lift(_.name),
      "description" -> lift(_ => Option.empty[String]),
      "locations" -> lift { dir =>
        val ys = d.positions.getOrElse(dir.name, Nil)
        val zs: List[DirectiveLocation] = ys.map {
          case Position.Field(_, _)                => DirectiveLocation.FIELD
          case Position.FragmentSpread(_, _)       => DirectiveLocation.FRAGMENT_SPREAD
          case Position.InlineFragmentSpread(_, _) => DirectiveLocation.INLINE_FRAGMENT
        }
        zs
      },
      "args" -> lift(inclDeprecated) { (_, dir) =>
        dir.arg match {
          case DirectiveArg.Empty      => Nil
          case DirectiveArg.WithArg(a) => a.entries.toList
        }
      },
      "isRepeatable" -> lift(_ => false)
    )

    case object PhantomSchema
    implicit lazy val schema: Type[F, PhantomSchema.type] = tpe[F, PhantomSchema.type](
      "__Schema",
      "description" -> lift(_ => Option.empty[String]),
      "types" -> lift { _ =>
        d.toplevels.values.toList.map {
          case x: OutToplevel[F, ?] => TypeInfo.OutInfo(x)
          case x: InToplevel[?]     => TypeInfo.InInfo(x)
        }: List[TypeInfo]
      },
      "queryType" -> lift(_ => TypeInfo.OutInfo(ss.query): TypeInfo),
      "mutationType" -> lift(_ => ss.mutation.map[TypeInfo](TypeInfo.OutInfo(_))),
      "subscriptionType" -> lift(_ => ss.subscription.map[TypeInfo](TypeInfo.OutInfo(_))),
      "directives" -> lift(_ => List.empty[Directive[?]])
    )

    lazy val rootFields: NonEmptyList[(String, Field[F, Unit, ?])] =
      fields(
        "__schema" -> lift(_ => PhantomSchema),
        "__type" -> lift(arg[String]("name")) { case (name, _) =>
          d.inputs
            .get(name)
            .map[TypeInfo](TypeInfo.InInfo(_))
            .orElse(d.outputs.get(name).map[TypeInfo](TypeInfo.OutInfo(_)))
        }
      )

    rootFields
  }
}
