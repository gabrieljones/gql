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
package gql.client.codegen

import cats.effect._
import fs2.io.file._
import gql.parser.TypeSystemAst._
import gql.parser.QueryAst._
import gql.parser.{Value => V, AnyValue}
import cats.data._
import cats._
import org.typelevel.paiges.Doc
import cats.implicits._
import gql._
import cats.mtl.Local
import gql.parser.Pos
import cats.mtl.Tell
import cats.mtl.Handle
import cats.mtl.Stateful

object Generator {
  def modifyHead(f: Char => Char): String => String = { str =>
    val hd = str.headOption.map(f(_).toString()).getOrElse("")
    hd + str.drop(1)
  }

  val toCaml: String => String =
    modifyHead(_.toLower)

  val toPascal: String => String =
    modifyHead(_.toUpper)

  def scalaField(name: String, tpe: String): Doc =
    Doc.text(name) + Doc.char(':') + Doc.space + Doc.text(tpe)

  def hardIntercalate(left: Doc, right: Doc, xs: List[Doc], sep: Doc = Doc.empty) = {
    left +
      (Doc.hardLine + Doc.intercalate(sep + Doc.hardLine, xs)).nested(2).grouped +
      right
  }

  def hardIntercalateBracket(left: Char, sep: Doc = Doc.empty)(xs: List[Doc])(right: Char) =
    hardIntercalate(Doc.char(left), Doc.hardLine + Doc.char(right), xs, sep)

  def quoted(doc: Doc): Doc =
    Doc.char('"') + doc + Doc.char('"')

  def quoted(str: String): Doc = quoted(Doc.text(str))

  def params(xs: List[Doc]): Doc =
    Doc.intercalate(Doc.comma + Doc.space, xs).tightBracketBy(Doc.char('('), Doc.char(')'))

  def caseClass[G[_]: Foldable](name: String, xs: G[Doc]): Doc =
    Doc.text(s"final case class $name") +
      hardIntercalateBracket('(', Doc.comma)(xs.toList)(')')

  def obj[G[_]: Foldable](name: String, body: G[Doc]): Doc =
    Doc.text("object") + Doc.space + Doc.text(name) + Doc.space +
      hardIntercalateBracket('{', Doc.hardLine)(body.toList)('}')

  def optUnless(cond: Boolean)(tpe: String): String =
    if (cond) tpe else s"Option[${tpe}]"

