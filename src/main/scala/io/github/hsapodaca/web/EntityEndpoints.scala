package io.github.hsapodaca.web

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.config
import Pagination.{OffsetMatcher, PageSizeMatcher}
import io.github.hsapodaca.alg.service.EntityService
import io.github.hsapodaca.alg.{Entity, EntityNotFoundError}
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

      case GET -> Root / "v1" / "entities" / LongVar(id) =>
        val action = for {
          res <- entityService.getAndTransact(id)
        } yield res
        action flatMap {
          case Some(entity) => Ok(entity.asJson)
          case None => NotFound("The entity was not found.")
        }

      case GET -> Root / "v1" / "entities" / "friends" :? PageSizeMatcher(
            pageSize
          ) :? OffsetMatcher(offset) =>
        for {
          res <- entityService.listFriends(
            pageSize.getOrElse(config.entitySearchLimitDefault),
            offset.getOrElse(0)
          )
          resp <- Ok(res.asJson)
        } yield resp

      case GET -> Root / "v1" / "entities" / "meditations" :? PageSizeMatcher(
            pageSize
          ) :? OffsetMatcher(offset) =>
        for {
          res <- entityService.listMeditations(
            pageSize.getOrElse(config.entitySearchLimitDefault),
            offset.getOrElse(0)
          )
          resp <- Ok(res.asJson)
        } yield resp

      case req @ PUT -> Root / "v1" / "entities" / LongVar(id) =>
        val action = for {
          entity <- req.as[Entity]
          resp <- entityService.updateAndTransact(id, entity).value
        } yield resp

        action flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(EntityNotFoundError) => NotFound(s"The entity id $id is not found.")
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
