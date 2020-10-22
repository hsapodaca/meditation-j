package io.github.hsapodaca.endpoint

import cats.effect.IO
import io.circe.generic.auto._
import io.github.hsapodaca.alg._
import io.github.hsapodaca.endpoint.repos.{clearData, entities, meditators}
import io.github.hsapodaca.web.{EntityEndpoints, MeditatorEndpoints}
import org.http4s.circe.CirceEntityCodec.{
  circeEntityDecoder,
  circeEntityEncoder
}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{Response, Status, Uri}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EntityEndpointsSpec
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfter
    with Http4sDsl[IO]
    with Http4sClientDsl[IO] {

  before {
    clearData
  }

  after {
    clearData
  }

  for {
    (item, itemType) <-
      Map("friends" -> "Friend", "meditations" -> "Meditation")
  } {
    s"GET /entities/$item" should "respond with 200 and a list of entities" in {
      val resp = get(s"/v1/entities/$item")
      assert(resp.status === Status.Ok)
      assert(!resp.as[List[Entity]].unsafeRunSync().head.name.isBlank)
      assert(
        resp.as[List[Entity]].unsafeRunSync().head.`type`.toString === itemType
      )
    }

    s"GET /entities/$item" should "paginate until the end of content" in {
      val resp = get(s"/v1/entities/$item?pageSize=1&offset=0")
      assert(resp.status === Status.Ok)
      assert(!resp.as[List[Entity]].unsafeRunSync().head.name.isBlank)
      val resp2 = get(s"/v1/entities/$item?pageSize=10&offset=2")
      assert(resp2.status === Status.Ok)
      assert(resp2.as[List[Entity]].unsafeRunSync() === List())
    }
  }

  "GET /entities/id" should "respond with 200 and an entity" in {
    val resp = get(s"/v1/entities/1")
    assert(resp.status === Status.Ok)
    assert(!resp.as[Entity].unsafeRunSync().name.isBlank)
  }

  it should "respond with 404 for nonexistent id" in {
    val resp = get(s"/v1/entities/0")
    assert(resp.status === Status.NotFound)
  }

  it should "respond with 404 for alphanumeric id" in {
    val resp = get(s"/v1/entities/nonexistent")
    assert(resp.status === Status.NotFound)
  }

  "GET /entities/id/script" should "respond with 200 and a script" in {
    val resp = get(s"/v1/entities/2/script")
    assert(resp.status === Status.Ok)
    assert(
      resp
        .as[Script]
        .unsafeRunSync()
        .steps
        .head
        .text === Some("Hello")
    )
  }

  it should "respond with 404 for nonexistent id" in {
    val resp = get(s"/v1/entities/0/script")
    assert(resp.status === Status.NotFound)
  }

  "PUT /entities/id" should "successfully edit an entity" in {
    val meditator = Meditator(
      Entity(None, "friendTest", "summary", "script", EntityType.Friend),
      Entity(None, "meditationTest", "summary", "script", EntityType.Meditation)
    )
    val resp = post(s"/v1/meditators", meditator)

    assert(resp.status === Status.Ok)
    val id = resp.as[Meditator].unsafeRunSync().meditation.id.get

    val resp2 = put(
      s"/v1/entities/${id}",
      Entity(
        Some(id),
        "editedName",
        "editedSummary",
        "editedScript",
        EntityType.Friend
      )
    )
    assert(resp2.status === Status.Ok)

    val resp3 = get(s"/v1/entities/$id")
    assert(resp3.status === Status.Ok)
    assert(resp3.as[Entity].unsafeRunSync().name !== "editedEntity")
    assert(resp3.as[Entity].unsafeRunSync().summary === "editedSummary")
    assert(resp3.as[Entity].unsafeRunSync().script === "editedScript")
    assert(resp3.as[Entity].unsafeRunSync().`type` === EntityType.Meditation)
  }

  private[this] def get(s: String): Response[IO] = {
    val req = GET(Uri.unsafeFromString(s)).unsafeRunSync()
    EntityEndpoints
      .endpoints(entities)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def put(s: String, entity: Entity): Response[IO] = {
    val req = PUT(entity, Uri.unsafeFromString(s)).unsafeRunSync()
    EntityEndpoints
      .endpoints(entities)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def post(s: String, meditator: Meditator): Response[IO] = {
    val req = POST(meditator, Uri.unsafeFromString(s)).unsafeRunSync()
    MeditatorEndpoints
      .endpoints(meditators)
      .orNotFound(req)
      .unsafeRunSync()
  }
}
