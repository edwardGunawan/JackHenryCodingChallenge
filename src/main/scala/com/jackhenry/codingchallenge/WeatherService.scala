package com.jackhenry.codingchallenge

import cats.effect.Concurrent
import com.jackhenry.codingchallenge.Model.WeatherClientResponse
import com.jackhenry.codingchallenge.Model.WeatherCondition
import org.http4s.client.Client
import org.http4s.circe.CirceEntityDecoder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._
import cats.implicits._

/**
  * Weather Service
  * Contains `get` method to get the weather of the current longitude and latitude
  * @tparam F
  */
trait WeatherService[F[_]] {
  def get(longitude: Double, latitude: Double, unit: Option[Model.Unit]): F[WeatherCondition]
}

object WeatherService {

  def make[F[_]: Concurrent](client: Client[F], appConfig: AppConfig): WeatherService[F] =
    new WeatherService[F] with Http4sClientDsl[F] with CirceEntityDecoder {
      private[this] val F = Concurrent[F]
      override def get(longitude: Double, latitude: Double, unit: Option[Model.Unit]): F[WeatherCondition] = {
        for {
          endpoint <- F.pure(
            uri"https://api.openweathermap.org/data/2.5/onecall"
              .withQueryParam("lat", latitude)
              .withQueryParam("lon", longitude)
              .withQueryParam("exclude", "hourly,daily,minutely")
              .withQueryParam("appid", appConfig.appId)
              .withQueryParam("units", "metric") //Celsius
          )

          resp <- client
            .expect[WeatherClientResponse](endpoint)
            .map { r =>
              WeatherCondition.transformFromWeatherClientResponse(
                unit = unit.getOrElse(Model.Unit.Fahrenheit),
                longitude = longitude,
                latitude = latitude,
                weatherClientResponse = r
              )
            }
        } yield {
          resp
        }
      }
    }
}