  final case class QVar(
      varDecl: Doc,
      name: String,
      scalaType: String,
      isOption: Boolean
  )
  sealed trait ContextInfo
  object ContextInfo {
    final case class Fragment(
        fragmentName: Option[String],
        typeCnd: String
    ) extends ContextInfo
    final case class Operation(
        op: OperationType,
        variables: List[VariableDefinition]
    ) extends ContextInfo
  }
  final case class Part(
      name: String,
      typePart: NonEmptyList[Doc],
      subParts: List[Part],
      codec: NonEmptyList[Doc],
      contextInfo: Option[ContextInfo]
  ) {
    // Yes yes, it is a fold
    def collapse: Doc = {
      val tpe = caseClass(name, typePart)

      val codecSelection = hardIntercalateBracket('(', Doc.comma)(codec.toList)(')') +
        Doc.char('.') +
        (if (codec.size > 1) Doc.text("mapN") else Doc.text("map")) +
        Doc.text("(apply)")

      val tpeName = contextInfo match {
        case Some(_: ContextInfo.Fragment) => s"Option[$name]"
        case _                             => name
      }

      def codecImplicit: Doc =
        Doc.text(s"implicit val selectionSet: SelectionSet[$tpeName] = ")

      val fullCodec = contextInfo match {
        case None => codecImplicit + codecSelection
        case Some(fi: ContextInfo.Fragment) =>
          val args = fi.fragmentName.toList ++ List(fi.typeCnd)

          val fragType = fi.fragmentName.as("fragment").getOrElse("inlineFrag")

          val invocation = Doc.text(fragType) + params(args.map(quoted))

          codecImplicit +
            hardIntercalate(
              Doc.empty,
              Doc.empty,
              List(invocation + Doc.space + hardIntercalateBracket('{')(List(codecSelection))('}'))
            )
        case Some(op: ContextInfo.Operation) =>
          val operationType = op.op match {
            case OperationType.Query        => "Query"
            case OperationType.Mutation     => "Mutation"
            case OperationType.Subscription => "Subscription"
          }

          val operationTypePath =
            s"_root_.gql.parser.QueryAst.OperationType.${operationType}"

          val vars = op.variables.map { v =>
            val ims = ModifierStack.fromType(v.tpe).invert
            val scalaType = ims.showScala(identity)
            val args = quoted(v.name) :: v.defaultValue.toList.map(generateValue(_, anyValue = false))
            QVar(
              Doc.text("variable") + Doc.char('[') + Doc.text(scalaType) + Doc.char(']') + params(args),
              v.name,
              optUnless(v.defaultValue.isEmpty)(scalaType),
              v.defaultValue.nonEmpty || ims.modifiers.headOption.contains(InverseModifier.Optional)
            )
          }

          val (expr, auxilaryType) = vars.toNel match {
            case None => (codecSelection, None)
            case Some(vars) =>
              val decls = vars.toList.map(_.varDecl)

              val (contraPart, contraType) = if (vars.size > 1) {
                val ccBody = vars.toList.map { q =>
                  val optPart = if (q.isOption) Doc.text(" = None") else Doc.empty
                  Doc.text(q.name) + Doc.char(':') + Doc.space + Doc.text(q.scalaType) + optPart
                }

                val auxilaryType = caseClass("Variables", ccBody)

                val tupleApply = ("(" * vars.size) + vars.map(_.name).toList.mkString("), ") + ")"

                val unapplyPart =
                  Doc.text("Variables(") + Doc.intercalate(Doc.text(", "), vars.toList.map(_.name).map(Doc.text)) + Doc.text(")")

                val contra = Doc.text(s".contramap[Variables]") +
                  hardIntercalate(
                    Doc.text(s"{ case ") + unapplyPart + Doc.text(" =>"),
                    Doc.hardLine + Doc.char('}'),
                    List(Doc.text(tupleApply))
                  )

                (contra, Some(auxilaryType))
              } else {
                (Doc.empty, None)
              }

              val e = hardIntercalateBracket('(', Doc.text(" ~"))(decls)(')') +
                contraPart +
                Doc.text(".introduce ") +
                hardIntercalate(Doc.text("{ _ =>"), Doc.hardLine + Doc.char('}'), List(codecSelection))

              (e, contraType)
          }

          val queryExpr = Doc.text("val queryExpr = ") + expr

          val queryPrefix =
            if (vars.size == 0) Doc.text("named")
            else Doc.text("parameterized")

          val args = List(
            Doc.text(operationTypePath),
            quoted(name),
            Doc.text("queryExpr")
          )

          val compiled = Doc.text("val query = ") +
            Doc.text("_root_.gql.client.Query.") + queryPrefix + hardIntercalateBracket('(', Doc.comma)(args)(')')

          auxilaryType.map(_ + Doc.hardLine + Doc.hardLine).getOrElse(Doc.empty) +
            queryExpr +
            Doc.hardLine +
            Doc.hardLine +
            compiled
      }

      val companionParts = subParts.map(_.collapse) ++ List(fullCodec)

      val companion = Doc.text("object") + Doc.space + Doc.text(name) + Doc.space +
        hardIntercalateBracket('{', Doc.hardLine)(companionParts)('}')

      Doc.intercalate(Doc.hardLine + Doc.hardLine, List(tpe.grouped, companion.grouped))
    }
  }

  def generateValue(v: V[AnyValue], anyValue: Boolean): Doc = {
    import V._
    val tpe = if (anyValue) "AnyValue" else "Const"
    v match {
      case IntValue(v)     => Doc.text(s"V.IntValue(${v.toString()})")
      case StringValue(v)  => Doc.text(s"""V.StringValue("$v")""")
      case FloatValue(v)   => Doc.text(s"""V.FloatValue("$v")""")
      case NullValue()     => Doc.text(s"""V.NullValue()""")
      case BooleanValue(v) => Doc.text(s"""V.BooleanValue(${v.toString()})""")
      case ListValue(v) =>
        Doc.text(s"V.ListValue[${tpe}](") +
          Doc
            .intercalate(Doc.comma + Doc.line, v.map(generateValue(_, anyValue)))
            .tightBracketBy(Doc.text("List("), Doc.char(')')) +
          Doc.text(")")
      case ObjectValue(fields) =>
        Doc.text(s"V.ObjectValue[${tpe}](") +
          Doc
            .intercalate(
              Doc.comma + Doc.line,
              fields.map { case (k, v) => quoted(k) + Doc.text(" -> ") + generateValue(v, anyValue) }
            )
            .bracketBy(Doc.text("List("), Doc.char(')')) +
          Doc.text(")")
      case EnumValue(v)     => Doc.text(s"""V.EnumValue("$v")""")
      case VariableValue(v) => Doc.text(s"""V.VariableValue("$v")""")
    }
  }

