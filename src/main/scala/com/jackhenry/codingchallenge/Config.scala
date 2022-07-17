package com.jackhenry.codingchallenge

import cats.effect.kernel.Async
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import ciris._
import com.comcast.ip4s._

case class AppConfig(appId: String, httpServerConfig: AppConfig.HttpServerConfig)

object AppConfig {

  case class HttpServerConfig(host: Host, port: Port)

  /**
    * There are currently 2 ENV test, and prod. However, for coding challenge we will not have such environment, and
    * will load the secret value from the env variable
    * @tparam F
    * @return
    */
  def load[F[_]: Async]: F[AppConfig] = default[F].load[F]

  private def default[F[_]]: ConfigValue[F, AppConfig] = env("APP_ID").as[String].map { appId =>
    AppConfig(
      appId = appId,
      httpServerConfig = HttpServerConfig(
        host = host"0.0.0.0",
        port = port"8080"
      )
    )
  }

}
