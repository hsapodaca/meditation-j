package io.github.hsapodaca.endpoint

import cats.effect.IO
import io.circe.generic.auto._
import io.github.hsapodaca.alg._
import io.github.hsapodaca.endpoint.repos.{entities, relationships}
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

  before {
    clearData
  }

  after {
    clearData
  }

  s"GET /entities/id/relationship" should "respond with 200 and a list of entities" in {
    val resp = get(s"/v1/entities/1/relationship")
    assert(resp.status === Status.Ok)
    assert(resp.as[List[EntityRelationship]].unsafeRunSync().head.primaryEntityId === 1)
  }

  s"GET /relationships/id" should "respond with 200 and a relationship" in {
    val resp = get(s"/v1/relationships/1")
    assert(resp.status === Status.Ok)
    assert(resp.as[List[EntityRelationship]].unsafeRunSync().head.primaryEntityId === 1)
  }

  s"GET /relationships/nonexistent" should "respond with 404" in {
    val resp = get(s"/v1/relationships/nonexistent")
    assert(resp.status === Status.NotFound)
  }

  s"POST /relationships" should "not create existing relationship" in {
    val resp = post(
      s"/v1/relationships",
      EntityRelationship(None, 1L, 2L, EntityRelationshipType.TherapistHasMeditation)
    )
    assert(resp.status === Status.Conflict)
  }

  s"POST, PUT and DELETE /entities and /relationship" should "succeed" in {
    val resp = post(
      "/v1/entities",
      Entity(None, "A", "Test", "Test", EntityType.Therapist)
    )
    assert(resp.status === Status.Ok)
    val id = resp.as[Entity].unsafeRunSync().id.get

    val resp2 = post(
      "/v1/entities",
      Entity(None, "Test", "Test", "Test", EntityType.Meditation)
    )
    assert(resp2.status === Status.Ok)
    val targetId = resp2.as[Entity].unsafeRunSync().id.get

    val resp3 = post(
      s"/v1/relationships",
      EntityRelationship(None, id, targetId, EntityRelationshipType.TherapistHasMeditation))
    assert(resp3.status === Status.Ok)

    val resp4 = delete(s"/v1/entities/$id")
    assert(resp4.status === Status.Ok)

    val resp5 = delete(s"/v1/entities/$targetId")
    assert(resp5.status === Status.Ok)

    val resp6 = get(s"/v1/entities/$id/relationship")
    assert(resp6.status === Status.NotFound)
  }

  private[this] def get(s: String): Response[IO] = {
    val req = GET(Uri.unsafeFromString(s)).unsafeRunSync()
    RelationshipEndpoints
      .endpoints(relationships)
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

  private[this] def post(s: String, entityRelationship: EntityRelationship): Response[IO] = {
    val req = POST(entityRelationship, Uri.unsafeFromString(s)).unsafeRunSync()
    RelationshipEndpoints
      .endpoints(relationships)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def delete(s: String): Response[IO] = {
    val req = DELETE(Uri.unsafeFromString(s)).unsafeRunSync()
    RelationshipEndpoints
      .endpoints(relationships)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def put(s: String, entityRelationship: EntityRelationship): Response[IO] = {
    val req = PUT(entityRelationship, Uri.unsafeFromString(s)).unsafeRunSync()
    RelationshipEndpoints
      .endpoints(relationships)
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
