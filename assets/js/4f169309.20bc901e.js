"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[381],{3905:(e,n,t)=>{t.d(n,{Zo:()=>m,kt:()=>u});var a=t(7294);function r(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function o(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){r(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function l(e,n){if(null==e)return{};var t,a,r=function(e,n){if(null==e)return{};var t,a,r={},i=Object.keys(e);for(a=0;a<i.length;a++)t=i[a],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(a=0;a<i.length;a++)t=i[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var s=a.createContext({}),p=function(e){var n=a.useContext(s),t=n;return e&&(t="function"==typeof e?e(n):o(o({},n),e)),t},m=function(e){var n=p(e.components);return a.createElement(s.Provider,{value:n},e.children)},d={inlineCode:"code",wrapper:function(e){var n=e.children;return a.createElement(a.Fragment,{},n)}},c=a.forwardRef((function(e,n){var t=e.components,r=e.mdxType,i=e.originalType,s=e.parentName,m=l(e,["components","mdxType","originalType","parentName"]),c=p(t),u=r,g=c["".concat(s,".").concat(u)]||c[u]||d[u]||i;return t?a.createElement(g,o(o({ref:n},m),{},{components:t})):a.createElement(g,o({ref:n},m))}));function u(e,n){var t=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var i=t.length,o=new Array(i);o[0]=c;var l={};for(var s in n)hasOwnProperty.call(n,s)&&(l[s]=n[s]);l.originalType=e,l.mdxType="string"==typeof e?e:r,o[1]=l;for(var p=2;p<i;p++)o[p]=t[p];return a.createElement.apply(null,o)}return a.createElement.apply(null,t)}c.displayName="MDXCreateElement"},3674:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>o,default:()=>d,frontMatter:()=>i,metadata:()=>l,toc:()=>p});var a=t(7462),r=(t(7294),t(3905));const i={title:"Resolvers"},o=void 0,l={unversionedId:"server/schema/resolvers",id:"server/schema/resolvers",title:"Resolvers",description:"Resolvers are at the core of gql; a resolver Resolver[F, I, O] takes an I and produces an O in effect F.",source:"@site/docs/server/schema/resolvers.md",sourceDirName:"server/schema",slug:"/server/schema/resolvers",permalink:"/gql/docs/server/schema/resolvers",draft:!1,editUrl:"https://github.com/valdemargr/gql/tree/main/docs/server/schema/resolvers.md",tags:[],version:"current",frontMatter:{title:"Resolvers"},sidebar:"docs",previous:{title:"The DSL",permalink:"/gql/docs/server/schema/dsl"},next:{title:"The schema",permalink:"/gql/docs/server/schema/"}},s={},p=[{value:"Resolvers",id:"resolvers",level:2},{value:"Lift",id:"lift",level:3},{value:"LiftF",id:"liftf",level:3},{value:"Arguments",id:"arguments",level:3},{value:"Meta",id:"meta",level:3},{value:"Errors",id:"errors",level:3},{value:"First",id:"first",level:3},{value:"Batch",id:"batch",level:3},{value:"Batch resolver syntax",id:"batch-resolver-syntax",level:4},{value:"Batchers from elsewhere",id:"batchers-from-elsewhere",level:4},{value:"Choice",id:"choice",level:3},{value:"Stream",id:"stream",level:3},{value:"Stream semantics",id:"stream-semantics",level:4},{value:"Steps",id:"steps",level:2}],m={toc:p};function d(e){let{components:n,...t}=e;return(0,r.kt)("wrapper",(0,a.Z)({},m,t,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"Resolvers are at the core of gql; a resolver ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, I, O]")," takes an ",(0,r.kt)("inlineCode",{parentName:"p"},"I")," and produces an ",(0,r.kt)("inlineCode",{parentName:"p"},"O")," in effect ",(0,r.kt)("inlineCode",{parentName:"p"},"F"),".\nResolvers are embedded in fields and act as continuations.\nWhen gql executes a query it first constructs a tree of continueations from your schema and the supplied GraphQL query."),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"Resolver"),"s act and compose like functions with combinators such as ",(0,r.kt)("inlineCode",{parentName:"p"},"andThen")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"compose"),"."),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},(0,r.kt)("inlineCode",{parentName:"p"},"Resolver")," forms an ",(0,r.kt)("inlineCode",{parentName:"p"},"Arrow")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"Choice"),".")),(0,r.kt)("p",null,"Lets start off with some imports:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import gql._\nimport gql.dsl._\nimport gql.resolver._\nimport gql.ast._\nimport cats.effect._\nimport cats.implicits._\nimport cats.data._\n")),(0,r.kt)("h2",{id:"resolvers"},"Resolvers"),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"Resolver")," is a collection of high-level combinators that constructs a tree of ",(0,r.kt)("inlineCode",{parentName:"p"},"Step"),"."),(0,r.kt)("admonition",{type:"note"},(0,r.kt)("p",{parentName:"admonition"},"If you are familiar with the relationship between ",(0,r.kt)("inlineCode",{parentName:"p"},"fs2.Stream")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"fs2.Pull"),", then the relationship between ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"Step")," should be familiar.")),(0,r.kt)("h3",{id:"lift"},"Lift"),(0,r.kt)("p",null,"The simplest ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver")," type is ",(0,r.kt)("inlineCode",{parentName:"p"},"lift")," which simply lifts a function ",(0,r.kt)("inlineCode",{parentName:"p"},"I => O")," into ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, I, O]"),".\n",(0,r.kt)("inlineCode",{parentName:"p"},"lift"),"'s method form is ",(0,r.kt)("inlineCode",{parentName:"p"},"map"),", which for any resolver ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, I, O]")," produces a new resolver ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, I, O2]")," given a function ",(0,r.kt)("inlineCode",{parentName:"p"},"O => O2"),"."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"val r = Resolver.lift[IO, Int](_.toLong)\n// r: Resolver[IO, Int, Long] = gql.resolver.Resolver@521faf40\nr.map(_.toString())\n// res0: Resolver[IO, Int, String] = gql.resolver.Resolver@2abcae4f\n")),(0,r.kt)("h3",{id:"liftf"},"LiftF"),(0,r.kt)("p",null,"Another simple resolver is ",(0,r.kt)("inlineCode",{parentName:"p"},"liftF")," which lifts a function ",(0,r.kt)("inlineCode",{parentName:"p"},"I => F[O]")," into ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, I, O]"),".\n",(0,r.kt)("inlineCode",{parentName:"p"},"liftF"),"'s method form is ",(0,r.kt)("inlineCode",{parentName:"p"},"evalMap"),"."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"val r = Resolver.liftF[IO, Int](i => IO(i.toLong))\n// r: Resolver[IO, Int, Long] = gql.resolver.Resolver@10344954\nr.evalMap(l => IO(l.toString()))\n// res1: Resolver[[x]IO[x], Int, String] = gql.resolver.Resolver@202a9cc4\n")),(0,r.kt)("h3",{id:"arguments"},"Arguments"),(0,r.kt)("p",null,"Arguments in gql are provided through resolvers.\nA resolver ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, I, A]")," can be constructed from an argument ",(0,r.kt)("inlineCode",{parentName:"p"},"Arg[A]"),", through either ",(0,r.kt)("inlineCode",{parentName:"p"},"argument")," or ",(0,r.kt)("inlineCode",{parentName:"p"},"arg")," in method form."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'lazy val ageArg = arg[Int]("age")\nval r = Resolver.argument[IO, Nothing, String](arg[String]("name"))\n// r: Resolver[IO, Nothing, String] = gql.resolver.Resolver@f60422e\nval r2 = r.arg(ageArg)\n// r2: Resolver[IO, Nothing, (Int, String)] = gql.resolver.Resolver@40e14b5b\nr2.map{ case (age, name) => s"$name is $age years old" }\n// res2: Resolver[IO, Nothing, String] = gql.resolver.Resolver@72adc4fa\n')),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"Arg")," also has an applicative defined for it, so multi-argument resolution can be simplified to:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'val r = Resolver.argument[IO, Nothing, (String, Int)](\n  (arg[String]("name"), arg[Int]("age")).tupled\n)\n// r: Resolver[IO, Nothing, (String, Int)] = gql.resolver.Resolver@695aad7\nr.map{ case (age, name) => s"$name is $age years old" }\n// res3: Resolver[IO, Nothing, String] = gql.resolver.Resolver@b69e919\n')),(0,r.kt)("h3",{id:"meta"},"Meta"),(0,r.kt)("p",null,"The ",(0,r.kt)("inlineCode",{parentName:"p"},"meta")," resolver provides metadata regarding query execution, such as the position of query execution, field aliasing and the provided arguments."),(0,r.kt)("h3",{id:"errors"},"Errors"),(0,r.kt)("p",null,"Well formed errors are returned in an ",(0,r.kt)("inlineCode",{parentName:"p"},"cats.data.Ior"),"."),(0,r.kt)("admonition",{type:"info"},(0,r.kt)("p",{parentName:"admonition"},"An ",(0,r.kt)("inlineCode",{parentName:"p"},"Ior")," is a non-exclusive ",(0,r.kt)("inlineCode",{parentName:"p"},"Either"),".")),(0,r.kt)("p",null,"The ",(0,r.kt)("inlineCode",{parentName:"p"},"Ior")," datatype's left side must be ",(0,r.kt)("inlineCode",{parentName:"p"},"String")," and acts as an optional error that will be present in the query result.\ngql can return an error and a result for the same path, given that ",(0,r.kt)("inlineCode",{parentName:"p"},"Ior")," has both left and right side defined."),(0,r.kt)("p",null,"Errors are embedded into resolvers via ",(0,r.kt)("inlineCode",{parentName:"p"},"rethrow"),".\nThe extension method ",(0,r.kt)("inlineCode",{parentName:"p"},"rethrow")," is present on any resolver of type ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, I, Ior[String, O]]"),":"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'val r = Resolver.lift[IO, Int](i => Ior.Both("I will be in the errors :)", i))\n// r: Resolver[IO, Int, Ior.Both[String, Int]] = gql.resolver.Resolver@4118d0bc\nr.rethrow\n// res4: Resolver[[A]IO[A], Int, Int] = gql.resolver.Resolver@5d0e4671\n')),(0,r.kt)("p",null,"We can also use ",(0,r.kt)("inlineCode",{parentName:"p"},"emap")," to map the current value into an ",(0,r.kt)("inlineCode",{parentName:"p"},"Ior"),"."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'val r = Resolver.id[IO, Int].emap(i => Ior.Both("I will be in the errors :)", i))\n// r: Resolver[IO, Int, Int] = gql.resolver.Resolver@76668c15\n')),(0,r.kt)("h3",{id:"first"},"First"),(0,r.kt)("p",null,"A ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver")," also implements ",(0,r.kt)("inlineCode",{parentName:"p"},"first")," (",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, A, B] => Resolver[F, (A, C), (B, C)]"),") which is very convinient since some ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver"),"s are constant functions (they throw away their inputs ",(0,r.kt)("inlineCode",{parentName:"p"},"I"),")."),(0,r.kt)("p",null,"Since a ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver")," does not form a ",(0,r.kt)("inlineCode",{parentName:"p"},"Monad"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"first")," is necessary to implement non-trivial resolver compositions.\nIn general, this allows us to trace a value through another resolver composition."),(0,r.kt)("p",null,"For instance, resolving an argument will ignore the input of the resolver, which is not always the desired semantics."),(0,r.kt)("p",null,"Or maybe your program contains some a general resolver compositon that is used many places, like say verifying credentials, but you'd like to trace a value through it without having to keep track of tupling output with input."),(0,r.kt)("p",null,"Assume we'd like to implement a resolver, that when given a name, can get a list of friends of the person with that name."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'case class PersonId(value: Int)\n\ncase class Person(id: PersonId, name: String)\n\ndef getFriends(id: PersonId, limit: Int): IO[List[Person]] = ???\n\ndef getPerson(name: String): IO[Person] = ???\n\ndef getPersonResolver = Resolver.liftF[IO, String](getPerson)\n\ndef limitResolver = Resolver.argument[IO, Person, Int](arg[Int]("limit"))\n\ndef limitArg = arg[Int]("limit")\ngetPersonResolver\n  .arg(limitArg)\n  .evalMap{ case (limit, p) => getFriends(p.id, limit) }\n// res5: Resolver[[x]IO[x], String, List[Person]] = gql.resolver.Resolver@328382e1\n')),(0,r.kt)("h3",{id:"batch"},"Batch"),(0,r.kt)("p",null,"Like most other GraphQL implementations, gql also supports batching."),(0,r.kt)("p",null,"Unlike most other GraphQL implementations, gql's batching implementation features a global query planner that lets gql delay field execution until it can be paired with another field."),(0,r.kt)("p",null,"Batch declaration and usage occurs as follows:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"Declare a function ",(0,r.kt)("inlineCode",{parentName:"li"},"Set[K] => F[Map[K, V]]"),"."),(0,r.kt)("li",{parentName:"ul"},"Give this function to gql and get back a ",(0,r.kt)("inlineCode",{parentName:"li"},"Resolver[F, Set[K], Map[K, V]]")," in a ",(0,r.kt)("inlineCode",{parentName:"li"},"State")," monad (for unique id generation)."),(0,r.kt)("li",{parentName:"ul"},"Use this new resolver where you want batching.")),(0,r.kt)("p",null,"And now put into practice:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"def getPeopleFromDB(ids: Set[PersonId]): IO[List[Person]] = ???\n\nResolver.batch[IO, PersonId, Person]{ keys => \n  getPeopleFromDB(keys).map(_.map(x => x.id -> x).toMap)\n}\n// res6: State[SchemaState[IO], Resolver[IO, Set[PersonId], Map[PersonId, Person]]] = cats.data.IndexedStateT@3599393d\n")),(0,r.kt)("p",null,"Whenever gql sees this resolver in any composition, it will look for similar resolvers during query planning."),(0,r.kt)("p",null,"Note, however, that you should only declare each batch resolver variant ",(0,r.kt)("strong",{parentName:"p"},"once"),", that is, you should build your schema in ",(0,r.kt)("inlineCode",{parentName:"p"},"State"),".\ngql consideres different batch instantiations incompatible regardless of any type information."),(0,r.kt)("p",null,"State has ",(0,r.kt)("inlineCode",{parentName:"p"},"Monad")," (and transitively ",(0,r.kt)("inlineCode",{parentName:"p"},"Applicative"),") defined for it, so it composes well.\nHere is an example of multiple batchers:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"def b1 = Resolver.batch[IO, Int, Person](_ => ???)\ndef b2 = Resolver.batch[IO, Int, String](_ => ???)\n\n(b1, b2).tupled\n// res7: State[SchemaState[IO], (Resolver[IO, Set[Int], Map[Int, Person]], Resolver[IO, Set[Int], Map[Int, String]])] = cats.data.IndexedStateT@1a0bcc31\n")),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"Even if your field doesn't benefit from batching, batching can still do duplicate key elimination.")),(0,r.kt)("h4",{id:"batch-resolver-syntax"},"Batch resolver syntax"),(0,r.kt)("p",null,"When a resolver in a very specific form ",(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, Set[K], Map[K, V]]"),", then the gql dsl provides some helper methods.\nFor instance, it is very likely that a batcher is embedded in a singular context (",(0,r.kt)("inlineCode",{parentName:"p"},"K => V"),").\nHere is a showcase of some of the helper methods:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'def pb: Resolver[IO, Set[Int], Map[Int, Person]] = \n  // Stub implementation\n  Resolver.lift(_ => Map.empty)\n\n// None if a key is missing\npb.optionals[List]\n// res8: Resolver[[A]IO[A], List[Int], List[Option[Person]]] = gql.resolver.Resolver@584ffd41\n\n// Emits all values\npb.values[List]\n// res9: Resolver[[A]IO[A], List[Int], List[Person]] = gql.resolver.Resolver@7675f823\n\n// Every key must have an associated value\n// or else raise an error via a custom show-like typeclass\nimplicit lazy val showMissingPersonId =\n  ShowMissingKeys.showForKey[Int]("not all people could be found")\npb.force[List]\n// res10: Resolver[[A]IO[A], List[Int], List[Person]] = gql.resolver.Resolver@ad97701\n\n// Maybe there is one value for one key?\npb.optional\n// res11: Resolver[[A]IO[A], Int, Option[Person]] = gql.resolver.Resolver@4e77482f\n\n// Same as optional\npb.optionals[cats.Id]\n// res12: Resolver[[A]IO[A], cats.package.Id[Int], cats.package.Id[Option[Person]]] = gql.resolver.Resolver@69751155\n\n// There is always one value for one key\npb.forceOne\n// res13: Resolver[[A]IO[A], Int, Person] = gql.resolver.Resolver@301858b8\n\n// Same as force but for non-empty containers\npb.forceNE[NonEmptyList]\n// res14: Resolver[[A]IO[A], NonEmptyList[Int], NonEmptyList[Person]] = gql.resolver.Resolver@713840c5\n')),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"For larger programs, consider declaring all your batchers up-front and putting them into some type of collection:"),(0,r.kt)("pre",{parentName:"admonition"},(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"case class MyBatchers(\n  personBatcher: Resolver[IO, Set[Int], Map[Int, Person]],\n  intStringBatcher: Resolver[IO, Set[Int], Map[Int, String]]\n)\n\n(b1, b2).mapN(MyBatchers.apply)\n// res15: State[SchemaState[IO], MyBatchers] = cats.data.IndexedStateT@5fa48fd\n")),(0,r.kt)("p",{parentName:"admonition"},"For most batchers it is likely that you eventually want to pre-compose them in various ways, for instance requsting args, which this pattern promotes.")),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"Sometimes you have multiple groups of fields in the same object where each group have different performance overheads."),(0,r.kt)("p",{parentName:"admonition"},"Say you had a ",(0,r.kt)("inlineCode",{parentName:"p"},"Person")," object in your database.\nThis ",(0,r.kt)("inlineCode",{parentName:"p"},"Person")," object also exists in a remote api.\nThis remote api can tell you, the friends of a ",(0,r.kt)("inlineCode",{parentName:"p"},"Person")," given the object's id and name."),(0,r.kt)("pre",{parentName:"admonition"},(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'def pureFields = fields[IO, PersonId](\n  "id" -> lift(id => id)\n)\n\ncase class PersonDB(\n  id: PersonId, \n  name: String, \n  remoteApiId: String\n)\n\n// SELECT id, name, remote_api_id FROM person WHERE id in (...)\ndef dbBatchResolver: Resolver[IO, PersonId, PersonDB] = ???\n\ndef dbFields = fields[IO, PersonDB](\n  "name" -> lift(_.name),\n  "apiId" -> lift(_.remoteApiId)\n)\n\ncase class PersonRemoteAPI(\n  id: PersonId, \n  friends: List[PersonId]\n)\n\n// Given a PersonDB we can call the api (via a batched GET or something)\ndef personBatchResolver: Resolver[IO, PersonDB, PersonRemoteAPI] = ???\n\ndef remoteApiFields = fields[IO, PersonRemoteAPI](\n  "friends" -> lift(_.friends)\n)\n\n// Given a PersonDB object we have the following fields\ndef dbFields2: Fields[IO, PersonDB] = \n  remoteApiFields.compose(personBatchResolver) ::: dbFields\n\n// Given a PersonId we have every field\n// If "friends" is selected, gql will first run `dbBatchResolver` and then `personBatchResolver`\ndef allFields = dbFields2.compose(dbBatchResolver) ::: pureFields\n\nimplicit def person: Type[IO, PersonId] = tpeNel[IO, PersonId](\n  "Person",\n  allFields\n)\n')),(0,r.kt)("p",{parentName:"admonition"},"The general pattern for this decomposition revolves around figuring out what the most basic description of your object is.\nIn this example, every fields can (eventually through various side-effects) be resolved from just ",(0,r.kt)("inlineCode",{parentName:"p"},"PersonId"),"."),(0,r.kt)("p",{parentName:"admonition"},"Notice that even if we had many more fields, the composition overhead remains constant.")),(0,r.kt)("h4",{id:"batchers-from-elsewhere"},"Batchers from elsewhere"),(0,r.kt)("p",null,"Most batching implementations have compatible signatures and can be adapted into a gql batcher."),(0,r.kt)("p",null,"For instance, converting ",(0,r.kt)("inlineCode",{parentName:"p"},"fetch")," to gql:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import fetch._\nobject People extends Data[PersonId, Person] {\n  def name = "People"\n\n  def source: DataSource[IO, PersonId, Person] = ???\n}\n\nResolver\n  .batch[IO, PersonId, Person](_.toList.toNel.traverse(People.source.batch).map(_.getOrElse(Map.empty)))\n// res16: State[SchemaState[IO], Resolver[IO, Set[PersonId], Map[PersonId, Person]]] = cats.data.IndexedStateT@44441eae\n')),(0,r.kt)("h3",{id:"choice"},"Choice"),(0,r.kt)("p",null,"Resolvers also implement ",(0,r.kt)("inlineCode",{parentName:"p"},"Choice")," via ",(0,r.kt)("inlineCode",{parentName:"p"},"(Resolver[F, A, C], Resolver[F, B, D]) => Resolver[F, Either[A, B], Either[C, D]]"),".\nOn the surface, this combinator may have limited uses, but with a bit of composition we can perform tasks such as caching."),(0,r.kt)("p",null,"For instance, a combinator derived from ",(0,r.kt)("inlineCode",{parentName:"p"},"Choice")," is ",(0,r.kt)("inlineCode",{parentName:"p"},"skippable: Resolver[F, I, O] => Resolver[F, Either[I, O], O]"),', which acts as a variant of "caching".\nIf the right side is present we skip the underlying resolver (',(0,r.kt)("inlineCode",{parentName:"p"},"Resolver[F, I, O]"),") altogether."),(0,r.kt)("p",null,"More refined variants of this combinator also exist:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'def getPersonForId: Resolver[IO, PersonId, Person] = ???\n\ntype CachedPerson = Either[PersonId, Person]\ndef cachedPerson = tpe[IO, CachedPerson](\n  "Person",\n  "id" -> lift(_.map(_.id).merge.value),\n  "name" -> build[IO, CachedPerson](_.skipThat(getPersonForId).map(_.name))\n)\n')),(0,r.kt)("p",null,"We can also use some of the ",(0,r.kt)("inlineCode",{parentName:"p"},"compose")," tricks from the ",(0,r.kt)("a",{parentName:"p",href:"#batch-resolver-syntax"},"batch resolver syntax section")," if we have a lot of fields that depend on ",(0,r.kt)("inlineCode",{parentName:"p"},"Person"),". "),(0,r.kt)("admonition",{type:"note"},(0,r.kt)("p",{parentName:"admonition"},"The query planner treats the choice branches as parallel, such that for two instances of a choice, resolvers in the two branches may be batched together.")),(0,r.kt)("h3",{id:"stream"},"Stream"),(0,r.kt)("p",null,"The stream resolver embeds an ",(0,r.kt)("inlineCode",{parentName:"p"},"fs2.Stream")," and provides the ability to emit a stream of results for a graphql subscription."),(0,r.kt)("h4",{id:"stream-semantics"},"Stream semantics"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"When one or more streams emit, the interpreter will re-evaluate the query from the position that emitted.\nThat is, only the sub-tree that changed will be re-interpreted."),(0,r.kt)("li",{parentName:"ul"},"If two streams emit and one occurs as a child of the other, the child will be ignored since it will be replaced."),(0,r.kt)("li",{parentName:"ul"},"By default, the interpreter will only respect the most-recent emitted data.")),(0,r.kt)("p",null,"This means that by default, gql assumes that your stream should behave like a signal, not sequentially.\nHowever, gql can also adhere sequential semantics."),(0,r.kt)("p",null,"For instance a schema designed like the following, emits incremental updates regarding the price for some symbol:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-graphql"},"type PriceChange {\n  difference: Float!\n}\n\ntype Subscription {\n  priceChanges(symbolId: ID!): PriceChange!\n}\n")),(0,r.kt)("p",null,"And here is a schema that represents an api that emits updates regarding the current price of a symbol:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-graphql"},"type SymbolState {\n  price: Float!\n}\n\ntype Subscription {\n  price(symbolId: ID!): SymbolState!\n}\n")),(0,r.kt)("p",null,"Consider the following example where two different evaluation semantics are displayed:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"case class PriceChange(difference: Float)\ndef priceChanges(symbolId: String): fs2.Stream[IO, PriceChange] = ???\n\ncase class SymbolState(price: Float)\ndef price(symbolId: String): fs2.Stream[IO, SymbolState] = ???\n\ndef priceChangesResolver = Resolver.id[IO, String].sequentialStreamMap(priceChanges)\n\ndef priceResolver = Resolver.id[IO, String].streamMap(price)\n")),(0,r.kt)("p",null,"If your stream is sequential, gql will only pull elements when they are needed."),(0,r.kt)("p",null,"The interpreter performs a global re-interpretation of your schema, when one or more streams emit.\nThat is, the interpreter cycles through the following two phases:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"Interpret for the current values."),(0,r.kt)("li",{parentName:"ul"},"Await new values (and values that arrived furing the previous step).")),(0,r.kt)("p",null,"Here is an example of some streams in action:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import scala.concurrent.duration._\nimport cats.effect.unsafe.implicits.global\n\ncase class Streamed(value: Int)\n\nimplicit lazy val streamed: Type[IO, Streamed] = tpe[IO, Streamed](\n  "Streamed",\n  "value" -> build[IO, Streamed](_.streamMap{ s =>\n    fs2.Stream\n      .bracket(IO(println(s"allocating $s")))(_ => IO(println(s"releasing $s"))) >>\n      fs2.Stream\n        .iterate(0)(_ + 1)\n        .evalTap(n => IO(println(s"emitting $n for $s")))\n        .meteredStartImmediately(((5 - s.value) * 20).millis)\n        .as(Streamed(s.value + 1))\n  })\n)\n\ndef query = """\n  subscription {\n    streamed {\n      value {\n        value { \n          value {\n            __typename\n          }\n        }\n      }\n    }\n  }\n"""\n\ndef schema = SchemaShape.unit[IO](\n  fields("ping" -> lift(_ => "pong")),\n  subscription = Some(fields("streamed" -> lift(_ => Streamed(0))))\n)\n\nSchema.simple(schema)\n  .map(Compiler[IO].compile(_, query))\n  .flatMap { case Right(Application.Subscription(stream)) => stream.take(4).compile.drain }\n  .unsafeRunSync()\n// allocating Streamed(0)\n// emitting 0 for Streamed(0)\n// allocating Streamed(1)\n// emitting 0 for Streamed(1)\n// allocating Streamed(2)\n// emitting 0 for Streamed(2)\n// emitting 1 for Streamed(0)\n// emitting 1 for Streamed(2)\n// emitting 1 for Streamed(1)\n// allocating Streamed(1)\n// emitting 0 for Streamed(1)\n// allocating Streamed(2)\n// emitting 0 for Streamed(2)\n// emitting 2 for Streamed(2)\n// emitting 2 for Streamed(1)\n// emitting 2 for Streamed(0)\n// allocating Streamed(2)\n// emitting 0 for Streamed(2)\n// emitting 1 for Streamed(2)\n// emitting 3 for Streamed(2)\n// emitting 1 for Streamed(1)\n// allocating Streamed(2)\n// allocating Streamed(1)\n// emitting 0 for Streamed(2)\n// emitting 3 for Streamed(1)\n// emitting 0 for Streamed(1)\n// allocating Streamed(2)\n// emitting 1 for Streamed(2)\n// emitting 0 for Streamed(2)\n// emitting 3 for Streamed(0)\n// emitting 2 for Streamed(2)\n// emitting 4 for Streamed(2)\n// emitting 2 for Streamed(1)\n// emitting 1 for Streamed(2)\n// emitting 2 for Streamed(2)\n// emitting 4 for Streamed(1)\n// emitting 3 for Streamed(2)\n// emitting 5 for Streamed(2)\n// releasing Streamed(0)\n// releasing Streamed(1)\n// releasing Streamed(2)\n// releasing Streamed(1)\n// releasing Streamed(1)\n// releasing Streamed(2)\n// releasing Streamed(2)\n// releasing Streamed(2)\n// releasing Streamed(2)\n')),(0,r.kt)("p",null,"gql also allows the user to specify how much time the interpreter may await more stream updates:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"Schema.simple(schema).map(Compiler[IO].compile(_, query, accumulate=Some(10.millis)))\n")),(0,r.kt)("p",null,"furthermore, gql can also emit interpreter information if you want to look into what gql is doing:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"Schema.simple(schema)\n  .map(Compiler[IO].compile(_, query, debug=gql.server.interpreter.DebugPrinter[IO](s => IO(println(s)))))\n  .flatMap { case Right(Application.Subscription(stream)) => stream.take(3).compile.drain }\n  .unsafeRunSync()\n// allocating Streamed(0)\n// emitting 0 for Streamed(0)\n// publishing at index 0 at root.streamed.value\n// allocating Streamed(1)\n// emitting 0 for Streamed(1)\n// publishing at index 0 at root.streamed.value.value\n// allocating Streamed(2)\n// emitting 0 for Streamed(2)\n// publishing at index 0 at root.streamed.value.value.value\n// unconsing with current tree:\n// |- unknown-cats.effect.kernel.Unique$Token@6a4a018b\n// got state, awaiting a non-empty state (publication)\n// emitting 1 for Streamed(2)\n// publishing at index 1 at root.streamed.value.value.value\n// done publishing at index 1 at root.streamed.value.value.value, await? true\n// got non-empty state, awaiting 5 milliseconds\n// unconsed:\n// [\n//   ResourceInfo(\n//     parentName = root.streamed.value.value.value (signal = true),\n//     name = resource-1,\n//     open = true,\n//     value = StreamingData(\n//       originIndex = 0,\n//       edges = StepCont.Done(\n//         Selection(\n//           PreparedSpecification(\n//             typename = Streamed,\n//             selections = PreparedSelections{\n//               PreparedDataField(\n//                 name = __typename,\n//                 alias = None,\n//                 cont = PreparedCont(\n//                   edges = Lift(...),\n//                   cont = PreparedLeaf(String)\n//                 )\n//               )\n//             }\n//           )\n//         )\n//       ),\n//       value = Right(repl.MdocSession$MdocApp$Streamed$1)\n//     )\n//   )\n// ]\n// unconsed after removing old children:\n// [\n//   ResourceInfo(\n//     parentName = root.streamed.value.value.value (signal = true),\n//     name = resource-1,\n//     open = true,\n//     value = ditto\n//   )\n// ]\n// tree after unconsing:\n// |- unknown-cats.effect.kernel.Unique$Token@6a4a018b\n// emitting 1 elements from uncons\n// interpreting for 1 inputs\n// done interpreting\n// unconsing with current tree:\n// |- unknown-cats.effect.kernel.Unique$Token@6a4a018b\n// got state, awaiting a non-empty state (publication)\n// emitting 1 for Streamed(1)\n// publishing at index 1 at root.streamed.value.value\n// done publishing at index 1 at root.streamed.value.value, await? true\n// got non-empty state, awaiting 5 milliseconds\n// emitting 1 for Streamed(0)\n// publishing at index 1 at root.streamed.value\n// done publishing at index 1 at root.streamed.value, await? true\n// unconsed:\n// [\n//   ResourceInfo(\n//     parentName = root.streamed.value.value (signal = true),\n//     name = resource-1,\n//     open = true,\n//     value = StreamingData(\n//       originIndex = 0,\n//       edges = StepCont.Done(\n//         Selection(\n//           PreparedSpecification(\n//             typename = Streamed,\n//             selections = PreparedSelections{\n//               PreparedDataField(\n//                 name = value,\n//                 alias = None,\n//                 cont = PreparedCont(\n//                   edges = Compose(\n//                     left = Compose(left = Lift(...), right = Lift(...)),\n//                     right = EmbedStream(signal = true)\n//                   ),\n//                   cont = Selection(\n//                     PreparedSpecification(\n//                       typename = Streamed,\n//                       selections = PreparedSelections{\n//                         PreparedDataField(\n//                           name = __typename,\n//                           alias = None,\n//                           cont = PreparedCont(\n//                             edges = Lift(...),\n//                             cont = PreparedLeaf(String)\n//                           )\n//                         )\n//                       }\n//                     )\n//                   )\n//                 )\n//               )\n//             }\n//           )\n//         )\n//       ),\n//       value = Right(repl.MdocSession$MdocApp$Streamed$1)\n//     )\n//   ),\n//   ResourceInfo(\n//     parentName = root.streamed.value (signal = true),\n//     name = resource-1,\n//     open = true,\n//     value = StreamingData(\n//       originIndex = 0,\n//       edges = StepCont.Done(\n//         Selection(\n//           PreparedSpecification(\n//             typename = Streamed,\n//             selections = PreparedSelections{\n//               PreparedDataField(\n//                 name = value,\n//                 alias = None,\n//                 cont = PreparedCont(\n//                   edges = Compose(\n//                     left = Compose(left = Lift(...), right = Lift(...)),\n//                     right = EmbedStream(signal = true)\n//                   ),\n//                   cont = Selection(\n//                     PreparedSpecification(\n//                       typename = Streamed,\n//                       selections = PreparedSelections{\n//                         PreparedDataField(\n//                           name = value,\n//                           alias = None,\n//                           cont = PreparedCont(\n//                             edges = Compose(\n//                               left = Compose(\n//                                 left = Lift(...),\n//                                 right = Lift(...)\n//                               ),\n//                               right = EmbedStream(signal = true)\n//                             ),\n//                             cont = Selection(\n//                               PreparedSpecification(\n//                                 typename = Streamed,\n//                                 selections = PreparedSelections{\n//                                   PreparedDataField(\n//                                     name = __typename,\n//                                     alias = None,\n//                                     cont = PreparedCont(\n//                                       edges = Lift(...),\n//                                       cont = PreparedLeaf(String)\n//                                     )\n//                                   )\n//                                 }\n//                               )\n//                             )\n//                           )\n//                         )\n//                       }\n//                     )\n//                   )\n//                 )\n//               )\n//             }\n//           )\n//         )\n//       ),\n//       value = Right(repl.MdocSession$MdocApp$Streamed$1)\n//     )\n//   )\n// ]\n// unconsed after removing old children:\n// [\n//   ResourceInfo(\n//     parentName = root.streamed.value.value (signal = true),\n//     name = resource-1,\n//     open = true,\n//     value = ditto\n//   ),\n//   ResourceInfo(\n//     parentName = root.streamed.value (signal = true),\n//     name = resource-1,\n//     open = true,\n//     value = ditto\n//   )\n// ]\n// tree after unconsing:\n// |- unknown-cats.effect.kernel.Unique$Token@6a4a018b\n// emitting 2 elements from uncons\n// interpreting for 2 inputs\n// allocating Streamed(2)\n// emitting 0 for Streamed(2)\n// publishing at index 0 at root.streamed.value.value.value\n// allocating Streamed(1)\n// emitting 0 for Streamed(1)\n// publishing at index 0 at root.streamed.value.value\n// allocating Streamed(2)\n// emitting 0 for Streamed(2)\n// publishing at index 0 at root.streamed.value.value.value\n// done interpreting\n// emitting 2 for Streamed(2)\n// publishing at index 2 at root.streamed.value.value.value\n// done publishing at index 2 at root.streamed.value.value.value, await? true\n// releasing Streamed(1)\n// releasing Streamed(2)\n// emitting 2 for Streamed(1)\n// releasing Streamed(0)\n// releasing Streamed(1)\n// releasing Streamed(2)\n// releasing Streamed(2)\n")),(0,r.kt)("h2",{id:"steps"},"Steps"),(0,r.kt)("p",null,"A ",(0,r.kt)("inlineCode",{parentName:"p"},"Step")," is the low-level algebra for a resolver, that describes a single step of evaluation for a query.\nThe variants of ",(0,r.kt)("inlineCode",{parentName:"p"},"Step")," are clearly listed in the source code. All variants of step provide orthogonal properties."))}d.isMDXComponent=!0}}]);