import sbt.librarymanagement.ModuleID
import sbt._

object Dependencies {

  object V {
    val cats = "2.7.0"
    val catsEffect = "3.3.11"
    val circe = "0.14.1"
    val http4s = "0.23.10"
    val log4Cats = "2.3.1"
    val ciris = "2.3.2"

    val betterMonadicFor = "0.3.1"
    val kindProjector = "0.13.2"
    val logback = "1.2.11"

    val squant = "1.8.3"
  }

  def ciris(artifact: String): ModuleID = "is.cir" %% artifact % V.ciris

  def circe(artifact: String): ModuleID =
    "io.circe" %% s"circe-$artifact" % V.circe

  def http4s(artifact: String): ModuleID =
    "org.http4s" %% s"http4s-$artifact" % V.http4s

  val cats = "org.typelevel" %% "cats-core" % V.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect

  val circeCore = circe("core")
  val circeGeneric = circe("generic")
  val circeParser = circe("parser")
  val circeRefined = circe("refined")
  val circeGenericExtra = circe("generic-extras")

  val cirisCore = ciris("ciris")

  val http4sDsl = http4s("dsl")
  val http4sServer = http4s("blaze-server")
  val http4sClient = http4s("blaze-client")
  val http4sCirce = http4s("circe")

  val log4cats = "org.typelevel" %% "log4cats-slf4j" % V.log4Cats

  val squant = "org.typelevel" %% "squants" % V.squant

  // Runtime
  val logback = "ch.qos.logback" % "logback-classic" % V.logback

  // Test
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.8"
  lazy val scalaMock = "org.scalamock" %% "scalamock" % "5.1.0"

  object CompilerPlugin {

    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % V.betterMonadicFor
    )

    val kindProjector = compilerPlugin(
      "org.typelevel" % "kind-projector" % V.kindProjector cross CrossVersion.full
    )
  }

}
