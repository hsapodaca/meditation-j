package io.github.hsapodaca.web

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.alg._
import io.github.hsapodaca.alg.service.MeditatorService
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.{jsonOf, _}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

import scala.language.existentials

class MeditatorEndpoints[F[_]: Sync] {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  implicit val mrDecoder: EntityDecoder[F, Meditator] = jsonOf[F, Meditator]

  def entityRoutes(
      meditatorService: MeditatorService[F]
  ): HttpRoutes[F] = {
    HttpRoutes.of[F] {

      case GET -> Root / "v1" / "meditators" / LongVar(id) => {
        val action = for {
          res <- meditatorService.get(id)
        } yield res
        action.flatMap {
          case Some(t) => Ok(t.asJson)
          case None =>
            NotFound(Error(ErrorCode.MD404, "Meditator not found.").asJson)
        }
      }

      case req @ POST -> Root / "v1" / "meditators" =>
        val action = for {
          r <- req.as[Meditator]
          result <- meditatorService.create(r).value
        } yield result
        action.flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(MeditatorAlreadyExistsError) =>
            Conflict(Error(ErrorCode.MD409, "Meditator already exists.").asJson)
          case Left(MeditatorEntityNamesMatchError) =>
            BadRequest(
              Error(
                ErrorCode.MD400,
                "Meditation name and friend name should be unique."
              ).asJson
            )
          case Left(EntityAlreadyExistsError) =>
            Conflict(Error(ErrorCode.MD409, "Entity already exists.").asJson)
          case Left(MeditatorCreationFailedError) =>
            InternalServerError(
              Error(ErrorCode.MD500, "Meditator creation failed.").asJson
            )
        }

      case DELETE -> Root / "v1" / "meditators" / LongVar(id) =>
        val action = for {
          result <- meditatorService.delete(id).value
        } yield result
        action.flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(MeditatorNotFoundError) =>
            NotFound(Error(ErrorCode.MD404, "Meditator was not found.").asJson)
        }
    }
  }
}

object MeditatorEndpoints {
  def endpoints[F[_]: Sync](
      meditatorService: MeditatorService[F]
  ): HttpRoutes[F] =
    new MeditatorEndpoints[F].entityRoutes(meditatorService)
}
