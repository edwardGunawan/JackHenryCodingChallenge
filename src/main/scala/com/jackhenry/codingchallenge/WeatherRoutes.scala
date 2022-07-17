package com.jackhenry.codingchallenge

import cats.MonadThrow
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import org.http4s.circe.CirceEntityEncoder
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

/**
  * The Weather Routes consist of the precis of /weathers.
  * Having weatherService algebra as an argument for our class.
  * We extend Http4sDSL to access the DSL method
  * There is prefix path
  * Lastly, public router which uses thea Router that let us add prefixPath to a group of endpoint denoted by HttpRoutes.
  * @param weatherService
  * @param monadThrow$F$0
  * @param logger$F$1
  * @tparam F
  */
final case class WeatherRoutes[F[_]: MonadThrow: Logger](weatherService: WeatherService[F])
    extends Http4sDsl[F]
    with CirceEntityEncoder {
  private[this] val prefix = "/weathers"

  object longitude extends QueryParamDecoderMatcher[Double]("longitude")
  object latitude extends QueryParamDecoderMatcher[Double]("latitude")
  object unit extends OptionalQueryParamDecoderMatcher[Model.Unit]("unit")

  private val httpRoutes = HttpRoutes.of[F] {
    case GET -> Root :? longitude(long) +& latitude(lat) +& unit(unit) => {
      weatherService
        .get(long, lat, unit)
        .handleErrorWith { err =>
          Logger[F].error(err)("Error occurred while fetching the weatherService") *> AppError
            .InternalServerError(msg = err.getMessage)
            .raiseError[F, Model.WeatherCondition]
        }
        .flatMap(Ok(_))
    }
  }

  def routes = Router(
    prefix -> httpRoutes
  )
}
