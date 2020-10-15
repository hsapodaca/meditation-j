package io.github.hsapodaca.repository

import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import io.github.hsapodaca.alg.Script
import io.github.hsapodaca.doobie.testTransactor
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ScriptQueryTypeCheckSpec
  extends AnyFunSuite
    with Matchers
    with IOChecker {
  override def transactor: Transactor[IO] = testTransactor

  import ScriptSQL._

  private implicit val script = Arbitrary[Script] {
    for {
      entityId <- Gen.posNum[Long]
      script <- arbitrary[String]
      id <- Gen.option(Gen.posNum[Long])
    } yield Script(id, entityId, script)
  }

  test("Type check script queries") {
    script.arbitrary.sample.map { s =>
      check(select(s.id.getOrElse(1L)))
      check(select(s.entityId))
      check(select(0, 0))
      check(select(10, 100))
      s.id.foreach(id => check(ScriptSQL.updateValues(id, s)))
      check(insertValues(s))
    }
    check(select(1L))
    check(deleteFrom(1L))
  }
}
