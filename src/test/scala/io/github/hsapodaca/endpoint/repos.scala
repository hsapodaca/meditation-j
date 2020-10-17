package io.github.hsapodaca.endpoint

import cats.effect.IO
import doobie.Transactor
import io.github.hsapodaca.alg.service.{EntityService, MeditatorService, RelationshipService}
import io.github.hsapodaca.alg.EntityValidation
import io.github.hsapodaca.repository.db.testTransactor
import io.github.hsapodaca.repository.{EntityRepository, RelationshipRepository}

package object repos {
  val transactor: Transactor[IO] = testTransactor
  val entityRepo = EntityRepository[IO]
  val relationshipRepo = RelationshipRepository[IO]
  val entities = EntityService[IO](entityRepo, EntityValidation[IO](entityRepo, transactor), transactor)
  val relationships = RelationshipService[IO](
    relationshipRepo,
    transactor
  )
  val meditators = MeditatorService[IO](entities, relationships, transactor)

  def clearData = {
    val allFriends = entities.listFriends(10000, 0).unsafeRunSync()
    val allMeditations = entities.listMeditations(10000, 0).unsafeRunSync()
    val seededFriends = allFriends.filter(_.entityName == "J")
    val seededMeditations =
      allMeditations.filter(_.entityName == "Leaves on a Stream Meditation")

    val seededEntityIds = (seededFriends ++ seededMeditations).map(_.id)

    (allFriends ++ allMeditations)
      .filterNot(i => seededEntityIds.contains(i.id))
      .foreach { entity =>
        entities.deleteAndTransact(entity.id.get).unsafeRunSync()
      }
  }
}
