package io.github.hsapodaca.repository

import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.EntityRelationship
import io.github.hsapodaca.doobie.testTransactor
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class RelationshipQueryTypeCheckSpec
    extends AnyFunSuite
    with Matchers
    with IOChecker {
  override def transactor: Transactor[IO] = testTransactor

  import RelationshipSQL._

  private implicit val relationship = Arbitrary[EntityRelationship] {
    for {
      primaryEntityId <- Gen.posNum[Long]
      targetEntityId <- Gen.posNum[Long]
      id <- Gen.option(Gen.posNum[Long])
    } yield EntityRelationship(id, primaryEntityId, targetEntityId)
  }

  test("Type check relationship queries") {
    relationship.arbitrary.sample.map { s =>
      check(select(s.id.getOrElse(1L)))
      check(select(0, 0))
      check(select(10, 100))
      check(insertValues(s))
    }
    check(select(1L))
    check(deleteFrom(1L))
  }
}
