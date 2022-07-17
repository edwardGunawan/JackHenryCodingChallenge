package com.jackhenry.codingchallenge

import java.time.Instant

import cats.data.ValidatedNel
import io.circe.generic.JsonCodec
import io.circe.generic.extras.ConfiguredJsonCodec
import org.http4s.ParseFailure
import org.http4s.QueryParamDecoder
import org.http4s.QueryParameterValue
import squants.thermal.TemperatureConversions._
import cats.implicits._
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.generic.extras.Configuration

object Model {

  @ConfiguredJsonCodec
  case class WeatherClientResponse(timezone: String, current: Current, alerts: List[Alert] = Nil)

  object WeatherClientResponse {
    implicit val config: Configuration = Configuration.default.withDefaults
  }

  @ConfiguredJsonCodec
  case class Current(
    dt: Long,
    temp: Double,
    feels_like: Double,
    wind_speed: Double,
    weather: List[Weather]
  )

  object Current {
    implicit val config: Configuration = Configuration.default.withDefaults
  }

  @JsonCodec
  case class Weather(id: Long, main: String, description: String)

  case class Alert(sender_name: String, event: String, start: Instant, end: Instant, description: String)

  object Alert {
    implicit val encoder: Encoder[Alert] = io.circe.generic.semiauto.deriveEncoder[Alert]
    implicit val decoder: Decoder[Alert] = (hCursor: HCursor) =>
      for {
        senderName <- hCursor.get[String]("sender_name")
        event <- hCursor.get[String]("event")
        startTime <- hCursor.get[Long]("start").map(l => Instant.ofEpochSecond(l))
        endTime <- hCursor.get[Long]("end").map(l => Instant.ofEpochSecond(l))
        description <- hCursor.get[String]("description")
      } yield (Alert(senderName, event, startTime, endTime, description))
  }

  @JsonCodec
  case class WeatherCondition(
    longitude: Double,
    latitude: Double,
    timezone: String,
    temperature: Double,
    unit: Unit,
    condition: String,
    feels: Feels,
    alerts: List[Alert]
  )

  object WeatherCondition {

    def transformFromWeatherClientResponse(
      unit: Unit,
      longitude: Double,
      latitude: Double,
      weatherClientResponse: WeatherClientResponse
    ): WeatherCondition = WeatherCondition(
      longitude = longitude,
      latitude = latitude,
      timezone = weatherClientResponse.timezone,
      temperature = convertTemperature(unit, weatherClientResponse.current.temp),
      unit = unit,
      condition = weatherClientResponse.current.weather.map(_.main).mkString(","),
      feels = Feels.convertToFeels(weatherClientResponse.current.temp),
      alerts = weatherClientResponse.alerts
    )

    private def convertTemperature(unit: Unit, temperature: Double): Double = unit match {
      case Unit.Fahrenheit =>
        (temperature.C toFahrenheitDegrees)
      case Unit.Celsius =>
        temperature
      case Unit.Kelvin =>
        (temperature.C toKelvinDegrees)
    }
  }

  sealed trait Feels

  object Feels {
    case object Hot extends Feels
    case object Cold extends Feels
    case object Moderate extends Feels

    implicit val encoder: Encoder[Feels] = io.circe.generic.extras.semiauto.deriveEnumerationEncoder[Feels]
    implicit val decoder: Decoder[Feels] = io.circe.generic.extras.semiauto.deriveEnumerationDecoder[Feels]

    def convertToFeels(temp: Double): Feels =
      if (temp < 15) Cold
      else if (temp > 15 && temp < 22) Moderate
      else {
        Hot
      }

  }

  sealed trait Unit

  object Unit {
    case object Fahrenheit extends Unit
    case object Celsius extends Unit
    case object Kelvin extends Unit

    implicit val encoder: Encoder[Unit] = io.circe.generic.extras.semiauto.deriveEnumerationEncoder[Unit]
    implicit val decoder: Decoder[Unit] = io.circe.generic.extras.semiauto.deriveEnumerationDecoder[Unit]

    implicit val decoderMatchers: QueryParamDecoder[Unit] = new QueryParamDecoder[Unit] {
      override def decode(value: QueryParameterValue): ValidatedNel[ParseFailure, Unit] =
        value.value.toLowerCase match {
          case "farenheit" => Unit.Fahrenheit.validNel
          case "celsius" => Unit.Celsius.validNel
          case "kelvin" => Unit.Kelvin.validNel
          case c => {
            val message = s"${c} is not a valid unit. Please enter farenheit, celsius, or kelvin."
            ParseFailure(message, message).invalidNel
          }
        }
    }
  }

}
