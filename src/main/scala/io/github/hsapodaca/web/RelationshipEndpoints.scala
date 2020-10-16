package io.github.hsapodaca.web

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.hsapodaca.alg._
import io.github.hsapodaca.config
import io.github.hsapodaca.web.Pagination.{OffsetMatcher, PageSizeMatcher}
import org.http4s.circe.{jsonOf, _}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class RelationshipEndpoints[F[_]: Sync] {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  implicit val entityRelationshipDecoder: EntityDecoder[F, EntityRelationship] = jsonOf[F, EntityRelationship]

  def entityRoutes(
      relationshipService: RelationshipService[F]
  ): HttpRoutes[F] = {
    HttpRoutes.of[F] {

      case GET -> Root / "v1" / "entities" / LongVar(id) / "relationships" =>
        for {
          res <- relationshipService.getByEntityId(id).value
          resp <- Ok(res.asJson)
        } yield resp

      case DELETE -> Root / "v1" / "relationships" / LongVar(id) =>
        for {
          _ <- relationshipService.delete(id)
          resp <- Ok()
        } yield resp

      case req @ POST -> Root / "v1" / "relationships" =>
        val action = for {
          relationship <- req.as[EntityRelationship]
          result <- relationshipService.create(relationship).value
        } yield result
        action.flatMap {
          case Right(entity) => Ok(entity.asJson)
          case Left(RelationshipAlreadyExistsError(m)) =>
            Conflict(s"The relationship ${m.entityName} already exists.")
        }
    }
  }
}


object RelationshipEndpoints {
  def endpoints[F[_]: Sync](
                             relationshipService: RelationshipService[F]
                           ): HttpRoutes[F] =
    new RelationshipEndpoints[F].entityRoutes(relationshipService)
}

