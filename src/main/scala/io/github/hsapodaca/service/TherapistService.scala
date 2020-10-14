package io.github.hsapodaca.service

import cats._
import cats.data.EitherT
import io.github.hsapodaca.alg.{
  TherapistRepositoryAlg,
  Therapist,
  TherapistAlreadyExistsError,
  TherapistNotFoundError
}

class TherapistService[F[_]](repository: TherapistRepositoryAlg[F]) {
  def create(Therapist: Therapist)(implicit
      M: Monad[F]
  ): EitherT[F, TherapistAlreadyExistsError, Therapist] =
    for {
      //_ <- validation.doesNotExist(Therapist)
      saved <- EitherT.liftF(repository.create(Therapist))
    } yield saved

  def update(Therapist: Therapist)(implicit
      M: Monad[F]
  ): EitherT[F, TherapistNotFoundError.type, Therapist] =
    for {
      //_ <- validation.exists(Therapist.id)
      saved <- EitherT.fromOptionF(
        repository.update(Therapist),
        TherapistNotFoundError
      )
    } yield saved

  def get(id: Long)(implicit
      F: Functor[F]
  ): EitherT[F, TherapistNotFoundError.type, Therapist] =
    EitherT.fromOptionF(repository.get(id), TherapistNotFoundError)

  def delete(id: Long)(implicit F: Functor[F]): F[Int] = {
    repository.delete(id)
  }

}
object TherapistService {
  def apply[F[_]](
      repository: TherapistRepositoryAlg[F]
  ): TherapistService[F] =
    new TherapistService[F](repository)
}
