package io.github.hsapodaca.repository

import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.Meditation
import io.github.hsapodaca.doobie.testTransactor
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MeditationQueryTypeCheckSpec
    extends AnyFunSuite
    with Matchers
    with IOChecker {
  override def transactor: Transactor[IO] = testTransactor
  import MeditationSQL._

  private implicit val meditation = Arbitrary[Meditation] {
    for {
      entityName <- Gen.nonEmptyListOf(Gen.asciiPrintableChar).map(_.mkString)
      summary <- arbitrary[String]
      id <- Gen.option(Gen.posNum[Long])
    } yield Meditation(id, entityName, summary)
  }

  test("Type check meditation queries") {
    meditation.arbitrary.sample.map { m =>
      check(select(m.id.get))
      check(select(m.entityName))
      check(select(0, 0))
      check(select(10, 100))
      m.id.foreach(id => check(MeditationSQL.updateValues(id, m)))
      check(insertValues(m))
    }
    check(select(1L))
    check(deleteFrom(1L))
  }
}
