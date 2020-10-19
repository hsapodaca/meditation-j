package io.github.hsapodaca.alg

import cats.Applicative
import cats.data.EitherT
import cats.effect.Bracket
import cats.syntax.all._
import doobie.implicits._
import doobie.util.transactor.Transactor

class MeditatorValidation[F[_]](
    repository: EntityRepositoryAlg[F],
    transactor: Transactor[F]
)(implicit ev: Bracket[F, Throwable])
    extends MeditatorValidationAlg[F] {

  override def doesNotExist(
      m: Meditator
  ): EitherT[F, MeditatorAlreadyExistsError.type, Unit] = {
    val action = EitherT {
      for {
        f <- repository.get(m.friend.entityName)
        m <- repository.get(m.meditation.entityName)
      } yield (f, m) match {
        case (Some(_), _) => Left(MeditatorAlreadyExistsError)
        case (_, Some(_)) => Left(MeditatorAlreadyExistsError)
        case _            => Right(())
      }
    }
    action.transact(transactor)
  }

  def uniqueEntityNames(
      meditator: Meditator
  ): EitherT[F, MeditatorEntityNamesMatchError.type, Unit] =
    EitherT {
      (
        meditator.meditation.entityName.toLowerCase,
        meditator.friend.entityName.toLowerCase
      ) match {
        case (m, f) if m == f =>
          Either
            .left[MeditatorEntityNamesMatchError.type, Unit](
              MeditatorEntityNamesMatchError
            )
            .pure[F]
        case _ =>
          Either.right[MeditatorEntityNamesMatchError.type, Unit](()).pure[F]
      }
    }

  override def exists(
      entityId: Option[Long]
  ): EitherT[F, MeditatorNotFoundError.type, Unit] = {
    EitherT {
      entityId match {
        case Some(id) =>
          val action = for {
            e <- repository.get(id)
          } yield e match {
            case Some(_) => Right(())
            case _       => Left(MeditatorNotFoundError)
          }
          action.transact(transactor)
        case _ =>
          Either
            .left[MeditatorNotFoundError.type, Unit](MeditatorNotFoundError)
            .pure[F]
      }
    }
  }
}

object MeditatorValidation {
  def apply[F[_]: Applicative](
      repository: EntityRepositoryAlg[F],
      transactor: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]) =
    new MeditatorValidation[F](repository, transactor)
}
