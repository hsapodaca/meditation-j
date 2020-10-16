package io.github.hsapodaca.alg

import cats.data.EitherT

trait RelationshipValidationAlg[F[_]] {
  def doesNotExist(
      r: EntityRelationship
  ): EitherT[F, EntityAlreadyExistsError, Unit]
}
