package io.github.hsapodaca.endpoint

import cats.effect.Sync
import cats.implicits._
import io.github.hsapodaca.service.ReadinessCheckService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
class ReadinessCheckEndpoints[F[_]: Sync] {
  val dsl = new Http4sDsl[F] {}
  import dsl._

  def readinessCheckRoutes(H: ReadinessCheckService[F]): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "status" =>
        for {
          res <- H.check()
          resp <- Ok(res)
        } yield resp
    }
  }
}

object ReadinessCheckEndpoints {
  def endpoints[F[_]: Sync](
      ReadinessCheckService: ReadinessCheckService[F]
  ): HttpRoutes[F] =
    new ReadinessCheckEndpoints[F].readinessCheckRoutes(ReadinessCheckService)
}
