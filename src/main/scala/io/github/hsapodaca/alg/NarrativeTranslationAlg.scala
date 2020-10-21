package io.github.hsapodaca.alg

trait NarrativeTranslationAlg[F[_]] {
  def parse(e: Entity): List[Action]
}
