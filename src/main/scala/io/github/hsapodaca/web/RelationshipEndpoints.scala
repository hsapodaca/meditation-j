package io.github.hsapodaca.web

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._

import io.github.hsapodaca.alg._
import io.github.hsapodaca.alg.service.RelationshipService
import org.http4s.circe.{jsonOf, _}
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class RelationshipEndpoints[F[_]: Sync] {
  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl._

  implicit val entityRelationshipDecoder: EntityDecoder[F, EntityRelationship] =
    jsonOf[F, EntityRelationship]
  implicit val entityRelationshipListDecoder
      : EntityDecoder[F, List[EntityRelationship]] =
    jsonOf[F, List[EntityRelationship]]

  def entityRoutes(
      relationshipService: RelationshipService[F]
  ): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "v1" / "entities" / LongVar(id) / "relationships" =>
        val action = for {
          r <- relationshipService.getByEntityIdAndTransact(id)
        } yield r
        action.flatMap {
          case Some(r) => Ok(r.asJson)
          case None =>
            NotFound(Error(ErrorCode.MD404, "Cannot find relationship.").asJson)
        }

      case GET -> Root / "v1" / "relationships" / LongVar(id) =>
        val action = for {
          r <- relationshipService.getAndTransact(id)
        } yield r
        action.flatMap {
          case Some(r) => Ok(r.asJson)
          case None =>
            NotFound(Error(ErrorCode.MD404, "Cannot find relationship.").asJson)
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
