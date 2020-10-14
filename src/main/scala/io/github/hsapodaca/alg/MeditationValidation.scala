package io.github.hsapodaca.alg

import cats.Applicative
import cats.data.EitherT
import cats.syntax.all._

class MeditationValidation[F[_]: Applicative](
    repository: MeditationRepositoryAlg[F]
) extends MeditationValidationAlg[F] {
  override def doesNotExist(
      meditation: Meditation
  ): EitherT[F, MeditationAlreadyExistsError, Unit] =
    EitherT {
      repository.get(meditation.entityName) map {
        case Some(_) => Right(())
        case _       => Left(MeditationAlreadyExistsError(meditation))
      }
    }

  override def exists(
      meditationId: Option[Long]
  ): EitherT[F, MeditationNotFoundError.type, Unit] =
    EitherT {
      meditationId match {
        case Some(id) =>
          repository.get(id) map {
            case Some(_) => Right(())
            case _       => Left(MeditationNotFoundError)
          }
        case _ =>
          Either
            .left[MeditationNotFoundError.type, Unit](MeditationNotFoundError)
            .pure[F]
      }
    }
}

object MeditationValidation {
  def apply[F[_]: Applicative](repository: MeditationRepositoryAlg[F]) =
    new MeditationValidation[F](repository)
}
