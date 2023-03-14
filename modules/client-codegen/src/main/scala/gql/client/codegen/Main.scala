package gql.client.codegen

import gql.parser.TypeSystemAst._
import gql.parser.QueryAst._
import gql.parser.{Value => V, AnyValue}
import cats.data._
import cats._
import org.typelevel.paiges.Doc
import cats.implicits._
import gql.ModifierStack
import gql.parser.Value.BooleanValue
import gql.SchemaShape
import gql.InverseModifierStack

object Main extends App {
  /*
   * query MyQuery {
   *   name
   *   personalInfo {
   *     age
   *     height
   *   }
   *   friends {
   *     ...FriendFragment
   *   }
   * }
   *
   * fragment FriendFragment on Friend {
   *   name
   *   age
   *   personalInfo {
   *     age
   *   }
   *   ... on Human {
   *     name
   *     age
   *   }
   * }
   *
   * final case class MyQuery(
   *   name: String,
   *   personalInfo: MyQuery.PersonalInfo,
   *   friends: List[MyQuery.Friends]
   * )
   *
   * object MyQuery {
   *   final case class PersonalInfo(
   *     age: Int,
   *     height: Double
   *   )
   *
   *   object PersonalInfo {
   *     implicit lazy val selectionSet: SelectionSet[PersonalInfo] = (
   *       sel[Int]("age"),
   *       sel[Double]("height")
   *     ).mapN(apply)
   *   }
   *
   *   final case class Friends(
   *     friendFragment: Option[FriendFragment]
   *   )
   *
   *   object Friends {
   *     implicit lazy val selectionSet: SelectionSet[Friends] = (
   *       FriendFragment.frag
   *     ).mapN(apply)
   *   }
   *
   *   implicit lazy val selectionSet: SelectionSet[MyQuery] = (
   *     sel[String]("name"),
   *     sel[PersonalInfo]("personalInfo"),
   *     sel[List[Friends]]("friends")
   *   ).mapN(apply)
   * }
   *
   * final case class FriendFragment(
   *   name: String,
   *   age: Int,
   *   personalInfo: FriendFragment.PersonalInfo,
   *   humanFragment: Option[HumanFragment]
   * )
   *
   * object FriendFragment {
   *   final case class PersonalInfo(
   *     age: Int
   *   )
   *
   *   object PersonalInfo {
   *     implicit lazy val selectionSet: SelectionSet[PersonalInfo] = (
   *       sel[Int]("age")
   *     ).mapN(apply)
   *   }
   *
   *   final case class HumanFragment(
   *     name: String,
   *     age: Int
   *   )
   *
   *   object HumanFragment {
   *     implicit lazy val selectionSet: SelectionSet[HumanFragment] = (
   *       sel[String]("name"),
   *       sel[Int]("age")
   *     ).mapN(apply)
   *   }
   *
   *   implicit lazy val frag: SelectionSet[Option[FriendFragment]] =
   *    fragment("FriendFragment", "Friend") {
   *      (
   *        sel[String]("name"),
   *        sel[Int]("age"),
   *        sel[PersonalInfo]("personalInfo"),
   *        inlineFragment[HumanFragment]("Human")
   *      ).mapN(apply)
   *    }
   * }
   *
   */

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