  type UsedInput = Either[TypeDefinition.EnumTypeDefinition, TypeDefinition.InputObjectTypeDefinition]
  type UsedInputTypes[F[_]] = Tell[F, Set[UsedInput]]
  type CurrentPath[F[_]] = Local[F, Chain[String]]

  def in[F[_], A](field: String)(fa: F[A])(implicit P: CurrentPath[F]): F[A] =
    P.local(fa)(_ append field)

  def raise[F[_], A](msg: String)(implicit
      F: MonadError[F, NonEmptyChain[String]],
      P: CurrentPath[F]
  ) =
    P.ask.flatMap { path =>
      F.raiseError[A](NonEmptyChain.one(msg + s" at ${path.toList.mkString(".")}"))
    }

  def partitionEmittableInputDef[F[_]](env: Env, typename: String)(implicit F: Monad[F], U: UsedInputTypes[F]): F[Unit] =
    env.get(typename).traverse_ {
      case x: TypeDefinition.InputObjectTypeDefinition => U.tell(Set(Right(x)))
      case x: TypeDefinition.EnumTypeDefinition        => U.tell(Set(Left(x)))
      case _                                           => F.unit
    }

  def partitionEmittableInputType[F[_]](env: Env, tpe: gql.parser.Type)(implicit F: Monad[F], U: UsedInputTypes[F]): F[Unit] =
    partitionEmittableInputDef[F](env, ModifierStack.fromType(tpe).inner)

  final case class FieldPart(
      typePart: Doc,
      // local case class and companion object, not present if the field is a fragment or terminal
      subPart: Option[Part],
      codec: Doc
  )
  def generateField[F[_]: Parallel](
      companionName: String,
      env: Env,
      f: Field[Pos],
      fd: FieldDefinition
  )(implicit
      P: CurrentPath[F],
      U: UsedInputTypes[F],
      F: MonadError[F, NonEmptyChain[String]]
  ): F[FieldPart] = {
    val ms = ModifierStack.fromType(fd.tpe)
    val gqlName = f.alias.getOrElse(f.name)
    val n = toPascal(gqlName)

    val existsing = fd.argumentsDefinition.map(iv => iv.name -> iv).toMap
    val provided = f.arguments.map(_.nel.toList).getOrElse(Nil).map(_.name).toSet
    val existingProvided = existsing.view.filterKeys(provided.contains).toMap

    val putUsedInputsF = existingProvided.values.toList.traverse_(x => partitionEmittableInputType[F](env, x.tpe))

    putUsedInputsF &>
      f.selectionSet.value
        .map(_.selections.map(_.value))
        .parTraverse(generateTypeDef[F](env, n, ms.inner, _, None))
        .map { subPart =>
          val argPart = f.arguments.toList.flatMap(_.nel.toList).map { x =>
            Doc.text("arg") +
              (
                quoted(x.name) + Doc.char(',') + Doc.space + generateValue(x.value, anyValue = true)
              ).tightBracketBy(Doc.char('('), Doc.char(')'))
          }

          val scalaTypeName = ms.invert
            .copy(inner =
              if (subPart.isDefined) s"${companionName}.${n}"
              else ms.inner
            )
            .showScala(identity)

          val clientSel = Doc.text("sel") +
            Doc.text(scalaTypeName).tightBracketBy(Doc.char('['), Doc.char(']')) +
            params(quoted(fd.name) :: f.alias.toList.map(quoted) ++ argPart)

          val sf = scalaField(gqlName, scalaTypeName)

          FieldPart(sf, subPart, clientSel)
        }
  }

