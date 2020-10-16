package io.github.hsapodaca.alg

import cats.Applicative
import cats.data.EitherT
import cats.syntax.all._

class RelationshipValidation[F[_]: Applicative](
    repository: RelationshipRepositoryAlg[F]
) extends RelationshipValidationAlg[F] {
  override def doesNotExist(
      r: EntityRelationship
  ): EitherT[F, ItemAlreadyExistsError.type, Unit] =
    EitherT {
      repository.getByEntityId(r.primaryEntityId) map {
        case Some(_) => Left(ItemAlreadyExistsError)
        case _       => Right(())
      }
    }
}

object RelationshipValidation {
  def apply[F[_]: Applicative](repository: RelationshipRepositoryAlg[F]) =
    new RelationshipValidation[F](repository)
}
