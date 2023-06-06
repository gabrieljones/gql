"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[80],{3905:(e,n,t)=>{t.d(n,{Zo:()=>c,kt:()=>u});var a=t(7294);function i(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function r(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function o(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?r(Object(t),!0).forEach((function(n){i(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):r(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function l(e,n){if(null==e)return{};var t,a,i=function(e,n){if(null==e)return{};var t,a,i={},r=Object.keys(e);for(a=0;a<r.length;a++)t=r[a],n.indexOf(t)>=0||(i[t]=e[t]);return i}(e,n);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(a=0;a<r.length;a++)t=r[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(i[t]=e[t])}return i}var s=a.createContext({}),p=function(e){var n=a.useContext(s),t=n;return e&&(t="function"==typeof e?e(n):o(o({},n),e)),t},c=function(e){var n=p(e.components);return a.createElement(s.Provider,{value:n},e.children)},d={inlineCode:"code",wrapper:function(e){var n=e.children;return a.createElement(a.Fragment,{},n)}},m=a.forwardRef((function(e,n){var t=e.components,i=e.mdxType,r=e.originalType,s=e.parentName,c=l(e,["components","mdxType","originalType","parentName"]),m=p(t),u=i,f=m["".concat(s,".").concat(u)]||m[u]||d[u]||r;return t?a.createElement(f,o(o({ref:n},c),{},{components:t})):a.createElement(f,o({ref:n},c))}));function u(e,n){var t=arguments,i=n&&n.mdxType;if("string"==typeof e||i){var r=t.length,o=new Array(r);o[0]=m;var l={};for(var s in n)hasOwnProperty.call(n,s)&&(l[s]=n[s]);l.originalType=e,l.mdxType="string"==typeof e?e:i,o[1]=l;for(var p=2;p<r;p++)o[p]=t[p];return a.createElement.apply(null,o)}return a.createElement.apply(null,t)}m.displayName="MDXCreateElement"},288:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>o,default:()=>d,frontMatter:()=>r,metadata:()=>l,toc:()=>p});var a=t(7462),i=(t(7294),t(3905));const r={title:"Output types"},o=void 0,l={unversionedId:"server/schema/output_types",id:"server/schema/output_types",title:"Output types",description:"An output type Out[F[_], A] is an ast node that can take some A as input and produce a graphql value in the effect F.",source:"@site/docs/server/schema/output_types.md",sourceDirName:"server/schema",slug:"/server/schema/output_types",permalink:"/gql/docs/server/schema/output_types",draft:!1,editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/server/schema/output_types.md",tags:[],version:"current",frontMatter:{title:"Output types"},sidebar:"docs",previous:{title:"Getting started",permalink:"/gql/docs/overview/getting_started"},next:{title:"Input types",permalink:"/gql/docs/server/schema/input_types"}},s={},p=[{value:"Scalar",id:"scalar",level:2},{value:"Enum",id:"enum",level:2},{value:"Field",id:"field",level:2},{value:"Type (object)",id:"type-object",level:2},{value:"Union",id:"union",level:2},{value:"Ad-hoc unions",id:"ad-hoc-unions",level:3},{value:"For the daring",id:"for-the-daring",level:3},{value:"Interface",id:"interface",level:2},{value:"A note on interface relationships",id:"a-note-on-interface-relationships",level:3},{value:"Unreachable types",id:"unreachable-types",level:2}],c={toc:p};function d(e){let{components:n,...t}=e;return(0,i.kt)("wrapper",(0,a.Z)({},c,t,{components:n,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"An output type ",(0,i.kt)("inlineCode",{parentName:"p"},"Out[F[_], A]")," is an ast node that can take some ",(0,i.kt)("inlineCode",{parentName:"p"},"A")," as input and produce a graphql value in the effect ",(0,i.kt)("inlineCode",{parentName:"p"},"F"),".\nOutput types act as continuations of their input types, such that a schema effectively is a tree of continuations.\nThe output types of gql are defined in ",(0,i.kt)("inlineCode",{parentName:"p"},"gql.ast")," and are named after their respective GraphQL types."),(0,i.kt)("admonition",{type:"note"},(0,i.kt)("p",{parentName:"admonition"},"Most examples use the ",(0,i.kt)("inlineCode",{parentName:"p"},"dsl")," to construct output types.\nThe types can naturally be constructed manually as well, but this can be verbose.")),(0,i.kt)("p",null,"Lets import the things we need: "),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"import gql.ast._\nimport gql.resolver._\nimport gql.dsl._\nimport gql._\nimport cats._\nimport cats.data._\nimport cats.implicits._\nimport cats.effect._\n")),(0,i.kt)("h2",{id:"scalar"},"Scalar"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Scalar")," types are composed of a name, an encoder and a decoder.\nThe ",(0,i.kt)("inlineCode",{parentName:"p"},"Scalar")," type can encode ",(0,i.kt)("inlineCode",{parentName:"p"},"A => Value")," and decode ",(0,i.kt)("inlineCode",{parentName:"p"},"Value => Either[Error, A]"),".\nA ",(0,i.kt)("inlineCode",{parentName:"p"},"Value")," is a graphql value, which is a superset of json."),(0,i.kt)("p",null,"gql comes with a few predefined scalars, but you can also define your own.\nFor instance, the ",(0,i.kt)("inlineCode",{parentName:"p"},"ID")," type is defined for any ",(0,i.kt)("inlineCode",{parentName:"p"},"Scalar")," as follows:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'final case class ID[A](value: A)\n\nobject ID {\n  implicit def idTpe[A](implicit s: Scalar[A]): Scalar[ID[A]] =\n    s.imap(ID(_))(_.value)\n      .rename("ID")\n      .document(\n        """|The `ID` scalar type represents a unique identifier, often used to refetch an object or as key for a cache.\n           |The ID type appears in a JSON response as a String; however, it is not intended to be human-readable.\n           |When expected as an input type, any string (such as `\\"4\\"`) or integer (such as `4`) input value will be accepted as an ID."""".stripMargin\n      )\n}\n  \nimplicitly[Scalar[ID[String]]]\n// res0: Scalar[ID[String]] = Scalar(\n//   name = "ID",\n//   encoder = scala.Function1$$Lambda$10352/0x0000000102d6c840@66c0b862,\n//   decoder = scala.Function1$$Lambda$10041/0x0000000102c1d840@52d04462,\n//   description = Some(\n//     value = """The `ID` scalar type represents a unique identifier, often used to refetch an object or as key for a cache.\n// The ID type appears in a JSON response as a String; however, it is not intended to be human-readable.\n// When expected as an input type, any string (such as `\\"4\\"`) or integer (such as `4`) input value will be accepted as an ID.""""\n//   )\n// )\n')),(0,i.kt)("h2",{id:"enum"},"Enum"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Enum")," types, like ",(0,i.kt)("inlineCode",{parentName:"p"},"Scalar")," types, are terminal types that consist of a name and non-empty bi-directional mapping from a scala type to a ",(0,i.kt)("inlineCode",{parentName:"p"},"String"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'sealed trait Color\nobject Color {\n  case object Red extends Color\n  case object Green extends Color\n  case object Blue extends Color\n}\n\nenumType[Color](\n  "Color",\n  "RED" -> enumVal(Color.Red),\n  "GREEN" -> enumVal(Color.Green),\n  "BLUE" -> enumVal(Color.Blue)\n)\n// res1: Enum[Color] = Enum(\n//   name = "Color",\n//   mappings = NonEmptyList(\n//     head = ("RED", EnumValue(value = Red, description = None)),\n//     tail = List(\n//       ("GREEN", EnumValue(value = Green, description = None)),\n//       ("BLUE", EnumValue(value = Blue, description = None))\n//     )\n//   ),\n//   description = None\n// )\n')),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Enum")," types have no constraints on the values they can encode or decode, so they can in fact, be dynamically typed:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'final case class UntypedEnum(s: String)\n\nenumType[UntypedEnum](\n  "UntypedEnum",\n  "FIRST" -> enumVal(UntypedEnum("FIRST"))\n)\n// res2: Enum[UntypedEnum] = Enum(\n//   name = "UntypedEnum",\n//   mappings = NonEmptyList(\n//     head = (\n//       "FIRST",\n//       EnumValue(value = UntypedEnum(s = "FIRST"), description = None)\n//     ),\n//     tail = List()\n//   ),\n//   description = None\n// )\n')),(0,i.kt)("admonition",{type:"caution"},(0,i.kt)("p",{parentName:"admonition"},"Encoding a value that has not been defined in the enum will result in a GraphQL error.\nTherefore, it is recommended to enumerate the image of the enum; only use ",(0,i.kt)("inlineCode",{parentName:"p"},"sealed trait"),"s")),(0,i.kt)("h2",{id:"field"},"Field"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Field")," is a type that represents a field in a graphql ",(0,i.kt)("inlineCode",{parentName:"p"},"type")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"interface"),".\nA ",(0,i.kt)("inlineCode",{parentName:"p"},"Field[F, I, T]")," contains a continuation ",(0,i.kt)("inlineCode",{parentName:"p"},"Out[F, T]")," and a resolver that takes ",(0,i.kt)("inlineCode",{parentName:"p"},"I")," to ",(0,i.kt)("inlineCode",{parentName:"p"},"F[T]"),".\nField also lazily captures ",(0,i.kt)("inlineCode",{parentName:"p"},"Out[F, T]"),", to allow recursive types.\nThe ",(0,i.kt)("inlineCode",{parentName:"p"},"dsl")," functions also lazily capture ",(0,i.kt)("inlineCode",{parentName:"p"},"Out[F, T]")," definitions as implicit parameters."),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},"Check out the ",(0,i.kt)("a",{parentName:"p",href:"/gql/docs/server/schema/resolvers"},"resolver section")," for more info on how resolvers work.")),(0,i.kt)("h2",{id:"type-object"},"Type (object)"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Type")," is the gql equivalent of ",(0,i.kt)("inlineCode",{parentName:"p"},"type")," in GraphQL parlance.\nA ",(0,i.kt)("inlineCode",{parentName:"p"},"Type")," consists of a name and a non-empty list of fields."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'final case class Domain(\n  name: String,\n  amount: Int\n)\n\nType[IO, Domain](\n  "Domain",\n  NonEmptyList.of(\n    "name" -> Field[IO, Domain, String](\n      Resolver.lift(_.name),\n      Eval.now(stringScalar)\n    ),\n    "amount" -> Field[IO, Domain, Int](\n      Resolver.lift(_.amount),\n      Eval.now(intScalar)\n    )\n  ),\n  Nil\n)\n// res3: Type[IO, Domain] = Type(\n//   name = "Domain",\n//   fields = NonEmptyList(\n//     head = (\n//       "name",\n//       Field(\n//         resolve = gql.resolver.Resolver@5cdf5491,\n//         output = Now(\n//           value = Scalar(\n//             name = "String",\n//             encoder = gql.ast$Scalar$$$Lambda$10637/0x0000000102e5f040@55bd2a7e,\n//             decoder = gql.ast$Scalar$$$Lambda$10638/0x0000000102e5e840@36dd8b03,\n//             description = Some(\n//               value = "The `String` is a UTF-8 character sequence usually representing human-readable text."\n//             )\n//           )\n//         ),\n//         description = None\n//       )\n//     ),\n//     tail = List(\n//       (\n//         "amount",\n//         Field(\n//           resolve = gql.resolver.Resolver@667e2aee,\n//           output = Now(\n//             value = Scalar(\n//               name = "Int",\n//               encoder = gql.ast$Scalar$$$Lambda$10637/0x0000000102e5f040@40a3816c,\n//               decoder = gql.ast$Scalar$$$Lambda$10638/0x0000000102e5e840@7cf1addc,\n//               description = Some(\n//                 value = "The `Int` scalar type represents non-fractional signed whole numeric values. Int can represent values between -(2^31) and 2^31 - 1."\n//               )\n//             )\n//           ),\n//           description = None\n//         )\n//       )\n//     )\n//   ),\n//   implementations = List(),\n//   description = None\n// )\n')),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Type"),"s look very rough, but are significantly easier to define with the ",(0,i.kt)("inlineCode",{parentName:"p"},"dsl"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'tpe[IO, Domain](\n  "Domain",\n  "name" -> lift(_.name),\n  "amount" -> lift(_.amount)\n)\n// res4: Type[IO, Domain] = Type(\n//   name = "Domain",\n//   fields = NonEmptyList(\n//     head = (\n//       "name",\n//       Field(\n//         resolve = gql.resolver.Resolver@26aeb4d2,\n//         output = cats.Later@3110d3b3,\n//         description = None\n//       )\n//     ),\n//     tail = List(\n//       (\n//         "amount",\n//         Field(\n//           resolve = gql.resolver.Resolver@6d649c1c,\n//           output = cats.Later@1c0a5321,\n//           description = None\n//         )\n//       )\n//     )\n//   ),\n//   implementations = List(),\n//   description = None\n// )\n')),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},"It is highly reccomended to define all ",(0,i.kt)("inlineCode",{parentName:"p"},"Type"),"s, ",(0,i.kt)("inlineCode",{parentName:"p"},"Union"),"s and ",(0,i.kt)("inlineCode",{parentName:"p"},"Interface"),"s as either ",(0,i.kt)("inlineCode",{parentName:"p"},"val")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"lazy val"),".")),(0,i.kt)("h2",{id:"union"},"Union"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Union")," types allow unification of arbitary types.\nThe ",(0,i.kt)("inlineCode",{parentName:"p"},"Union")," type defines a set of ",(0,i.kt)("inlineCode",{parentName:"p"},"PartialFunction"),"s that can specify the the type."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'sealed trait Animal\nfinal case class Dog(name: String) extends Animal\nfinal case class Cat(name: String) extends Animal\n\nimplicit lazy val dog = tpe[IO, Dog](\n  "Dog",\n  "name" -> lift(_.name)\n)\n\nimplicit lazy val cat = tpe[IO, Cat](\n  "Cat",\n  "name" -> lift(_.name)\n)\n\nunion[IO, Animal]("Animal")\n  .variant{ case x: Dog => x }\n  .subtype[Cat]\n// res5: Union[IO, Animal] = Union(\n//   name = "Animal",\n//   types = NonEmptyList(\n//     head = Variant(tpe = cats.Later@4fd17c56),\n//     tail = List(Variant(tpe = cats.Later@78eba354))\n//   ),\n//   description = None\n// )\n')),(0,i.kt)("admonition",{type:"caution"},(0,i.kt)("p",{parentName:"admonition"},"Defining instances for ",(0,i.kt)("inlineCode",{parentName:"p"},"Animal")," that are not referenced in the gql type is mostly safe, since any spread will simple give no fields.\nMost GraphQL clients also handle this case gracefully, for backwards compatibility reasons.")),(0,i.kt)("h3",{id:"ad-hoc-unions"},"Ad-hoc unions"),(0,i.kt)("p",null,"In the true spirit of unification, ",(0,i.kt)("inlineCode",{parentName:"p"},"Union")," types can be constructed in a more ad-hoc fashion:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'final case class Entity1(value: String)\nfinal case class Entity2(value: String)\n\nsealed trait Unification\nobject Unification {\n  final case class E1(value: Entity1) extends Unification\n  final case class E2(value: Entity2) extends Unification\n}\n\nimplicit lazy val entity1: Type[IO, Entity1] = ???\n\nimplicit lazy val entity2: Type[IO, Entity2] = ???\n\nunion[IO, Unification]("Unification")\n  .variant{ case Unification.E1(value) => value }\n  .variant{ case Unification.E2(value) => value }\n// res6: Union[IO, Unification] = Union(\n//   name = "Unification",\n//   types = NonEmptyList(\n//     head = Variant(tpe = cats.Later@31a65429),\n//     tail = List(Variant(tpe = cats.Later@7f7b8a67))\n//   ),\n//   description = None\n// )\n')),(0,i.kt)("h3",{id:"for-the-daring"},"For the daring"),(0,i.kt)("p",null,"Since the specify function is a ",(0,i.kt)("inlineCode",{parentName:"p"},"PartialFunction"),", it is indeed possible to have no unifying type:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'union[IO, Any]("AnyUnification")\n  .variant{ case x: Entity1 => x }\n  .variant{ case x: Entity2 => x }\n// res7: Union[IO, Any] = Union(\n//   name = "AnyUnification",\n//   types = NonEmptyList(\n//     head = Variant(tpe = cats.Later@3b0c6a12),\n//     tail = List(Variant(tpe = cats.Later@1ed88f24))\n//   ),\n//   description = None\n// )\n')),(0,i.kt)("p",null,"And also complex routing logic:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'union[IO, Unification]("RoutedUnification")\n  .variant{ case Unification.E1(x) if x.value == "Jane" => x }\n  .variant{ \n    case Unification.E1(x) => Entity2(x.value)\n    case Unification.E2(x) => x\n  }\n// res8: Union[IO, Unification] = Union(\n//   name = "RoutedUnification",\n//   types = NonEmptyList(\n//     head = Variant(tpe = cats.Later@1d5c87ca),\n//     tail = List(Variant(tpe = cats.Later@3e571eb4))\n//   ),\n//   description = None\n// )\n')),(0,i.kt)("h2",{id:"interface"},"Interface"),(0,i.kt)("p",null,"An interface is a ",(0,i.kt)("inlineCode",{parentName:"p"},"Type"),' that can be "implemented".'),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Interface"),"s have abstract fields, that are very much like fields in ",(0,i.kt)("inlineCode",{parentName:"p"},"Type"),"s.\n",(0,i.kt)("inlineCode",{parentName:"p"},"Interface"),"s can also be implemented by other ",(0,i.kt)("inlineCode",{parentName:"p"},"Type"),"s and ",(0,i.kt)("inlineCode",{parentName:"p"},"Interface"),"s.\n",(0,i.kt)("inlineCode",{parentName:"p"},"Interface"),"s don't declare their implementations, but rather the implementations declare their interfaces."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'sealed trait Node {\n def id: String\n}\n\nfinal case class Person(\n  name: String,\n  id: String\n) extends Node\n\nfinal case class Company(\n  name: String,\n  id: String\n) extends Node\n  \nimplicit lazy val node = interface[IO, Node](\n  "Node",\n  "id" -> abst[IO, ID[String]]\n)\n\nlazy val person = tpe[IO, Person](\n  "Person",\n  "name" -> lift(_.name),\n  "id" -> lift(x => ID(x.id))\n).implements[Node]{ case x: Person => x }\n  \nlazy val company = tpe[IO, Company](\n  "Company",\n  "name" -> lift(_.name),\n  "id" -> lift(x => ID(x.id))\n).subtypeOf[Node]\n')),(0,i.kt)("h3",{id:"a-note-on-interface-relationships"},"A note on interface relationships"),(0,i.kt)("admonition",{type:"info"},(0,i.kt)("p",{parentName:"admonition"},"This sub-section is a bit of a philosophical digression and can be skipped.")),(0,i.kt)("p",null,"The nature of the ",(0,i.kt)("inlineCode",{parentName:"p"},"Interface"),' type unfortunately causes some complications.\nSince a relation goes from implementation to interface, cases of ambiguity can arise of what interface to consider the "truth".\nSchema validation will catch such cases, but it can still feel like a somewhat arbitrary limitation.'),(0,i.kt)("p",null,"One could argue that the relation could simple be inverted, like unions, but alas such an endeavour has another consequence.\nConceptually an interface is defined most generally (in a core library or a most general purpose module), where implementations occur in more specific places.\nInverting the relationships of the interface would mean that the interface would have to be defined in the most specific place instead of the most general.\nThat is, inverting the arrows (relationships) of an interface, produces a union instead (with some extra features such as fields)."),(0,i.kt)("p",null,"Now we must define the scala type for the interface in the most general place, but the ",(0,i.kt)("inlineCode",{parentName:"p"},"Interface")," in the most specific?\nConnecting such a graph requires significant effort (exploitation of some laziness) and as such is not the chosen approach."),(0,i.kt)("h2",{id:"unreachable-types"},"Unreachable types"),(0,i.kt)("p",null,"gql discovers types by traversing the schema types.\nThis also means that even if you have a type declared it must occur in the ast to be respected."),(0,i.kt)("p",null,"You might want to declare types that are not yet queryable.\nOr maybe you only expose an interface, but there re no reachable references to any implementing types, thus the implementations won't be discovered."),(0,i.kt)("p",null,'The schema lets you declare "extra" types that should occur in introspection, rendering and evaluation:'),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'def getNode: Node = Company("gql", "1")\n\ndef shape = SchemaShape.unit[IO](fields("node" -> lift(_ => getNode)))\n\nprintln(shape.render)\n// type Query {\n//   node: Node!\n// }\n// \n// interface Node {\n//   id: ID!\n// }\n\ndef withCompany = shape.addOutputTypes(company)\n\nprintln(withCompany.render)\n// type Company implements Node {\n//   name: String!\n//   id: ID!\n// }\n// \n// interface Node {\n//   id: ID!\n// }\n// \n// type Query {\n//   node: Node!\n// }\n\nprintln(withCompany.addOutputTypes(person).render)\n// type Company implements Node {\n//   name: String!\n//   id: ID!\n// }\n// \n// interface Node {\n//   id: ID!\n// }\n// \n// type Query {\n//   node: Node!\n// }\n// \n// type Person implements Node {\n//   name: String!\n//   id: ID!\n// }\n')))}d.isMDXComponent=!0}}]);