  def generateSelection[F[_]: Parallel](
      td: TypeDefinition,
      companionName: String,
      env: Env,
      fieldMap: Map[String, FieldDefinition],
      sel: Selection[Pos]
  )(implicit
      P: CurrentPath[F],
      U: UsedInputTypes[F],
      F: MonadError[F, NonEmptyChain[String]]
  ): F[FieldPart] = {
    sel match {
      case fs: Selection.FieldSelection[Pos] =>
        val f = fs.field
        fieldMap.get(f.name) match {
          case None => raise[F, FieldPart](s"Field '${f.name}' not found in type '$companionName'")
          case Some(x) =>
            in(f.name) {
              generateField[F](companionName, env, f, x)
            }
        }
      case frag: Selection.FragmentSpreadSelection[Pos] =>
        val fs = frag.fragmentSpread

        val f = env.fragmentInfos.get(fs.fragmentName)

        val optTn = s"Option[${fs.fragmentName}]"

        // If the this type is a sub-type (more specific) of the fragment type
        // That is, This <: Fragment.on
        // Then the fragment result will always be present
        val (tn, suf) = f.filter(fi => env.subtypesOf(fi.on).contains(td.name)) match {
          case Some(x) =>
            (
              fs.fragmentName,
              Doc.text(".requiredFragment") + params(quoted(x.name) :: quoted(x.on) :: Nil)
            )
          case _ => (optTn, Doc.empty)
        }

        F.pure {
          FieldPart(
            scalaField(toCaml(fs.fragmentName), tn),
            None,
            Doc.text(s"embed[${optTn}]") + suf
          )
        }
      case inlineFrag: Selection.InlineFragmentSelection[Pos] =>
        val ilf = inlineFrag.inlineFragment
        val ss = ilf.selectionSet.selections.map(_.value)
        val cnd = ilf.typeCondition.get

        val name = s"Inline${cnd}"

        val fn = s"${companionName}.${name}"

        val (tn, suf) =
          if (env.subtypesOf(cnd).contains(td.name))
            (fn, Doc.text(".requiredFragment") + params(quoted(name) :: quoted(cnd) :: Nil))
          else
            (s"Option[$fn]", Doc.empty)

        in(s"inline-fragment-${cnd}") {
          // We'd like to match every concrete subtype of the inline fragment's type condition (since typename in the result becomes concrete)
          generateTypeDef[F](env, name, cnd, ss, Some(ContextInfo.Fragment(None, cnd)))
            .map { p =>
              FieldPart(
                scalaField(toCaml(name), tn),
                Some(p),
                Doc.text(s"embed[Option[${name}]]") + suf
              )
            }
        }
    }
  }

  def generateTypeDef[F[_]: Parallel](
      env: Env,
      name: String,
      typename: String,
      sels: NonEmptyList[Selection[Pos]],
      contextInfo: Option[ContextInfo]
  )(implicit
      P: CurrentPath[F],
      U: UsedInputTypes[F],
      F: MonadError[F, NonEmptyChain[String]]
  ): F[Part] = {
    env.get(typename) match {
      case None => raise[F, Part](s"Type `$typename` not found")
      case Some(td) =>
        in(s"type-definition-${typename}") {
          val fieldMapF: F[Map[String, FieldDefinition]] = td match {
            case TypeDefinition.ObjectTypeDefinition(_, _, _, fds)    => F.pure(fds.toList.map(f => f.name -> f).toMap)
            case TypeDefinition.InterfaceTypeDefinition(_, _, _, fds) => F.pure(fds.toList.map(f => f.name -> f).toMap)
            case TypeDefinition.UnionTypeDefinition(_, _, _)          => F.pure(Map.empty[String, FieldDefinition])
            case _ =>
              raise[F, Map[String, FieldDefinition]](
                s"Type tried to perform selection on`$typename`, but it is not an object, interface or union"
              )
          }

          val tn = FieldDefinition(None, "__typename", Nil, gql.parser.Type.NonNull(gql.parser.Type.Named("String")))

          fieldMapF.flatMap { fm =>
            sels
              .parTraverse(generateSelection[F](td, name, env, fm + ("__typename" -> tn), _))
              .map { parts =>
                Part(
                  name,
                  parts.map(_.typePart),
                  parts.toList.flatMap(_.subPart.toList),
                  parts.map(_.codec),
                  contextInfo
                )
              }
          }
        }
    }
  }

