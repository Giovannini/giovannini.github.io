---
layout: post
title: Utiliser un DTO pour s'interfacer avec une API
date: 2018-03-18
categories: api, dto, scala, play framework
comments: true
---

Un objet de transfert de données (Data Transfer Object ou DTO) est un design pattern dont le but est de simplifier la communication entre différents processus. Si dans une architecture monolithique une évolutions d'API s'opère simplement, il s'agit d'une tâche plus complexe dans une architecture comportant plusieurs services: on ne peut forcer tous les clients de son service à évoluer en même temps.

Un des premiers buts de ce design pattern était de concentrer de l'information de manière à réduire le nombre d'appels à une API externe qui pourrait être coûteux. Cependant, nous allons voir dans cet article un second avantage: la manière dont les DTO nous permettent de décoreller différents services et ainsi prévenir des évolutions externes. En effet, **nous ne souhaitons pas que l'évolution du schéma d'une API externe impacte notre système pour éviter de demander une évolution à tous les services dépendant de notre service**.

Nous allons ici imaginer un service dont le but est de diffuser les différents éléments constituant notre blog. Il a une dépendance sur une API externe qui expose justement des informations du blog...

## Modélisation

*Nous ne nous soucierons pas de potentiels soucis d'asynchronisme dans cet article ni de la façon dont les éléments sont exposés (API REST, utilisation via un client, ...). Le design pattern dont nous traitons aujourd'hui est agnostique à ces problèmatiques.*

Imaginons donc une modélisation simple de ce problème:

{% highlight scala %}
package io.giovannini.externalApi

import play.api.libs.json.{JsObject, Json}

class ExternalApi() {

  def get: JsObject = Json.obj(
    "title" -> "Utiliser un DTO pour s'interfacer avec une API",
    "author" -> "Thomas GIOVANNINI",
    "email" -> "giovannini.thomas@gmail.com",
    "publicationDate" -> "2018-03-18"
  )

}
{% endhighlight %}

{% highlight scala %}
package io.giovannini.ourService

import io.giovannini.externalApi.ExternalApi
import play.api.libs.json.Json

object OurService extends App {

  val externalApi = new ExternalApi()

  println(Json.stringify(externalApi.get))

}
{% endhighlight %}

Notre objectif ici est de décoreller ce qu'expose `OurService` du schéma envoyé par `ExternalApi`. Pour ce faire, nous allons définir deux types d'objets qui se ressembleront beaucoup mais qui auront deux objectifs différents.

Le premier sera notre DTO, c'est à dire la schématisation de la donnée provenant de notre API externe avec laquelle nous n'aurons aucune interaction autre qu'une action de conversion en notre second objet.
{% highlight scala %}
package io.giovannini.ourService.dtos

import play.api.libs.json.{Json, OFormat}

case class ArticleDto(
  title: String,
  author: String,
  email: String,
  publicationDate: java.time.LocalDate
)

object ArticleDto {
  implicit val format: OFormat[ArticleDto] =
    Json.format[ArticleDto]
}
{% endhighlight %}

Le second sera le modèle exposé par notre service: il sera construit à partir du DTO et notre service pourra interagir avec, effectuer des traitements dessus.
{% highlight scala %}
package io.giovannini.ourService.models

import play.api.libs.json.{Format, Json}

case class Author(name: String, email: String)

object Author {
  implicit val format: Format[Author] =
    Json.format[Author]
}

case class Article(
  title: String,
  author: Author,
  date: java.time.LocalDate
)

object Article {
  def apply(articleDto: ArticleDto) = new Article(
    title = articleDto.title,
    author = Author(
      name = articleDto.author,
      email = articleDto.email
    ),
    date = articleDto.publicationDate
  )
  
  implicit val format: Format[Article] =
    Json.format[Article]
}
{% endhighlight %}

L'idée ici est que notre service n'utilisera jamais directement la donnée provenant de l'API externe puisqu'elle opérera uniquement sur son modèle. Ainsi, si le schéma de l'API externe évolue, tous les traitement développés, en interne dans le service comme en externe par des services dépendants, ne seront pas à revoir. Seule la courte étape de conversion entre le DTO et le modèle devra être modifiée.

{% highlight scala %}
package io.giovannini.ourService

import io.giovannini.externalApi.ExternalApi
import io.giovannini.ourService.dtos.ArticleDto
import io.giovannini.ourService.models.Article
import play.api.libs.json.Json

object Main extends App {

  val externalApi = new ExternalApi()

  val json = externalApi.get
  ArticleDto.format.reads(json).asOpt match {
    case Some(articleDto) =>
      val article = Article(articleDto)
      // doSomething(article)
      println(Json.stringify {
        Article.format.writes(article)
      })
    case _ =>
      println(s"An conversion evolution is needed.")
  }

}
{% endhighlight %}

On peut observer ce que l'on défini comme étant notre DTO ici n'est en réalité que le modèle d'un autre service. Ainsi, notre `Article` étant ici notre modèle, se retrouvera certainement défini comme étant un DTO dans un service client.

Grâce à cette modélisation quelque peu répétitive, nous avons un tampon avec la ligne
{% highlight scala %}
val article = Article(articleDto)
{% endhighlight %}
qui nous permet de n'avoir aucun impact sur nos traitements ni sur le schéma de notre donnée exposée.

Une évolution du schéma de l'API externe devra être reportée dans notre DTO et prise en compte dans le méthode de construction de notre `Article`. Ce changement qui peut être majeur pour l'API externe sera mineur pour notre service et n'aura donc pas d'impact sur nos clients.

## Conclusion
Nous avons vu que le design pattern DTO permet d'assurer à un service client de ne pas dépendre d'un modèle provenant d'un domaine externe.

[Le code de cet article est disponible ici.](https://github.com/Giovannini/giovannini.github.io/tree/master/projects/2018-03-18-utilite-d-un-dto)

### Interesting reads:
* [LocalDTO - Matrin Fowlers](https://martinfowler.com/bliki/LocalDTO.html)
* [Data Transfer Object Is a Shame - Yegor Bugayenko](http://www.yegor256.com/2016/07/06/data-transfer-object.html)

