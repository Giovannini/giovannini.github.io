lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.github.giovannini",
      scalaVersion := "2.12.7"
    )),
    name := "scalatest-example"
  )

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.10"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
