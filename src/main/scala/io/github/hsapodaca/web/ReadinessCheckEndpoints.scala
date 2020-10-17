package io.github.hsapodaca.web

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.alg.service.MeditatorService
import io.github.hsapodaca.alg.{MeditatorNotFoundError, StatusInfo}
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class ReadinessCheckEndpoints[F[_]: Sync] {
  val dsl = new Http4sDsl[F] {}
  import dsl._

  def readinessCheckRoutes(
      meditationReader: MeditatorService[F]
  ): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "status" =>
        meditationReader.get(1).flatMap {
          case Some(mr) =>
            Ok(
              StatusInfo(
                friend = mr.friend.entityName,
                meditation = mr.meditation.entityName
              ).asJson
            )
          case None =>
            NotFound("Pre-seeded data was not found.")
        }
    }
  }
}

object ReadinessCheckEndpoints {
  def endpoints[F[_]: Sync](
      meditationReader: MeditatorService[F]
  ): HttpRoutes[F] =
    new ReadinessCheckEndpoints[F].readinessCheckRoutes(meditationReader)
}
