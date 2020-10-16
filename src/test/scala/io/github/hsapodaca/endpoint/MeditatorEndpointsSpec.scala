package io.github.hsapodaca.endpoint

import cats.effect.IO
import cats.implicits.toSemigroupKOps
import io.circe.generic.auto._
import io.github.hsapodaca.alg._
import io.github.hsapodaca.endpoint.repos.{
  clearData,
  entities,
  meditators,
  relationships
}
import io.github.hsapodaca.web.{
  EntityEndpoints,
  MeditatorEndpoints,
  RelationshipEndpoints
}
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

class MeditatorEndpointsSpec
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

  s"GET /meditators/id" should "respond with 200 and a body" in {
    val resp = get(s"/v1/meditators/1")
    assert(resp.status === Status.Ok)
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

  s"GET /meditators/nonexistent" should "respond with 404" in {
    val resp = get(s"/v1/meditators/nonexistent")
    assert(resp.status === Status.NotFound)
  }

  s"POST /meditators" should "not create existing meditator" in {
    val resp = post(
      s"/v1/meditators",
      Meditator(
        Entity(Some(1L), "J", "...", "...", EntityType.Friend),
        Entity(Some(2L), "", "", "", EntityType.Meditation)
      )
    )
    assert(resp.status === Status.Conflict)
  }

  it should "not succeed in creating a meditator (friend with meditation) record if there's conflict" in {
    val resp = post(
      "/v1/meditators",
      Meditator(
        Entity(None, "J", "", "...", EntityType.Friend),
        Entity(None, "A", "", "...", EntityType.Meditation)
      )
    )
    assert(resp.status === Status.Conflict)
  }

  it should "succeed in creating a meditator (friend with meditation) record" in {
    val resp = post(
      "/v1/meditators",
      Meditator(
        Entity(None, "TestFriend", "", "...", EntityType.Friend),
        Entity(None, "TestMeditation", "", "...", EntityType.Meditation)
      )
    )
    assert(resp.status === Status.Ok)
  }

  private[this] def get(s: String): Response[IO] = {
    val req = GET(Uri.unsafeFromString(s)).unsafeRunSync()
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
