import sbt._

lazy val externalApi = project.in(file("external-api"))
  .settings(
    organization := "io.giovannini",
    name := "external-api",
    scalaVersion := "2.12.4",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.7"
  )

lazy val ourService = project.in(file("our-service"))
  .settings(
    organization := "io.giovannini",
    name := "our-service",
    scalaVersion := "2.12.4",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.7"
  )
  .dependsOn(externalApi)