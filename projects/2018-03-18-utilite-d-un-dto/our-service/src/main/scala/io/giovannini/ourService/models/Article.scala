package io.giovannini.ourService.models

import io.giovannini.ourService.dtos.ArticleDto
import play.api.libs.json.{Format, Json}

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

  implicit val format: Format[Article] = Json.format[Article]
}
