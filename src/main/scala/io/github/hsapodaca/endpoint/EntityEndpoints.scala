package io.github.hsapodaca.endpoint

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.alg.{Entity, EntityAlreadyExistsError, EntityNotFoundError}
import io.github.hsapodaca.config
import io.github.hsapodaca.endpoint.Pagination.{OffsetMatcher, PageSizeMatcher}
import io.github.hsapodaca.service.{EntityService}
import org.http4s.circe.{jsonOf, _}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class EntityEndpoints[F[_]: Sync] {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  implicit val meditationDecoder: EntityDecoder[F, Entity] = jsonOf[F, Entity]

  def meditationRoutes(
      meditationService: EntityService[F]
  ): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "meditations" / LongVar(id) =>
        meditationService.get(id).value.flatMap {
          case Right(meditation) => Ok(meditation.asJson)
          case Left(EntityNotFoundError) =>
            NotFound("The meditation was not found.")
        }

      case GET -> Root / "meditations" :? PageSizeMatcher(
            pageSize
          ) :? OffsetMatcher(offset) =>
        for {
          res <- meditationService.list(
            pageSize.getOrElse(config.meditationSearchLimitDefault),
            offset.getOrElse(0)
          )
          resp <- Ok(res.asJson)
        } yield resp

      case DELETE -> Root / "meditation" / LongVar(id) =>
        for {
          _ <- meditationService.delete(id)
          resp <- Ok()
        } yield resp

      case req @ POST -> Root / "meditation" / LongVar(_) =>
        val action = for {
          meditation <- req.as[Entity]
          result <- meditationService.create(meditation).value
        } yield result
        action.flatMap {
          case Right(meditation) => Ok(meditation.asJson)
          case Left(EntityAlreadyExistsError(m)) =>
            NotFound(s"The meditation ${m.entityName} already exists.")
        }

      case req @ PUT -> Root / "meditation" / LongVar(_) =>
        val action = for {
          meditation <- req.as[Entity]
          result <- meditationService.update(meditation).value
        } yield result
        action.flatMap {
          case Right(meditation) => Ok(meditation.asJson)
          case Left(EntityNotFoundError) =>
            NotFound("The meditation was not found.")
        }
    }
  }
}

object EntityEndpoints {
  def endpoints[F[_]: Sync](
      meditationService: EntityService[F]
  ): HttpRoutes[F] =
    new EntityEndpoints[F].meditationRoutes(meditationService)
}
