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
           case None => NotFound(s"Meditator not found.")
         }
       }

      case req @ POST -> Root / "v1" / "meditators" =>
        val action = for {
          r <- req.as[Meditator]
          result <- meditatorService.create(r)
        } yield result
        action.flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(ItemCreationFailedError) =>
            BadRequest(s"Failed to create a meditator.")
        }

       case DELETE -> Root / "v1" / "meditators" / LongVar(id) =>
         val action = for {
           result <- meditatorService.delete(id)
         } yield result
         action.flatMap {
           case Right(entity) => Ok(entity.asJson)
           case Left(ItemDeletionFailedError) =>
             BadRequest(s"Failed to delete a meditator.")
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
