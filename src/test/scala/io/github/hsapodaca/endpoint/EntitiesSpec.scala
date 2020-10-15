package io.github.hsapodaca.endpoint

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import io.github.hsapodaca.endpoint.repos.entities
import io.github.hsapodaca.alg._
import io.github.hsapodaca.web.EntityEndpoints
import org.http4s.circe.jsonOf
import org.http4s.{Method, Request, Response, Status, Uri}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EntitiesSpec
    extends AnyFlatSpec
    with Matchers
    with Http4sDsl[IO]
    with Http4sClientDsl[IO] {

  List("therapists", "meditations") foreach { item =>
    s"GET /entities/$item" should "respond with 200 and a list of entities" in {
      val resp = get(s"/v1/entities/$item")
      assert(resp.status === Status.Ok)
      assert(!resp.as[List[Entity]].unsafeRunSync().head.entityName.isBlank)
    }

    s"GET /entities/$item" should "paginate until the end of content" in {
      val resp = get(s"/v1/entities/$item?pageSize=1&offset=0")
      assert(resp.status === Status.Ok)
      assert(!resp.as[List[Entity]].unsafeRunSync().head.entityName.isBlank)
      val resp2 = get(s"/v1/entities/$item?pageSize=10&offset=2")
      assert(resp2.status === Status.Ok)
      assert(resp2.as[List[Entity]].unsafeRunSync() === List())
    }
  }

  s"GET /entities/id" should "respond with 200 and an entity" in {
    val resp = get(s"/v1/entities/1")
    assert(resp.status === Status.Ok)
    assert(!resp.as[Entity].unsafeRunSync().entityName.isBlank)
  }

  s"GET /entities/nonexistent" should "respond with 404" in {
    val resp = get(s"/v1/entities/nonexistent")
    assert(resp.status === Status.NotFound)
  }

  s"POST /entities" should "not create existing type and name" in {
    val resp = post(s"/v1/entities", Entity(Some(1L), "J", "...", "...", EntityType.Therapist))
    assert(resp.status === Status.Conflict)
  }

  private[this] def get(s: String): Response[IO] = {
    val req = GET(Uri.unsafeFromString(s)).unsafeRunSync()
    EntityEndpoints
      .endpoints(entities)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def post(s: String, entity: Entity): Response[IO] = {
    val req = POST(entity, Uri.unsafeFromString(s)).unsafeRunSync()
    EntityEndpoints
      .endpoints(entities)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def delete(s: String): Response[IO] = {
    val req = DELETE(Uri.unsafeFromString(s)).unsafeRunSync()
    EntityEndpoints
      .endpoints(entities)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def patch(s: String, entity: Entity): Response[IO] = {
    val req = PATCH(entity, Uri.unsafeFromString(s)).unsafeRunSync()
    EntityEndpoints
      .endpoints(entities)
      .orNotFound(req)
      .unsafeRunSync()
  }
}
