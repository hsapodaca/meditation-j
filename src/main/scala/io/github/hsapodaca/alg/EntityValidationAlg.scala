package io.github.hsapodaca.alg

import cats.data.EitherT

trait EntityValidationAlg[F[_]] {
  def exists(
      id: Option[Long]
  ): EitherT[F, EntityNotFoundError.type, Unit]
}
