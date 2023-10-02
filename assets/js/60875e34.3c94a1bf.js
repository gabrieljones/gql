"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[554],{3905:(e,n,t)=>{t.d(n,{Zo:()=>d,kt:()=>c});var a=t(7294);function i(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function r(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function o(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?r(Object(t),!0).forEach((function(n){i(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):r(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function l(e,n){if(null==e)return{};var t,a,i=function(e,n){if(null==e)return{};var t,a,i={},r=Object.keys(e);for(a=0;a<r.length;a++)t=r[a],n.indexOf(t)>=0||(i[t]=e[t]);return i}(e,n);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(a=0;a<r.length;a++)t=r[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(i[t]=e[t])}return i}var s=a.createContext({}),p=function(e){var n=a.useContext(s),t=n;return e&&(t="function"==typeof e?e(n):o(o({},n),e)),t},d=function(e){var n=p(e.components);return a.createElement(s.Provider,{value:n},e.children)},m={inlineCode:"code",wrapper:function(e){var n=e.children;return a.createElement(a.Fragment,{},n)}},u=a.forwardRef((function(e,n){var t=e.components,i=e.mdxType,r=e.originalType,s=e.parentName,d=l(e,["components","mdxType","originalType","parentName"]),u=p(t),c=i,h=u["".concat(s,".").concat(c)]||u[c]||m[c]||r;return t?a.createElement(h,o(o({ref:n},d),{},{components:t})):a.createElement(h,o({ref:n},d))}));function c(e,n){var t=arguments,i=n&&n.mdxType;if("string"==typeof e||i){var r=t.length,o=new Array(r);o[0]=u;var l={};for(var s in n)hasOwnProperty.call(n,s)&&(l[s]=n[s]);l.originalType=e,l.mdxType="string"==typeof e?e:i,o[1]=l;for(var p=2;p<r;p++)o[p]=t[p];return a.createElement.apply(null,o)}return a.createElement.apply(null,t)}u.displayName="MDXCreateElement"},6351:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>o,default:()=>m,frontMatter:()=>r,metadata:()=>l,toc:()=>p});var a=t(7462),i=(t(7294),t(3905));const r={title:"Tutorial"},o=void 0,l={unversionedId:"tutorial",id:"tutorial",title:"Tutorial",description:"For this showcase, Star Wars will be our domain of choice.",source:"@site/docs/tutorial.md",sourceDirName:".",slug:"/tutorial",permalink:"/gql/docs/tutorial",draft:!1,editUrl:"https://github.com/valdemargr/gql/tree/main/docs/tutorial.md",tags:[],version:"current",frontMatter:{title:"Tutorial"},sidebar:"docs",previous:{title:"Modules",permalink:"/gql/docs/overview/modules"},next:{title:"Output types",permalink:"/gql/docs/server/schema/output_types"}},s={},p=[{value:"Setup",id:"setup",level:2},{value:"GraphQL",id:"graphql",level:2},{value:"gql schema",id:"gql-schema",level:3},{value:"Schema",id:"schema",level:2}],d={toc:p};function m(e){let{components:n,...t}=e;return(0,i.kt)("wrapper",(0,a.Z)({},d,t,{components:n,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"For this showcase, Star Wars will be our domain of choice."),(0,i.kt)("p",null,"We'll go through setting up a GraphQL sevrver that represents our Star Wars datatypes by:"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Defining idiomatic scala types for the Star Wars domain."),(0,i.kt)("li",{parentName:"ol"},"Introducing GraphQL."),(0,i.kt)("li",{parentName:"ol"},"Defining a gql schema for our datatypes.")),(0,i.kt)("h2",{id:"setup"},"Setup"),(0,i.kt)("p",null,"We'll have to introduce some dependencies first.\nFor this tutorial, we'll be pulling everything needed to construct a server."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies ++= Seq(\n  "io.github.valdemargr" %% "gql-server" % "0.3.1",\n)\n')),(0,i.kt)("p",null,"We'll define the domain types for characters and episodes as follows."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"sealed trait Episode\n\nobject Episode {\n  case object NewHope extends Episode\n  case object Empire extends Episode\n  case object Jedi extends Episode\n}\n\ntrait Character {\n  def id: String\n  def name: Option[String]\n  def friends: List[String]\n  def appearsIn: List[Episode]\n}\n\nfinal case class Human(\n    id: String,\n    name: Option[String],\n    friends: List[String],\n    appearsIn: List[Episode],\n    homePlanet: Option[String]\n) extends Character\n\nfinal case class Droid(\n    id: String,\n    name: Option[String],\n    friends: List[String],\n    appearsIn: List[Episode],\n    primaryFunction: String\n) extends Character\n")),(0,i.kt)("p",null,"Now that we have defined our domain, we can define our Star Wars algebra."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"trait Repository[F[_]] {\n  def getHero(episode: Option[Episode]): F[Character]\n\n  def getCharacter(id: String): F[Option[Character]]\n\n  def getHuman(id: String): F[Option[Human]]\n\n  def getDroid(id: String): F[Option[Droid]]\n}\n")),(0,i.kt)("p",null,"The respository is abstract and as such, we can implement it in any way we want."),(0,i.kt)("p",null,"These types will be the foundation of our GraphQL implementation."),(0,i.kt)("p",null,"To construct the schema later on, we need some imports."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"import gql._\nimport gql.dsl.all._\nimport gql.ast._\nimport cats.effect._\nimport cats.implicits._\n")),(0,i.kt)("h2",{id:"graphql"},"GraphQL"),(0,i.kt)("p",null,"GraphQL comes in two parts:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"A schema that is provided by a server which acts as a type system for the data."),(0,i.kt)("li",{parentName:"ul"},"A query language that is used to query the data.")),(0,i.kt)("p",null,"Consider the following schema:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"# 'enum' defines an enumeration\nenum Episode {\n  NEWHOPE\n  EMPIRE\n  JEDI\n}\n\n# 'type' defined a record type\ntype Human {\n  id: String! # ! means non-nullable, which is the default in gql\n\n  name: String # name is nullable, represented as `Option` in gql\n\n  friends: [Human!]! # [T] is a list of T\n  # notice that you must specify nullability for the list and its elements\n\n  appearsIn: [Episode!]! # Lists can contain any types, including enums\n\n  homePlanet: String\n}\n\n# the Query type must be present in the schema\n# it is the entry point for queries\ntype Query {\n  humans: [Human!]!\n}\n")),(0,i.kt)("p",null,"The above defined schema looks like some of our Scala types, and we will in fact be defining our own gql schema later on in a way that syntatically resembles graphql schemas."),(0,i.kt)("p",null,"Notice that the ",(0,i.kt)("inlineCode",{parentName:"p"},"friends")," field on the ",(0,i.kt)("inlineCode",{parentName:"p"},"Human")," type is a list of ",(0,i.kt)("inlineCode",{parentName:"p"},"Human"),"s, that is, it is a recursive type.\nIn graphql recursive types are allowed."),(0,i.kt)("p",null,"Now that we have defined our type system, we would like to query it also.\nThe ",(0,i.kt)("a",{parentName:"p",href:"https://spec.graphql.org/draft/"},"GraphQL specification")," states that the schema should expose a ",(0,i.kt)("inlineCode",{parentName:"p"},"Query")," type.\nWe'll query all human names by constructing the following query:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},"# we specify that we'd like to query the api\nquery {\n  # the query type defines the 'humans' field\n  # which returns a list of humans\n  humans {\n    # we perform a \"selection\" on humans\n    # which tells the schema that we would like to select some fields\n    # for every human in the list\n\n    # we select the 'name' field\n    name\n  }\n}\n")),(0,i.kt)("p",null,"GraphQL also features interfaces, unions and a variant of pattern matching.\nWe'll take a look at these features later on, but for now let's try to implement the above schema."),(0,i.kt)("h3",{id:"gql-schema"},"gql schema"),(0,i.kt)("p",null,"We'll start by defining the enum ",(0,i.kt)("inlineCode",{parentName:"p"},"Episode")," and ",(0,i.kt)("inlineCode",{parentName:"p"},"Human")," type."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'// We let gql know how to use the scala type `Episode` in our schema\n// by associating the `Episode` cases with a string representation\nimplicit val episode: Enum[Episode] = enumType[Episode](\n  // Name as first parameter\n  "Episode",\n  // The rest of the parameters are the enum values\n  "NEWHOPE" -> enumVal(Episode.NewHope),\n  "EMPIRE" -> enumVal(Episode.Empire),\n  "JEDI" -> enumVal(Episode.Jedi)\n)\n\n// Notice how episode is also an implicit (given in scala 3)\n// such that the `Human` type can use it wihtout having to reference it directly\n\nimplicit lazy val human: Type[IO, Human] = tpe[IO, Human](\n  // The typename that will appear in our schema\n  "Human",\n  // Now we can start defining the fields of our type.\n  // Fields in gql are more or less regular functions with some extra information attached to them\n  // Here the "id" field is defined as a function `Human => Option[String]` which is lifted into a field\n  "id" -> lift(_.id),\n  "name" -> lift(_.name),\n\n  // We\'ll leave friends empty for now :-)\n  "friends" -> lift(_ => List.empty[Human]),\n  \n  // Notice how we implicitly use the `episode` type here\n  // If we were to remove the implicit `episode` from scope\n  // we would get a "missing implicit" compiler error\n  "appearsIn" -> lift(_.appearsIn),\n\n  "homePlanet" -> lift(_.homePlanet)\n)\n')),(0,i.kt)("p",null,"Now let's take a look at the schema."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'SchemaShape.unit[IO](\n  fields(\n    // We\'ll also leave the \'humans\' field empty for now\n    "humans" -> lift(_ => List.empty[Human])\n  )\n).render\n// res0: String = """enum Episode {\n// NEWHOPE\n// EMPIRE\n// JEDI\n// }\n// \n// type Query {\n//   humans: [Human!]!\n// }\n// \n// type Human {\n//   id: String!\n//   name: String\n//   friends: [Human!]!\n//   appearsIn: [Episode!]!\n//   homePlanet: String\n// }"""\n')),(0,i.kt)("p",null,"Very cool! We have defined our first gql schema.\nNow let's try to add the rest of the types into a neat package."),(0,i.kt)("h2",{id:"schema"},"Schema"),(0,i.kt)("p",null,"Let's define a schema for our whole Star Wars API:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'// We define a schema as a class since we want some dependencies.\nfinal class StarWarsSchema[F[_]](repo: Repository[F])(implicit F: Async[F]) {\n  implicit lazy val episode: Enum[Episode] = enumType[Episode](\n    "Episode",\n    "NEWHOPE" -> enumVal(Episode.NewHope),\n    "EMPIRE" -> enumVal(Episode.Empire),\n    "JEDI" -> enumVal(Episode.Jedi)\n  )\n\n  // We can define our Character interface from the shared field definitions\n  implicit lazy val character: Interface[F, Character] = interface[F, Character](\n    "Character",\n    "id" -> lift(_.id),\n    "name" -> lift(_.name),\n    "friends" -> eff(_.friends.traverse(repo.getCharacter)),\n    "appearsIn" -> lift(_.appearsIn),\n    "secretBackstory" -> \n      build[F, Character](_.emap(_ => "secretBackstory is secret.".leftIor[String]))\n  )\n\n  // Human has the character fields along with its own unique "homePlanet" field\n  implicit lazy val human: Type[F, Human] = tpe[F, Human](\n    "Human",\n    "homePlanet" -> lift(_.homePlanet)\n  ).subtypeImpl[Character]\n\n  // Droid has the character fields along with its own unique "primaryFunction" field\n  implicit lazy val droid: Type[F, Droid] = tpe[F, Droid](\n    "Droid",\n    "primaryFunction" -> lift(_.primaryFunction)\n  ).subtypeImpl[Character]\n\n  // Arguments types can be defined as values as well\n  val episodeArg = arg[Option[Episode]]("episode")\n  val idArg = arg[String]("id")\n\n  val makeSchema = Schema.query(\n    tpe[F, Unit](\n      "Query",\n      // There are various ways to define fields depending on how unorthodox a usecase is\n      // `build` is a more explicit way of defining a field\n      "hero" -> build.from(arged(episodeArg).evalMap(repo.getHero)),\n      "human" -> build.from(arged(idArg).evalMap(repo.getHuman)),\n      "droid" -> eff(idArg){ case (id, _) => repo.getDroid(id) }\n    )\n  )\n}\n')),(0,i.kt)("p",null,"Lets construct a simple in-memory repository for our schema:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'val luke = Human(\n  "1000",\n  "Luke Skywalker".some,\n  "1002" :: "1003" :: "2000" :: "2001" :: Nil,\n  Episode.NewHope :: Episode.Empire :: Episode.Jedi :: Nil,\n  "Tatooine".some\n)\n\nval vader = Human(\n  "1001",\n  "Darth Vader".some,\n  "1004" :: Nil,\n  Episode.NewHope :: Episode.Empire :: Episode.Jedi :: Nil,\n  "Tatooine".some\n)\n\nval han = Human(\n  "1002",\n  "Han Solo".some,\n  "1000" :: "1003" :: "2001" :: Nil,\n  Episode.NewHope :: Episode.Empire :: Episode.Jedi :: Nil,\n  None\n)\n\nval leia = Human(\n  "1003",\n  "Leia Organa".some,\n  "1000" :: "1002" :: "2000" :: "2001" :: Nil,\n  Episode.NewHope :: Episode.Empire :: Episode.Jedi :: Nil,\n  "Alderaan".some\n)\n\nval tarkin = Human(\n  "1004",\n  "Wilhuff Tarkin".some,\n  "1001" :: Nil,\n  Episode.NewHope :: Nil,\n  None\n)\n\nval humanData =\n  List(luke, vader, han, leia, tarkin)\n    .map(x => x.id -> x)\n    .toMap\n\nval threepio = Droid(\n  "2000",\n  "C-3PO".some,\n  "1000" :: "1002" :: "1003" :: "2001" :: Nil,\n  Episode.NewHope :: Episode.Empire :: Episode.Jedi :: Nil,\n  "Protocol"\n)\n\nval artoo = Droid(\n  "2001",\n  "R2-D2".some,\n  "1000" :: "1002" :: "1003" :: Nil,\n  Episode.NewHope :: Episode.Empire :: Episode.Jedi :: Nil,\n  "Astromech"\n)\n\nval droidData =\n  List(threepio, artoo)\n    .map(x => x.id -> x)\n    .toMap\n\ndef repo: Repository[IO] = new Repository[IO] {\n  def getHero(episode: Option[Episode]): IO[Character] =\n    if (episode.contains(Episode.Empire)) IO(luke)\n    else IO(artoo)\n    \n  def getCharacter(id: String): IO[Option[Character]] =\n    IO(humanData.get(id) orElse droidData.get(id))\n    \n  def getHuman(id: String): IO[Option[Human]] =\n    IO(humanData.get(id))\n    \n  def getDroid(id: String): IO[Option[Droid]] =\n    IO(droidData.get(id))\n}\n')),(0,i.kt)("p",null,"The following GraphQL query will be used to test our schema:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-graphql"},'query {\n  # 1\n  hero(episode: NEWHOPE) {\n    id\n    name\n\n    # 2\n    __typename\n\n    # 3\n    ... on Droid {\n      primaryFunction\n      friends {\n        name\n        __typename\n        appearsIn\n      }\n    }\n    ... HumanDetails\n  }\n  c3po: droid(id: "2000") {\n    name\n  }\n}\n\n# 4\nfragment HumanDetails on Human {\n  homePlanet\n}\n')),(0,i.kt)("p",null,"Some new things are going on in this query:"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},(0,i.kt)("inlineCode",{parentName:"li"},"(episode: NEWHOPE)")," is used to pass arguments to the ",(0,i.kt)("inlineCode",{parentName:"li"},"hero")," field."),(0,i.kt)("li",{parentName:"ol"},"The ",(0,i.kt)("inlineCode",{parentName:"li"},"__typename")," field is used to get the type of the object returned.\nThis field is available on all types and interfaces."),(0,i.kt)("li",{parentName:"ol"},"The ",(0,i.kt)("inlineCode",{parentName:"li"},"... on ")," syntax is used to pattern match on specific types.\nSince the ",(0,i.kt)("inlineCode",{parentName:"li"},"hero")," returns a ",(0,i.kt)("inlineCode",{parentName:"li"},"Character")," interface we must match it to a ",(0,i.kt)("inlineCode",{parentName:"li"},"Droid")," to get the ",(0,i.kt)("inlineCode",{parentName:"li"},"primaryFunction")," field."),(0,i.kt)("li",{parentName:"ol"},"The ",(0,i.kt)("inlineCode",{parentName:"li"},"fragment")," syntax is used to define a reusable block of fields akin to a CTE in SQL.")),(0,i.kt)("p",null,"Now let us introduce the query in scala:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'def query = """\n  query {\n    hero(episode: NEWHOPE) {\n      id\n      name\n      __typename\n      ... on Droid {\n        primaryFunction\n        friends {\n          name\n          __typename\n          appearsIn\n        }\n      }\n      ... HumanDetails\n    }\n    c3po: droid(id: "2000") {\n      name\n    }\n  }\n\n  fragment HumanDetails on Human {\n    homePlanet\n  }\n"""\n')),(0,i.kt)("p",null,"Finally we can parse, plan and evaluate the query:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'import io.circe.syntax._\n(new StarWarsSchema[IO](repo))\n  .makeSchema\n  .map(Compiler[IO].compile(_, query))\n  .flatMap { case Right(Application.Query(run)) => run.map(_.asJson) }\n// {\n//   "data" : {\n//     "c3po" : {\n//       "name" : "C-3PO"\n//     },\n//     "hero" : {\n//       "name" : "R2-D2",\n//       "__typename" : "Droid",\n//       "primaryFunction" : "Astromech",\n//       "id" : "2001",\n//       "friends" : [\n//         {\n//           "__typename" : "Human",\n//           "appearsIn" : [\n//             "NEWHOPE",\n//             "EMPIRE",\n//             "JEDI"\n//           ],\n//           "name" : "Luke Skywalker"\n//         },\n//         {\n//           "__typename" : "Human",\n//           "appearsIn" : [\n//             "NEWHOPE",\n//             "EMPIRE",\n//             "JEDI"\n//           ],\n//           "name" : "Han Solo"\n//         },\n//         {\n//           "__typename" : "Human",\n//           "appearsIn" : [\n//             "NEWHOPE",\n//             "EMPIRE",\n//             "JEDI"\n//           ],\n//           "name" : "Leia Organa"\n//         }\n//       ]\n//     }\n//   }\n// }\n')),(0,i.kt)("p",null,"And that's the end of this tutorial!\nThe docs contain more examples and information about the library, so be sure to check them out."))}m.isMDXComponent=!0}}]);