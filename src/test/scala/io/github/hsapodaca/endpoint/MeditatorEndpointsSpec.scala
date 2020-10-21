package io.github.hsapodaca.endpoint

import cats.effect.IO
import cats.implicits.toSemigroupKOps
import io.circe.generic.auto._
import io.github.hsapodaca.alg._
import io.github.hsapodaca.endpoint.repos.{entities, meditators}
import io.github.hsapodaca.web.{EntityEndpoints, MeditatorEndpoints}
import org.http4s.circe.CirceEntityCodec.{
  circeEntityDecoder,
  circeEntityEncoder
}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{Response, Uri}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MeditatorEndpointsSpec
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfter
    with Http4sDsl[IO]
    with Http4sClientDsl[IO] {

  s"GET /meditators/id" should "respond with 200 and a body" in {
    val resp = get(s"/v1/meditators/1")
    assert(resp.status === Ok)
    assert(resp.as[Meditator].unsafeRunSync().friend.entityName === "J")
    assert(
      resp
        .as[Meditator]
        .unsafeRunSync()
        .meditation
        .entityName
        .startsWith("Leaves")
    )
  }

  "GET /meditators/$id" should "respond with 404 to the nonexistent id" in {
    val resp = get(s"/v1/meditators/0")
    assert(resp.status === NotFound)
  }

  s"POST /meditators" should "not create existing meditator" in {
    val resp = post(
      s"/v1/meditators",
      Meditator(
        Entity(Some(1L), "J", "...", "...", EntityType.Friend),
        Entity(Some(2L), "", "", "", EntityType.Meditation)
      )
    )
    assert(resp.status === Conflict)
  }

  it should "not succeed in creating a meditator (friend with meditation) record if there's conflict" in {
    val resp = post(
      "/v1/meditators",
      Meditator(
        Entity(None, "J", "", "...", EntityType.Friend),
        Entity(None, "A", "", "...", EntityType.Meditation)
      )
    )
    assert(resp.status === Conflict)
  }

  it should "not succeed in adding first entity record if adding second one fails" in {
    val resp = post(
      "/v1/meditators",
      Meditator(
        Entity(None, "SomeNonexistentEntity", "", "...", EntityType.Friend),
        Entity(None, "J", "", "...", EntityType.Meditation)
      )
    )
    assert(resp.status === Conflict)
    val resp2 = get("/v1/entities/meditations").as[List[Entity]].unsafeRunSync()
    assert(!resp2.map(_.entityName).contains("SomeNonexistentEntity"))
  }

  it should "not succeed in adding second entity record if adding first one fails" in {
    val resp = post(
      "/v1/meditators",
      Meditator(
        Entity(None, "SameNameTest", "", "...", EntityType.Friend),
        Entity(None, "SameNameTest", "", "...", EntityType.Meditation)
      )
    )
    assert(resp.status === BadRequest)
    val resp2 = get("/v1/entities/meditations").as[List[Entity]].unsafeRunSync()
    assert(!resp2.map(_.entityName).contains("SomeNonexistentEntity"))
  }

  it should "succeed in creating multiple meditator (friend with meditation) records" in {
    for { num <- 1 to 10 } {
      val resp = post(
        "/v1/meditators",
        Meditator(
          Entity(
            None,
            s"TestMeditatorFriend$num",
            "",
            "...",
            EntityType.Friend
          ),
          Entity(
            None,
            s"TestMeditatorMeditation$num",
            "",
            "...",
            EntityType.Meditation
          )
        )
      )
      assert(resp.status === Ok)
      assert(
        resp
          .as[Meditator]
          .unsafeRunSync()
          .meditation
          .entityName === s"TestMeditatorMeditation$num"
      )
    }
  }

  "DELETE /meditators" should "delete existing meditator" in {
    val resp = post(
      "/v1/meditators",
      Meditator(
        Entity(None, "TestFriend", "Summary", "Script", EntityType.Friend),
        Entity(
          None,
          "TestMeditation",
          "Summary",
          "Script",
          EntityType.Meditation
        )
      )
    )
    val friendId = resp.as[Meditator].unsafeRunSync().friend.id.get
    val meditationId = resp.as[Meditator].unsafeRunSync().meditation.id.get
    val resp2 = delete(s"/v1/meditators/$friendId")
    assert(resp2.as[Meditator].unsafeRunSync().friend.id.get === friendId)
    for {
      id <- List(friendId, meditationId)
    } {
      val resp3 = get(s"/v1/meditators/$id")
      assert(resp3.status === NotFound)
    }
  }

  it should "not find a nonexistent meditator" in {
    val resp1 = delete(s"/v1/meditators/0")
    assert(resp1.status === NotFound)
  }

  private[this] def get(s: String): Response[IO] = {
    val req = GET(Uri.unsafeFromString(s)).unsafeRunSync()
    (MeditatorEndpoints
      .endpoints(meditators) <+> EntityEndpoints
      .endpoints(entities))
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def delete(s: String): Response[IO] = {
    val req = DELETE(Uri.unsafeFromString(s)).unsafeRunSync()
    MeditatorEndpoints
      .endpoints(meditators)
      .orNotFound(req)
      .unsafeRunSync()
  }

  private[this] def post(
      s: String,
      meditator: Meditator
  ): Response[IO] = {
    val req = POST(meditator, Uri.unsafeFromString(s)).unsafeRunSync()
    MeditatorEndpoints
      .endpoints(meditators)
      .orNotFound(req)
      .unsafeRunSync()
  }
}
