package io.github.hsapodaca.endpoint

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.alg.ReadinessCheckService
import org.http4s.circe.{jsonOf, _}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
class ReadinessCheckEndpoints[F[_]: Sync] {
  val dsl = new Http4sDsl[F] {}
  import dsl._

  def readinessCheckRoutes(rcs: ReadinessCheckService[F]): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "status" => Ok(rcs.check().asJson)
    }
  }
}

object ReadinessCheckEndpoints {
  def endpoints[F[_]: Sync](
      ReadinessCheckService: ReadinessCheckService[F]
  ): HttpRoutes[F] =
    new ReadinessCheckEndpoints[F].readinessCheckRoutes(ReadinessCheckService)
}
