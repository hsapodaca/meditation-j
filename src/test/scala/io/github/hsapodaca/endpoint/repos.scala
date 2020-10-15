package io.github.hsapodaca.endpoint

import cats.effect.IO
import doobie.Transactor
import io.github.hsapodaca.alg.{EntityService, EntityValidation, RelationshipService, TherapistService}
import io.github.hsapodaca.repository.db.testTransactor
import io.github.hsapodaca.repository.{EntityRepository, RelationshipRepository}

package object repos {
  val transactor: Transactor[IO] = testTransactor
  val entityRepo = EntityRepository[IO](transactor)
  val relationshipRepo = RelationshipRepository[IO](transactor)
  val entities = EntityService[IO](entityRepo, EntityValidation[IO](entityRepo))
  val relationships = RelationshipService[IO](relationshipRepo)
  val therapists = TherapistService[IO](entities, relationships)
}
