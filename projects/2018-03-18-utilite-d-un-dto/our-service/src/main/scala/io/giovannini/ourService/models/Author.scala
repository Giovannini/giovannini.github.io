package io.giovannini.ourService.models

import play.api.libs.json.{Format, Json}

case class Author(name: String, email: String)

object Author {
  implicit val format: Format[Author] = Json.format[Author]
}
