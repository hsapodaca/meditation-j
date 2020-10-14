package io.github.hsapodaca.endpoint

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.alg.{Meditation, MeditationNotFoundError}
import io.github.hsapodaca.service.MeditationService
import org.http4s.circe.{jsonOf, _}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class MeditationEndpoints[F[_]: Sync] {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  implicit val meditationDecoder: EntityDecoder[F, Meditation] = jsonOf[F, Meditation]

  def meditationRoutes(meditationService: MeditationService[F]): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "meditation" / LongVar(id) =>
        meditationService.get(id).value.flatMap {
          case Right(found) => Ok(found.asJson)
          case Left(MeditationNotFoundError) => NotFound("The meditation was not found.")
        }
    }
  }
}

object MeditationEndpoints {
  def endpoints[F[_]: Sync](
      MeditationService: MeditationService[F]
  ): HttpRoutes[F] =
    new MeditationEndpoints[F].meditationRoutes(MeditationService)
}
