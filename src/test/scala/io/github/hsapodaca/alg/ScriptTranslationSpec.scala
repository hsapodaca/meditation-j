package io.github.hsapodaca.alg

import cats.effect.IO
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScriptTranslationSpec extends AnyFlatSpec with Matchers {

  val narrative = NarrativeTranslation[IO]

  "narrative generator" should "not parse pause instructions if there is no pause" in {
    val actions = narrative.parse("""Test
        |test2
        |test2
        |""".stripMargin)
    assert(actions.size === 3)
    actions.foreach { a =>
      assert(a.`type` === ActionType.Speech)
      assert(a.text.isDefined)
    }
  }

  for (
    (input, expected) <- Map(
      "[ Pause three seconds.]" -> 3,
      "[pause.]" -> 1,
      "[    Pause 1 second ]." -> 1
    )
  ) {
    it should s"parse $input" in {
      assert(narrative.parse(input).head.`type` === ActionType.Pause)
      assert(narrative.parse(input).head.waitFor === Some(expected))
    }
  }

  it should s"parse text" in {
    assert(narrative.parse("test").head.`type` === ActionType.Speech)
    assert(narrative.parse("test").head.waitFor === None)
    assert(narrative.parse("test").head.text === Some("test"))
  }

}
