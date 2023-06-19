# gql <a href="https://typelevel.org/cats/"><img src="https://typelevel.org/cats/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>

gql is a functional server and client GraphQL implementation for Scala.

Check out the docs at https://valdemargr.github.io/gql/

```scala
val human = tpe[IO, Human](
  "Human",
  "name" -> lift(h => h.name),
  "friends" -> eff(_.friends.traverse(getFriend))
)
```
