package io.github.hsapodaca.alg

import cats.effect.IO
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScriptTranslationSpec extends AnyFlatSpec with Matchers {

  val narrative = NarrativeTranslation[IO]

  "narrative generator" should "not parse pause instructions if there is no pause" in {
    val e = Entity(
      None,
      "",
      "",
      """Test
      |test2
      |test2
      |""".stripMargin,
      EntityType.Meditation
    )
    val actions = narrative.parse(e)
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
      val entity = Entity(None, "", "", input, EntityType.Friend)
      assert(narrative.parse(entity).head.`type` === ActionType.Pause)
      assert(narrative.parse(entity).head.waitFor === Some(expected))
    }
  }

  it should s"parse text" in {
    val e = Entity(None, "", "", "test", EntityType.Friend)
    val resp = narrative.parse(e)
    assert(resp.head.`type` === ActionType.Speech)
    assert(resp.head.waitFor === None)
    assert(resp.head.text === Some("test"))
  }
}
