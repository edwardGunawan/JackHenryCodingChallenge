import Dependencies.CompilerPlugin

name := "JackHenryCodingChallenge"

version := "0.1"

lazy val commonSettings = Seq(
  scalaVersion := { "2.13.8" },
  dependencyUpdatesFilter -= moduleFilter(name = "scala-library"),
  dependencyUpdatesFailBuild := { false },
  scalacOptions ++= Seq(
    "-encoding",
    //    "-Ylog-classpath",
    "UTF-8", // source files are in UTF-8
    "-deprecation", // warn about use of deprecated APIs
    "-unchecked", // warn about unchecked type parameters
    "-feature", // warn about misused language features
    "-language:higherKinds", // allow higher kinded types without "import scala.language.higherKinds"
    "-language:implicitConversions", // allow use of implicit conversions
    "-language:postfixOps",
    "-Xlint", // enabled handy linter warnings
    "-Ymacro-annotations", // macro annotation enabled
    "-Ywarn-macros:after" // allows the compiler to resolve implicit imports being flagged as unused
    //    "-Xfatal-warnings" // promotes the warnings to compiler error (so it cannot be ignored)
  ),
  addCompilerPlugin(CompilerPlugin.kindProjector)
)

lazy val root = (project
  .in(file(".")))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(AshScriptPlugin) //Tell package manager to generate our binary using ASH instead of bash
  .settings(
    commonSettings,
    name := "Weather Application",
    Docker / packageName := "weather-app",
    dockerExposedPorts ++= Seq(8080),
    dockerUpdateLatest := true,
    dockerBaseImage := "openjdk:11-jre-slim-buster", // using alpine instead of the real java jdk 8
    makeBatScripts := Seq(), // by default windows and linux scripts will be generated, since it will be run in linux env, tell to skip creation of bat script file
    resolvers += Resolver.sonatypeRepo("snapshots"),
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      Dependencies.cats,
      Dependencies.catsEffect,
      Dependencies.circeCore,
      Dependencies.circeGeneric,
      Dependencies.circeGenericExtra,
      Dependencies.circeParser,
      Dependencies.circeCore,
      Dependencies.cirisCore,
      Dependencies.http4sDsl,
      Dependencies.http4sServer,
      Dependencies.http4sClient,
      Dependencies.http4sCirce,
      Dependencies.log4cats,
      Dependencies.logback % Runtime,
      Dependencies.squant
    ) ++ Seq(
      Dependencies.scalaMock,
      Dependencies.scalaTest
    ).map(_ % "test"),
    addCompilerPlugin(CompilerPlugin.kindProjector),
    addCompilerPlugin(CompilerPlugin.betterMonadicFor)
  )
