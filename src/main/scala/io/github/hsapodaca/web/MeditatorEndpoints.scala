package io.github.hsapodaca.web

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.alg._
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
           res <- meditatorService.get(id).value
         } yield res
         action.flatMap {
           case Right(t) => Ok(t.asJson)
           case Left(EntityNotFoundError) =>
             NotFound(s"This meditator was not found.")
         }
       }

      case req @ POST -> Root / "v1" / "meditators" =>
        val action = for {
          r <- req.as[Meditator]
          result <- meditatorService.create(r).value
        } yield result
        action.flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(ItemAlreadyExistsError) =>
            Conflict(s"This meditator already exists.")
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
