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