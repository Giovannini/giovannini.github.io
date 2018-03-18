package io.giovannini.ourService.dtos

import play.api.libs.json.{Json, OFormat}

case class ArticleDto(
  title: String,
  author: String,
  email: String,
  publicationDate: java.time.LocalDate
)

object ArticleDto {
  implicit val format: OFormat[ArticleDto] = Json.format[ArticleDto]
}
