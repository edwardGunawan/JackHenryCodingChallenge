package com.jackhenry.codingchallenge

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.Logger
import cats.implicits._

object Main extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      appConfig <- AppConfig.load[IO]
      _ <- Logger[IO].info(s"Loaded config $appConfig")
      success <- build(appConfig).useForever.as(ExitCode.Success)
    } yield (success)

  private def build(appConfig: AppConfig): Resource[IO, Server] =
    for {
      client <- BlazeClientBuilder[IO].resource
      weatherService = WeatherService.make[IO](client = client, appConfig = appConfig)
      httpApi = HttpApi.make[IO](weatherService)
      server <- BlazeServerBuilder[IO]
        .bindHttp(port = appConfig.httpServerConfig.port.value, host = appConfig.httpServerConfig.host.show)
        .withHttpApp(httpApi.httpApp)
        .resource
    } yield (server)

}
