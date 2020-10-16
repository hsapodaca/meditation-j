package io.github.hsapodaca.endpoint

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import io.github.hsapodaca.endpoint.repos.{entities, relationships}
import io.github.hsapodaca.alg._
import io.github.hsapodaca.web.EntityEndpoints
import org.http4s.{Method, Request, Response, Status, Uri}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EntitiesSpec
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
    val resp2 = get(s"/v1/entities/1")
    assert(resp2.status === Status.Ok)
    assert(!resp2.as[Entity].unsafeRunSync().entityName.isBlank)
  }

  s"GET /entities/nonexistent" should "respond with 404" in {
    val resp = get(s"/v1/entities/nonexistent")
    assert(resp.status === Status.NotFound)
  }

  s"POST /entities" should "not create existing type and name" in {
    val resp = post(
      s"/v1/entities",
      Entity(Some(1L), "J", "...", "...", EntityType.Therapist)
    )
    assert(resp.status === Status.Conflict)
  }

  s"PUT /entities/id" should "not update to existing name" in {
    val resp = post(
      s"/v1/entities",
      Entity(None, "Test", "Test", "Test", EntityType.Meditation)
    )
    assert(resp.status === Status.Ok)
    val id = resp.as[Entity].unsafeRunSync().id.get
    val resp2 = put(
      s"/v1/entities/$id",
      Entity(Some(id), "J", "...", "...", EntityType.Therapist)
    )
    assert(resp2.status === Status.BadRequest)
    val resp3 = delete(s"/v1/entities/$id")
    assert(resp3.status === Status.Ok)
  }

  s"POST, PUT and DELETE /entities" should "succeed" in {
    val resp = post(
      "/v1/entities",
      Entity(None, "Test", "Test", "Test", EntityType.Meditation)
    )
    assert(resp.status === Status.Ok)
    val id = resp.as[Entity].unsafeRunSync().id.get

    val resp2 = get(s"/v1/entities/$id")
    assert(resp2.status === Status.Ok)

    val resp3 = put(
      s"/v1/entities/$id",
      Entity(Some(id), "Test", "Test2", "Test", EntityType.Meditation)
    )
    assert(resp3.status === Status.Ok)

    val resp4 = get(s"/v1/entities/$id")
    val summary = resp4.as[Entity].unsafeRunSync().summary
    assert(summary === "Test2")

    val resp5 = delete(s"/v1/entities/$id")
    assert(resp5.status === Status.Ok)

    val resp6 = get(s"/v1/entities/$id")
    assert(resp6.status === Status.NotFound)
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

  private[this] def put(s: String, entity: Entity): Response[IO] = {
    val req = PUT(entity, Uri.unsafeFromString(s)).unsafeRunSync()
    EntityEndpoints
      .endpoints(entities)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private def clearData = {
    val allTherapists = entities.listTherapists(10000, 0).unsafeRunSync()
    val allMeditations = entities.listMeditations(10000, 0).unsafeRunSync()
    val seededTherapists = allTherapists.filter(_.entityName == "J")
    val seededMeditations =
      allMeditations.filter(_.entityName == "Leaves on a Stream Meditation")

    val seededEntityIds = (seededTherapists ++ seededMeditations).map(_.id)

    (allTherapists ++ allMeditations)
      .filterNot(i => seededEntityIds.contains(i.id))
      .foreach { entity =>
        entities.delete(entity.id.get).unsafeRunSync()
      }
  }
}
