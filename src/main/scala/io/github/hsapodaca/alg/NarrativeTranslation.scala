package io.github.hsapodaca.alg

class NarrativeTranslation[F[_]] extends NarrativeTranslationAlg[F] {
  private val pauseSeconds = """(?i)\[.*Pause ([0-9]+) second.*\]""".r
  private val pauseSecondsWords = """(?i)\[.*Pause ([A-z]+) second.*\]""".r
  private val pause = """(?i).*Pause.*"""

  val numbers = Map(
    1 -> "one",
    2 -> "two",
    3 -> "three",
    4 -> "four",
    5 -> "five",
    6 -> "six",
    7 -> "seven",
    8 -> "eight",
    9 -> "nine",
    10 -> "ten"
  )

  override def parse(e: Entity): List[Action] =
    e.script
      .split(System.lineSeparator())
      .toList
      .map {
        case pauseSeconds(p) =>
          Action(ActionType.Pause, Some(p.toInt), None)
        case pauseSecondsWords(p) =>
          val num = numbers.find(_._2 == p.toLowerCase).map(_._1)
          Action(ActionType.Pause, num, None)
        case s if s.matches(pause) =>
          Action(ActionType.Pause, Some(1), None)
        case s => Action(ActionType.Speech, None, Some(s))
      }
}

object NarrativeTranslation {
  def apply[F[_]]: NarrativeTranslation[F] = new NarrativeTranslation[F]
}