  sealed trait ContextInfo
  object ContextInfo {
    final case class Fragment(
        fragmentName: Option[String],
        typeCnd: String,
        extraMatches: List[String]
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
      val tpe = Doc.text(s"final case class $name") + params(typePart.toList)

      val codecSelection = hardIntercalateBracket('(', Doc.comma)(codec.toList)(')') +
        Doc.char('.') +
        (if (codec.size > 1) Doc.text("mapN") else Doc.text("map")) +
        Doc.text("(apply)")

      def codecImplicit: Doc =
        Doc.text(s"implicit val selectionSet: SelectionSet[$name] = ")

      val fullCodec = contextInfo match {
        case None => codecImplicit + codecSelection
        case Some(fi: ContextInfo.Fragment) =>
          val args = fi.fragmentName.toList ++ List(fi.typeCnd) ++ fi.extraMatches

          val fragType = fi.fragmentName.as("fragment").getOrElse("inlineFragment")

          val invocation = Doc.text(fragType) + params(args.map(quoted))

          codecImplicit +
            hardIntercalate(
              Doc.empty,
              Doc.empty,
              List(
                invocation + Doc.space + hardIntercalateBracket('{')(List(codecSelection))('}')
              )
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
            val scalaType = ModifierStack.fromType(v.tpe).invert.showScala(identity)
            val args = quoted(v.name) :: v.defaultValue.toList.map(generateValue)
            Doc.text("variable") + Doc.char('[') + Doc.text(scalaType) + Doc.char(']') + params(args)
          }

          val expr = vars.toNel match {
            case None => codecSelection
            case Some(vars) =>
              hardIntercalateBracket('(', Doc.text(" ~"))(vars.toList)(')') + Doc.text(".introduce ") +
                hardIntercalate(Doc.text("{ _ =>"), Doc.hardLine + Doc.char('}'), List(codecSelection))
          }

          val queryExpr = Doc.text("val queryExpr = ") + expr

          val queryPrefix =
            if (vars.size == 0) Doc.text("named")
            else Doc.text("parametrized")

          val args = List(
            Doc.text(operationTypePath),
            quoted(name),
            Doc.text("queryExpr")
          )

          val compiled = Doc.text("val query = ") +
            Doc.text("_root_.gql.client.Query.") + queryPrefix + hardIntercalateBracket('(', Doc.comma)(args)(')')

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

  def generateValue(v: V[AnyValue]): Doc =
    SchemaShape.renderValueDoc(v)

  final case class FieldPart(
      typePart: Doc,
      // local case class and companion object, not present if the field is a fragment or terminal
      subPart: Option[Part],
      codec: Doc
  )
  def generateField(
      companionName: String,
      schema: Map[String, TypeDefinition],
      f: Field,
      fd: FieldDefinition
  ): FieldPart = {
    val ms = ModifierStack.fromType(fd.tpe)
    val n = toPascal(f.alias.getOrElse(f.name))

    val subPart: Option[Part] = f.selectionSet.value
      .map(_.selections.map(_.value))
      .map(generateTypeDef(schema, n, ms.inner, _, None))

    val argPart = f.arguments.toList.flatMap(_.nel.toList).map { x =>
      Doc.text("arg") +
        (
          quoted(x.name) + Doc.char(',') + Doc.space + generateValue(x.value)
        ).tightBracketBy(Doc.char('('), Doc.char(')'))
    }

    val clientSel = Doc.text("sel") +
      Doc.text(ms.invert.showScala(identity)).tightBracketBy(Doc.char('['), Doc.char(']')) +
      params(quoted(fd.name) :: argPart)

    val scalaTypeName =
      if (subPart.isDefined) s"${companionName}.${n}"
      else ms.inner

    val sf = scalaField(fd.name, scalaTypeName)

    FieldPart(sf, subPart, clientSel)
  }

  def generateSelection(
      companionName: String,
      schema: Map[String, TypeDefinition],
      fieldMap: Map[String, FieldDefinition],
      sel: Selection
  ): FieldPart = {
    sel match {
      case fs: Selection.FieldSelection =>
        val f = fs.field
        fieldMap.get(f.name) match {
          case None    => ???
          case Some(x) => generateField(companionName, schema, f, x)
        }
      case frag: Selection.FragmentSpreadSelection =>
        val fs = frag.fragmentSpread
        FieldPart(
          scalaField(toCaml(fs.fragmentName), fs.fragmentName),
          None,
          Doc.text(s"embed[${fs.fragmentName}]")
        )
      case inlineFrag: Selection.InlineFragmentSelection =>
        val ilf = inlineFrag.inlineFragment
        val ss = ilf.selectionSet.selections.map(_.value)
        val cnd = ilf.typeCondition.get
        val name = s"Inline${cnd}"
        val p = generateTypeDef(schema, name, cnd, ss, Some(ContextInfo.Fragment(None, cnd, Nil)))
        FieldPart(
          scalaField(toCaml(name), s"${companionName}.${name}"),
          Some(p),
          Doc.text(s"embed[${name}]")
        )
    }
  }

  def generateTypeDef(
      schema: Map[String, TypeDefinition],
      name: String,
      typename: String,
      sels: NonEmptyList[Selection],
      contextInfo: Option[ContextInfo]
  ): Part = {
    schema.get(typename) match {
      case None => throw new Exception(s"Type $typename not found")
      case Some(td) =>
        val fieldMap = td match {
          case TypeDefinition.ObjectTypeDefinition(_, _, _, fds)    => fds.toList.map(f => f.name -> f).toMap
          case TypeDefinition.InterfaceTypeDefinition(_, _, _, fds) => fds.toList.map(f => f.name -> f).toMap
          case TypeDefinition.UnionTypeDefinition(_, _, _)          => Map.empty[String, FieldDefinition]
          case _                                                    => ???
        }

        val parts = sels.map(generateSelection(name, schema, fieldMap, _))

        Part(
          name,
          parts.map(_.typePart),
          parts.toList.flatMap(_.subPart.toList),
          parts.map(_.codec),
          contextInfo
        )
    }
  }

  def generateFor(schema: Map[String, TypeDefinition], query: NonEmptyList[ExecutableDefinition]) = {
    val body = query.map {
      case ExecutableDefinition.Operation(o) =>
        o.value match {
          case OperationDefinition.Simple(_) => ???
          case d: OperationDefinition.Detailed =>
            generateTypeDef(
              schema,
              d.name.get,
              "Query",
              d.selectionSet.selections.map(_.value),
              Some(
                ContextInfo.Operation(
                  d.tpe,
                  d.variableDefinitions.toList.flatMap(_.nel.toList.map(_.value))
                )
              )
            )
        }
      case ExecutableDefinition.Fragment(f) => {
        val f2 = f.value
        generateTypeDef(
          schema,
          f2.name,
          f2.typeCnd,
          f2.selectionSet.selections.map(_.value),
          Some(ContextInfo.Fragment(Some(f2.name), f2.typeCnd, Nil))
        )
      }
    }

    val bodyDoc = Doc.intercalate(Doc.hardLine + Doc.hardLine, body.toList.map(_.collapse))

    def imp(t: String) = Doc.text(s"import $t")
    Doc.intercalate(
      Doc.hardLine,
      List(
        Doc.text("package gql.client.generated"),
        Doc.empty,
        imp("_root_.gql.client._"),
        imp("_root_.gql.client.dsl._")
      )
    ) + Doc.hardLine + Doc.hardLine + bodyDoc
  }

  val parsed = gql.parser.parseSchema(testSchema).fold(x => throw new Exception(x.toString()), identity)

  val parsedQuery = gql.parser.parseQuery(testQuery).fold(x => throw new Exception(x.toString()), identity)

  val m = parsed.map(td => td.name -> td).toList.toMap

  val o = generateFor(m, parsedQuery)

  println(o.render(80))

  def testQuery = """
  query AlienQuery($name: String!, $id: ID!) {
    dog {
      ... DogFragment
      ... on Dog {
        name
        barkVolume
      }
      owner {
        name
      }
    }
    findDog(searchBy: { name: "Barky" }) {
      ... DogFragment
    }
  }

  fragment DogFragment on Dog {
    name,
    barkVolume
  }
  """

  def testSchema = """
type Query {
  dog: Dog
  findDog(searchBy: FindDogInput): Dog
}

enum DogCommand {
  SIT
  DOWN
  HEEL
}

type Dog implements Pet {
  name: String!
  nickname: String
  barkVolume: Int
  doesKnowCommand(dogCommand: DogCommand!): Boolean!
  isHouseTrained(atOtherHomes: Boolean): Boolean!
  owner: Human
}

interface Sentient {
  name: String!
}

interface Pet {
  name: String!
}

type Alien implements Sentient {
  name: String!
  homePlanet: String
}

type Human implements Sentient {
  name: String!
  pets: [Pet!]
}

enum CatCommand {
  JUMP
}

type Cat implements Pet {
  name: String!
  nickname: String
  doesKnowCommand(catCommand: CatCommand!): Boolean!
  meowVolume: Int
}

union CatOrDog = Cat | Dog
union DogOrHuman = Dog | Human
union HumanOrAlien = Human | Alien

input FindDogInput {
  name: String
  owner: String
}
"""
}

