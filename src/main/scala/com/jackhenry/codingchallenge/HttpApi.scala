package com.jackhenry.codingchallenge

import cats.effect.kernel.Async
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.RequestLogger
import org.http4s.server.middleware.ResponseLogger
import org.http4s.implicits._
import org.typelevel.log4cats.Logger

sealed abstract class HttpApi[F[_]: Async: Logger](weatherService: WeatherService[F]) {
  private val weatherRoutes = WeatherRoutes(weatherService).routes

  private val combinedRoutes: HttpRoutes[F] = Router(
    Version.v1 -> weatherRoutes
  )

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = { httpRoute: HttpRoutes[F] =>
    CORS.policy(httpRoute)
  }

  private val logger: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  val httpApp: HttpApp[F] = logger(middleware(combinedRoutes).orNotFound)
}

object HttpApi {
  def make[F[_]: Async: Logger](weatherService: WeatherService[F]): HttpApi[F] = new HttpApi[F](weatherService) {}
}
