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
      // do something
      println(Json.stringify {
        Article.format.writes(article)
      })
    case _ =>
      println(s"An conversion evolution is needed.")
  }

}