  def imp(t: String) = Doc.text(s"import $t")

  final case class FragmentInfo(
      name: String,
      on: String
  )

  def gatherFragmentInfos(
      xs: NonEmptyList[ExecutableDefinition[Pos]]
  ): List[FragmentInfo] =
    xs.collect { case ExecutableDefinition.Fragment(f) =>
      val x = f.value
      FragmentInfo(x.name, x.typeCnd)
    }

  def generateExecutableDefs[F[_]: Parallel](
      env: Env,
      query: NonEmptyList[ExecutableDefinition[Pos]]
  )(implicit
      P: CurrentPath[F],
      U: UsedInputTypes[F],
      F: MonadError[F, NonEmptyChain[String]]
  ) = {
    val bodyF: F[NonEmptyList[Part]] = query.parTraverse {
      case ExecutableDefinition.Operation(o) =>
        o.value match {
          case OperationDefinition.Simple(_) =>
            raise[F, Part]("Simple operations are not supported, please name all your operations")
          case d: OperationDefinition.Detailed[Pos] =>
            in(s"operation-${d.name.get}") {
              val vds = d.variableDefinitions.toList.flatMap(_.nel.toList).map(_.value)
              val usedF = vds.traverse_(vd => partitionEmittableInputType[F](env, vd.tpe))

              usedF *>
                generateTypeDef[F](
                  env,
                  d.name.get,
                  d.tpe.toString(),
                  d.selectionSet.selections.map(_.value),
                  Some(
                    ContextInfo.Operation(
                      d.tpe,
                      d.variableDefinitions.toList.flatMap(_.nel.toList.map(_.value))
                    )
                  )
                )
            }
        }
      case ExecutableDefinition.Fragment(f) =>
        in(s"fragment-${f.value.name}") {
          val f2 = f.value
          generateTypeDef[F](
            env,
            f2.name,
            f2.typeCnd,
            f2.selectionSet.selections.map(_.value),
            Some(ContextInfo.Fragment(Some(f2.name), f2.typeCnd))
          )
        }

    }

    bodyF.map { body =>
      val bodyDoc = Doc.intercalate(Doc.hardLine + Doc.hardLine, body.toList.map(_.collapse))

      Doc.intercalate(
        Doc.hardLine,
        List(
          Doc.text("package gql.client.generated"),
          Doc.empty,
          imp("_root_.gql.client._"),
          imp("_root_.gql.parser.{Value => V, AnyValue, Const}"),
          imp("_root_.gql.client.dsl._"),
          imp("cats.implicits._")
        )
      ) + Doc.hardLine + Doc.hardLine + bodyDoc
    }
  }

  def generateEnumType(td: TypeDefinition.EnumTypeDefinition): Doc = {
    val st = Doc.text(s"sealed trait ${td.name}")

    val names = td.values.toList.map(_.name)

    val companionParts = List(
      Doc.intercalate(
        Doc.hardLine,
        names.map { e =>
          Doc.text("case object ") + Doc.text(e) + Doc.text(" extends ") + Doc.text(td.name)
        }
      )
    ) ++ List(
      Doc.text(s"implicit val circeDecoder: io.circe.Decoder[${td.name}] = io.circe.Decoder.decodeString.emap") +
        hardIntercalateBracket('{') {
          names.map { e =>
            Doc.text("case ") + quoted(e) + Doc.text(s" => Right($e)")
          } ++ List(Doc.text(s"""case x => Left(s"Unknown enum value for ${td.name}: $$x")"""))
        }('}')
    ) ++ List(
      Doc.text(s"implicit val circeEncoder: io.circe.Encoder[${td.name}] = io.circe.Encoder.encodeString.contramap[${td.name}]") +
        hardIntercalateBracket('{') {
          names.map { e =>
            Doc.text("case ") + Doc.text(e) + Doc.text(s" => ") + quoted(e)
          }
        }('}')
    ) ++ List(
      Doc.text(s"implicit val typenameInstance: Typename[${td.name}] = typename[${td.name}](") +
        quoted(td.name) + Doc.char(')')
    )

    val companion = obj(td.name, companionParts)

    st + Doc.hardLine + companion
  }

