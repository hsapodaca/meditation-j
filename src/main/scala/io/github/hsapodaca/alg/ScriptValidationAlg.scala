package io.github.hsapodaca.alg

import cats.data.EitherT

trait ScriptValidationAlg[F[_]] {
  def exists(
      entity: Option[Long]
  ): EitherT[F, ScriptNotFoundError.type, Unit]
}
