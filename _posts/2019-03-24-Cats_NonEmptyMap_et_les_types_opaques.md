---
layout: post
title: La NonEmptyMap de Cats et les types opaques
date: 2019-03-24T10:00:00.000Z
categories: Scala, Cats, Opaque types, NonEmptyMap, Functional
comments: true
---

En voulant utiliser cette semaine certains types fonctionnels que propose
la librairie Cats, j'ai été confronté à la problématique d'utilisation
récurrente des `newtypes` dans cette librairie.

## Le problème
`NonEmptyMap` est une des structure de données que propose Cats depuis sa
version 1.6.0, qui se comporte globalement comme une `Map` Scala avec la
particularité d'assurer à la compilation qu'elle n'est pas vide. Des
opérations comme `head` ne sont alors pas dangereuses avec de telles
structures.

J'ai rencontré un problème avec ces structures lorsque j'ai souhaité dériver
les formats de sérialisation/désérialisation JSON de cette classe via la
librairie Play JSON.

```scala
import cats.data.NonEmptyMap
import play.api.libs.json.{Json, OFormat}

final case class Foo(a: Int, b: NonEmptyMap[String, Int])
object Foo {
  val format: OFormat[Foo] = Json.format[Foo]
  // 👆 No instance of play.api.libs.json.Format is available

  // for cats.data.Newtype2.Type[java.lang.String, scala.Int]

  // in the implicit scope

}
```

La librairie Play JSon est capable de dériver un format pour une `case class`
si elle est capable de dériver le format pour chaque type qui la compose.
Il est assez simple de générer un format particulier pour une NonEmptyMap,
par exemple en se basant sur le format de `Map` qui est déjà défini dans
Play JSON. Ne nous encombrons pas de ce code ici: cela ne résout pas notre
problème:

```scala
  implicit val nemFormat: OFormat[NonEmptyMap[String, Int]] = ???
  val format: OFormat[Foo] = Json.format[Foo]
  // 👆 No instance of play.api.libs.json.Format is available

  // for cats.data.Newtype2.Type[java.lang.String, scala.Int]

  // in the implicit scope

}
```
Nous avons toujours la même erreur de compilation, même en définissant
explicitement l'implicite qui est censé manquer.

## Approfondissement
Un détail est que le compilateur ne semble pas indiquer que l'impicite lui
manquant est celui que nous définissons. Plutôt que de nous parler de 
`cats.data.NonEmptyMap[java.lang.String, scala.Int]`, il nous indique un type
`cats.data.Newtype2.Type[java.lang.String, scala.Int]`.

Cela est dû à la définition du type `NonEmptyMap`. Dans cats, nous retrouvons:
```scala
// cats/core/src/main/scala/cats/data/package.scala 

type NonEmptyMap[K, +A] = NonEmptyMapImpl.Type[K, A]
val NonEmptyMap = NonEmptyMapImpl

// cats/core/src/main/scala/cats/data/NonEmptyMapImpl.scala 

private[data] object NonEmptyMapImpl extends NonEmptyMapInstances with Newtype2
```

On retrouve ce `NewType2` qui
[est défini](https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/Newtype2.scala)
comme un helper pour ce qu'on appelle des `newtypes`.
```scala
// cats/core/src/main/scala/cats/data/Newtype2.scala 

private[data] trait Newtype2 { self =>
  private[data] type Base
  private[data] trait Tag extends Any
  type Type[A, +B] <: Base with Tag
}
```

## Newtypes
Le mot clef `newtype` provient de Haskell et est liée à ce qu'on appelle des
types opaques. L'introduction d'une syntaxe particulière pour ce genre de
définitions est discuté dans le SIP-35 concernant les types opaques. On y
trouve beaucoup d'informations sur la motivation à utiliser ce genre de
structures.

Pour résumer
[ce qui est dit dans ce SIP](Source: https://docs.scala-lang.org/sips/opaque-types.html#motivation),
l'objectif est de limiter le coût d'allocation à payer au runtime lors de la
définition de types "emballage" que l'on peut toujours avoir même en utilisant
des value classes. Ces opérations étant fréquentes dans du code de la vie
réelle, il est souhaitable de vouloir les optimiser. L'idée proposée par le
SIP-35 est alors de fournir un emballage qui n'existe qu'à la compilation et
plus au runtime.

Cats est forcément friand de genre d'optimisation puisque la plupart des
structures qu'ils définissent dans leur package `cats.data` sont justement
des emballages de structures de données: `NonEmptyList` pour `List`,
`NonEmptyMap` pour `Map`, ...

Ainsi, l'idée est de laisser les auteurs de librairies créer des types et leur
API dans une partie isolée de leur code et forcer les utilisateurs de cette
API à utiliser ces types opaques.

## Résolution
Pour le moment, la seule façon de résoudre le problème induit par ce genre de
construction est de ne pas passer par les macros de PlayJson qui semblent
ne pas parvenir à résoudre la résolution des instances implicites dans le scope.
Il est possible de définir son format manuellement pour passer outre:
```scala
final case class Foo(a: Int, b: NonEmptyMap[String, Int])
object Foo {
  implicit val nemFormat: OFormat[NonEmptyMap[String, Int]] = ???
  val format: OFormat[Foo] = {
    import play.api.libs.functional.syntax._
    (
      (__ \ "a").format[Int] and
      (__ \ "b").format[NonEmptyMap[String, Int]]
    )(Foo.apply, unlift(Foo.unapply))
  }
}
```
C'est la solution qui est proposée dans l'[issue 2582](https://github.com/typelevel/cats/issues/2582) de la librairie Cats.

[Le code de cet article est disponible ici.](https://github.com/Giovannini/giovannini.github.io/tree/master/projects/2019-03-24-Cats_NonEmptyMap_et_les_types_opaques/opaque-types)
