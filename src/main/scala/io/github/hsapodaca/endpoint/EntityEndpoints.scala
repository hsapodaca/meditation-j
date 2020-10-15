package io.github.hsapodaca.endpoint

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.alg.{
  Entity,
  EntityAlreadyExistsError,
  EntityNotFoundError,
  EntityType
}
import io.github.hsapodaca.config
import io.github.hsapodaca.endpoint.Pagination.{OffsetMatcher, PageSizeMatcher}
import io.github.hsapodaca.service.EntityService
import org.http4s.circe.{jsonOf, _}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class EntityEndpoints[F[_]: Sync] {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  implicit val entityDecoder: EntityDecoder[F, Entity] = jsonOf[F, Entity]

  def entityRoutes(
      entityService: EntityService[F]
  ): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "entities" / LongVar(id) =>
        entityService.get(id).value.flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(EntityNotFoundError) =>
            NotFound("The entity was not found.")
        }

      case GET -> Root / "entities" / "therapist" :? PageSizeMatcher(
            pageSize
          ) :? OffsetMatcher(offset) =>
        for {
          res <- entityService.listTherapists(
            pageSize.getOrElse(config.entitySearchLimitDefault),
            offset.getOrElse(0)
          )
          resp <- Ok(res.asJson)
        } yield resp

      case GET -> Root / "entities" / "meditation" :? PageSizeMatcher(
            pageSize
          ) :? OffsetMatcher(offset) =>
        for {
          res <- entityService.listTherapists(
            pageSize.getOrElse(config.entitySearchLimitDefault),
            offset.getOrElse(0)
          )
          resp <- Ok(res.asJson)
        } yield resp

      case DELETE -> Root / "entity" / LongVar(id) =>
        for {
          _ <- entityService.delete(id)
          resp <- Ok()
        } yield resp

      case req @ POST -> Root / "entity" / LongVar(_) =>
        val action = for {
          entity <- req.as[Entity]
          result <- entityService.create(entity).value
        } yield result
        action.flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(EntityAlreadyExistsError(m)) =>
            NotFound(s"The entity ${m.entityName} already exists.")
        }

      case req @ PUT -> Root / "entity" / LongVar(_) =>
        val action = for {
          entity <- req.as[Entity]
          result <- entityService.update(entity).value
        } yield result
        action.flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(EntityNotFoundError) =>
            NotFound("The entity was not found.")
        }
    }
  }
}

object EntityEndpoints {
  def endpoints[F[_]: Sync](
      entityService: EntityService[F]
  ): HttpRoutes[F] =
    new EntityEndpoints[F].entityRoutes(entityService)
}
