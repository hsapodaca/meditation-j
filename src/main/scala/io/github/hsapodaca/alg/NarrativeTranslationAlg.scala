package io.github.hsapodaca.alg

trait NarrativeTranslationAlg[F[_]] {
  def generate(s: Option[Entity]): Option[Script]
  def parse(s: String): List[Action]
}

