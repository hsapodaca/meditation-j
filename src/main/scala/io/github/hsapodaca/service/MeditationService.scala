package io.github.hsapodaca.service

import cats._
import cats.data.EitherT
import io.github.hsapodaca.alg.{Meditation, MeditationAlreadyExistsError, MeditationNotFoundError, MeditationRepositoryAlg, MeditationValidationAlg}

class MeditationService[F[_]](
    repository: MeditationRepositoryAlg[F],
    validation: MeditationValidationAlg[F]
) {
  def create(entity: Meditation)(implicit
                                 M: Monad[F]
  ): EitherT[F, MeditationAlreadyExistsError, Meditation] =
    for {
      _ <- validation.doesNotExist(entity)
      saved <- EitherT.liftF(repository.create(entity))
    } yield saved

  def update(entity: Meditation)(implicit
      M: Monad[F]
  ): EitherT[F, MeditationNotFoundError.type, Meditation] =
    for {
      _ <- validation.exists(entity.id)
      saved <- EitherT.fromOptionF(
        repository.update(entity),
        MeditationNotFoundError
      )
    } yield saved

  def get(id: Long)(implicit
      F: Functor[F]
  ): EitherT[F, MeditationNotFoundError.type, Meditation] =
    EitherT.fromOptionF(repository.get(id), MeditationNotFoundError)

  def delete(id: Long): F[Int] = {
    repository.delete(id)
  }

  def list(pageSize: Int, offset: Int): F[List[Meditation]] = {
    repository.list(pageSize, offset)
  }
}
object MeditationService {
  def apply[F[_]](
      repository: MeditationRepositoryAlg[F],
      validation: MeditationValidationAlg[F]
  ): MeditationService[F] =
    new MeditationService[F](repository, validation)
}
