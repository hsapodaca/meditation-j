package io.github.hsapodaca.repository

import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.{Entity, EntityType}
import io.github.hsapodaca.doobie.testTransactor
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EntityQueryTypeCheckSpec
    extends AnyFunSuite
    with Matchers
    with IOChecker {
  override def transactor: Transactor[IO] = testTransactor
  import EntitySQL._

  private implicit val entity = Arbitrary[Entity] {
    for {
      entityName <- Gen.nonEmptyListOf(Gen.asciiPrintableChar).map(_.mkString)
      summary <- arbitrary[String]
      id <- Gen.option(Gen.posNum[Long])
      script <- arbitrary[String]
      entityType <- Gen.oneOf(EntityType.Therapist, EntityType.Meditation)
    } yield Entity(id, entityName, summary, script, entityType )
  }

  test("Check entity queries") {
    entity.arbitrary.sample.map { e =>
      check(select(e.id.getOrElse(1L)))
      check(select(e.entityName))
      List(EntityType.Therapist, EntityType.Meditation).foreach { t =>
        check(select(t, 0, 0))
        check(select(t, 10, 100))
      }
      e.id.foreach(id => check(EntitySQL.updateValues(id, e)))
      check(insertValues(e))
    }
    check(select(1L))
    check(deleteFrom(1L))
  }
}
