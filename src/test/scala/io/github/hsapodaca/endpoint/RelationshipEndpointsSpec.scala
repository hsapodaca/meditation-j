package io.github.hsapodaca.endpoint

import cats.effect.IO
import cats.implicits.toSemigroupKOps
import io.circe.generic.auto._
import io.github.hsapodaca.alg._
import io.github.hsapodaca.endpoint.repos.{clearData, entities, relationships}
import io.github.hsapodaca.web.{EntityEndpoints, RelationshipEndpoints}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{Response, Status, Uri}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RelationshipEndpointsSpec
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfter
    with Http4sDsl[IO]
    with Http4sClientDsl[IO] {

  s"GET /entities/id/relationships" should "respond with 200 and a list of entities" in {
    val resp = get(s"/v1/entities/1/relationships")
    assert(resp.status === Status.Ok)
    assert(
      resp
        .as[Option[EntityRelationship]]
        .unsafeRunSync()
        .head
        .primaryEntityId === 1
    )
  }

  it should "respond with 404 to nonexistent one" in {
    val resp = get(s"/v1/entities/4/relationships")
    assert(resp.status === Status.NotFound)
  }

  s"GET /relationships/id" should "respond with 200 and a relationship" in {
    val resp = get(s"/v1/relationships/1")
    assert(resp.status === Status.Ok)
    assert(resp.as[EntityRelationship].unsafeRunSync().primaryEntityId === 1)
  }

  it should "respond with 404 to nonexistent one" in {
    val resp = get(s"/v1/relationships/nonexistent")
    assert(resp.status === Status.NotFound)
    val resp2 = get(s"/v1/relationships/0")
    assert(resp2.status === Status.NotFound)
  }

  private[this] def get(s: String): Response[IO] = {
    val req = GET(Uri.unsafeFromString(s)).unsafeRunSync()
    RelationshipEndpoints
      .endpoints(relationships)
      .orNotFound(req)
      .unsafeRunSync()
  }
}
