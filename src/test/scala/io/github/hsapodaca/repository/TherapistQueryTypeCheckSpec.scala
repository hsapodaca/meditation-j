package io.github.hsapodaca.repository

import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.Therapist
import io.github.hsapodaca.doobie.testTransactor
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TherapistQueryTypeCheckSpec
    extends AnyFunSuite
    with Matchers
    with IOChecker {
  override def transactor: Transactor[IO] = testTransactor
  import TherapistSQL._

  private implicit val therapist = Arbitrary[Therapist] {
    for {
      entityName <- Gen.nonEmptyListOf(Gen.asciiPrintableChar).map(_.mkString)
      summary <- arbitrary[String]
      id <- Gen.option(Gen.posNum[Long])
    } yield Therapist(id, entityName, summary)
  }

  test("Typecheck therapist queries") {
    therapist.arbitrary.sample.map { t =>
      check(select(t.id.get))
      check(select(t.entityName))
      check(select(0, 0))
      check(select(10, 100))
      t.id.foreach(id => check(TherapistSQL.updateValues(id, t)))
      check(insertValues(t))
    }
    check(select(1L))
    check(deleteFrom(1L))
  }
}