  def generateInputType(td: TypeDefinition.InputObjectTypeDefinition): Doc = {
    val fixedMods: List[(String, InverseModifierStack[String])] = td.inputFields.toList.map { f =>
      val ms = ModifierStack.fromType(f.tpe).invert

      // Add an option if the outermost modifier is not option and there is a default value
      val ms2 = if (!ms.modifiers.contains(InverseModifier.Optional) && f.defaultValue.isDefined) {
        ms.push(InverseModifier.Optional)
      } else ms

      f.name -> ms2
    }

    val ccFields = fixedMods.map { case (name, ms) =>
      val isOpt = ms.modifiers.headOption.contains(InverseModifier.Optional)

      Doc.text(name) + Doc.char(':') + Doc.space +
        Doc.text(ms.showScala(identity)) + (if (isOpt) Doc.text(" = None") else Doc.empty)
    }

    val cc = caseClass(td.name, ccFields)

    val companionParts = List(
      Doc.text(
        s"implicit val circeEncoder: io.circe.Encoder.AsObject[${td.name}] = io.circe.Encoder.AsObject.instance[${td.name}]{ a => "
      ) +
        (
          Doc
            .intercalate(
              Doc.hardLine,
              List(
                Doc.text("import io.circe.syntax._"),
                Doc.text("Map") +
                  hardIntercalateBracket('(', sep = Doc.comma) {
                    fixedMods.map { case (name, _) =>
                      quoted(name) + Doc.text(" -> ") + Doc.text(s"a.$name.asJson")
                    }
                  }(')') + Doc.text(".asJsonObject")
              )
            )
          )
          .nested(2)
          .grouped + Doc.hardLine + Doc.char('}'),
      Doc.text(s"implicit val typenameInstance: Typename[${td.name}] = typename[${td.name}](") +
        quoted(td.name) + Doc.char(')')
    )

    val companion = obj(td.name, companionParts)

    cc + Doc.hardLine + companion
  }

  def generateOneInput(ui: UsedInput): Doc =
    ui match {
      case Left(x)  => generateEnumType(x)
      case Right(x) => generateInputType(x)
    }

  def generateInputs(env: Env, uis: List[UsedInput]): Doc = {
    def walkObject[F[_]](
        x: TypeDefinition.InputObjectTypeDefinition
    )(implicit F: Monad[F], S: Stateful[F, Map[String, UsedInput]]): F[Unit] =
      x.inputFields.toList.traverse_ { i =>
        S.get.flatMap { cache =>
          val typename = ModifierStack.fromType(i.tpe).inner
          if (cache.get(typename).isDefined) F.unit
          else {
            val outcome = partitionEmittableInputDef[Writer[Set[UsedInput], *]](env, typename).written
            val insertF = outcome.toList.traverse_(x => S.modify(_ + (typename -> x)))

            insertF *> outcome.toList.traverse_ {
              case Right(io) => walkObject[F](io)
              case _         => F.unit
            }
          }
        }
      }

    val ios = uis.collect { case Right(x) => x }
    val m = ios
      .traverse_(walkObject[StateT[Eval, Map[String, UsedInput], *]](_))
      .runS(uis.map(io => io.bimap(_.name, _.name).merge -> io).toMap)
      .value

    Doc.intercalate(
      Doc.hardLine + Doc.hardLine,
      List(
        Doc.text("package gql.client.generated"),
        Doc.intercalate(
          Doc.hardLine,
          List(
            imp("_root_.gql.client.dsl._")
          )
        )
      ) ++ m.values.toList.map(generateOneInput)
    )
  }

  final case class Env(
      schema: Map[String, TypeDefinition],
      fragmentInfos: Map[String, FragmentInfo]
  ) {
    def get(name: String): Option[TypeDefinition] = schema.get(name)

    // A -> [B <: A]
    val subtypeRelations: Map[String, Set[String]] = schema.values.toList
      .collect {
        case td: TypeDefinition.ObjectTypeDefinition =>
          // this, impl1, impl2 -> this
          (td.name :: td.interfaces) tupleRight td.name
        case td: TypeDefinition.UnionTypeDefinition =>
          // this -> this
          List(td.name -> td.name)
        case td: TypeDefinition.InterfaceTypeDefinition =>
          // this, impl1, impl2 -> this
          (td.name :: td.interfaces) tupleRight td.name
      }
      .flatten
      .groupMap { case (abs, _) => abs } { case (_, conc) => conc }
      .fmap(_.toSet)

    def subtypesOf(abs: String): Set[String] = subtypeRelations.getOrElse(abs, Set.empty)

    def concreteSubtypesOf(abs: String): Set[String] =
      subtypesOf(abs).filter(schema(_) match {
        case _: TypeDefinition.ObjectTypeDefinition => true
        case _                                      => false
      })
  }

