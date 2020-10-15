package io.github.hsapodaca.endpoint

import cats.effect.IO
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.{
  EntityService,
  EntityValidation,
  RelationshipService,
  TherapistService
}
import io.github.hsapodaca.repository.db.testTransactor
import io.github.hsapodaca.repository.{EntityRepository, RelationshipRepository}
import io.github.hsapodaca.web.ReadinessCheckEndpoints
import org.http4s._
import org.http4s.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReadinessCheck_GET_Spec extends AnyFlatSpec with Matchers {

  val transactor: Transactor[IO] = testTransactor
  val entityRepo = EntityRepository[IO](transactor)
  val relationshipRepo = RelationshipRepository[IO](transactor)
  val entities = EntityService[IO](entityRepo, EntityValidation[IO](entityRepo))
  val relationships = RelationshipService[IO](relationshipRepo)
  val therapists = TherapistService[IO](entities, relationships)

  "GET /status" should "respond with 200" in {
    assert(resp.status === Status.Ok)
  }

  it should "return status details" in {
    assert(
      resp.as[String].unsafeRunSync() ===
        "{\"status\":\"Up\",\"defaultMeditation\":\"Leaves on a Stream Meditation\",\"defaultTherapist\":\"J\"}"
    )
  }

  private[this] val resp: Response[IO] = get(uri"/status")

  private[this] def get(uri: Uri): Response[IO] = {
    val req = Request[IO](Method.GET, uri)
    ReadinessCheckEndpoints
      .endpoints(therapists)
      .orNotFound(req)
      .unsafeRunSync()
  }
}
