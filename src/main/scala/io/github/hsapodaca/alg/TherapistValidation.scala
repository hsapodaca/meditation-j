package io.github.hsapodaca.alg

import cats.Applicative
import cats.data.EitherT
import cats.syntax.all._

class TherapistValidation[F[_]: Applicative](
    repository: TherapistRepositoryAlg[F]
) extends TherapistValidationAlg[F] {
  override def doesNotExist(
      therapist: Therapist
  ): EitherT[F, TherapistAlreadyExistsError, Unit] =
    EitherT {
      repository.get(therapist.entityName) map {
        case Some(_) => Right(())
        case _       => Left(TherapistAlreadyExistsError(therapist))
      }
    }

  override def exists(
      therapistId: Option[Long]
  ): EitherT[F, TherapistNotFoundError.type, Unit] =
    EitherT {
      therapistId match {
        case Some(id) =>
          repository.get(id) map {
            case Some(_) => Right(())
            case _       => Left(TherapistNotFoundError)
          }
        case _ =>
          Either
            .left[TherapistNotFoundError.type, Unit](TherapistNotFoundError)
            .pure[F]
      }
    }
}