  type Err[F[_]] = Handle[F, NonEmptyChain[String]]

  def generateFor[F[_]](
      env: Env,
      query: NonEmptyList[ExecutableDefinition[Pos]]
  )(implicit E: Err[F], F: Applicative[F]): F[(Set[UsedInput], Doc)] = {
    type F[A] = WriterT[EitherT[Kleisli[Eval, Chain[String], *], NonEmptyChain[String], *], Set[UsedInput], A]
    generateExecutableDefs[F](env, query).run.value.run(Chain.empty).value.fold(E.raise, F.pure)
  }

  def getSchemaFrom(s: String): Either[String, Map[String, TypeDefinition]] =
    gql.parser.parseSchema(s).leftMap(_.prettyError.value).map(_.map(td => td.name -> td).toList.toMap)

  final case class Input(
      query: Path,
      output: Path
  )
  final case class Output(
      path: Path,
      doc: Doc
  )

  def readInputData[F[_]](i: Input)(implicit
      E: Err[F],
      F: Async[F]
  ): F[NonEmptyList[ExecutableDefinition[Pos]]] =
    Files[F]
      .readAll(i.query)
      .through(fs2.text.utf8.decode[F])
      .compile
      .foldMonoid
      .flatMap(x => gql.parser.parseQuery(x).leftFlatMap(_.prettyError.value.leftNec).fold(E.raise, F.pure))

  def generateForInput[F[_]: Async: Err](
      env: Env,
      i: Input
  ): F[(Set[UsedInput], Output)] =
    readInputData[F](i)
      .flatMap(generateFor(env, _))
      .map { case (usedInputs, doc) => (usedInputs, Output(i.output, doc)) }

  def gatherFragInfo[F[_]: Async: Err](i: Input): F[List[FragmentInfo]] =
    readInputData[F](i).map(gatherFragmentInfos(_))

  def writeStream[F[_]: Async](path: Path, doc: Doc) =
    fs2.Stream
      .iterable(doc.renderStream(80))
      .lift[F]
      .through(fs2.text.utf8.encode)
      .through(Files[F].writeAll(path))

  def readSchema[F[_]](schemaPath: Path)(implicit E: Err[F], F: Async[F]): F[Map[String, TypeDefinition]] =
    Files[F]
      .readAll(schemaPath)
      .through(fs2.text.utf8.decode[F])
      .compile
      .foldMonoid
      .flatMap(s => getSchemaFrom(s).fold(s => E.raise(NonEmptyChain.one(s)), F.pure))

  def readEnv[F[_]: Async: Err](schema: Path)(data: List[Input]): F[Env] =
    readSchema[F](schema).flatMap { m =>
      data
        .flatTraverse(gatherFragInfo[F])
        .map(f => Env(m, f.map(fi => fi.name -> fi).toMap))
    }

  def readAndGenerate[F[_]](schemaPath: Path, sharedPath: Path)(
      data: List[Input]
  )(implicit F: Async[F], E: Err[F]): F[Unit] =
    readEnv[F](schemaPath)(data).flatMap { e =>
      data
        .flatTraverse { d =>
          generateForInput[F](e, d)
            .flatMap { case (used, x) => writeStream[F](x.path, x.doc).compile.drain.as(used.toList) }
        }
        .flatMap(_.distinct.toNel.traverse_(nel => writeStream[F](sharedPath, generateInputs(e, nel.toList)).compile.drain))
    }

  def mainGenerate[F[_]: Async](schemaPath: Path, sharedPath: Path)(data: List[Input]): F[List[String]] =
    readAndGenerate[EitherT[F, NonEmptyChain[String], *]](schemaPath, sharedPath)(data).value.map(_.fold(_.toList, _ => Nil))
}
