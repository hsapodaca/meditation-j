package io.github.hsapodaca.alg

import cats.Applicative
import cats.data.EitherT
import cats.syntax.all._

class ScriptValidation[F[_]: Applicative](
    repository: ScriptRepositoryAlg[F]
) extends ScriptValidationAlg[F] {
  override def exists(
      meditationId: Option[Long]
  ): EitherT[F, ScriptNotFoundError.type, Unit] =
    EitherT {
      meditationId match {
        case Some(id) =>
          repository.get(id) map {
            case Some(_) => Right(())
            case _       => Left(ScriptNotFoundError)
          }
        case _ =>
          Either
            .left[ScriptNotFoundError.type, Unit](ScriptNotFoundError)
            .pure[F]
      }
    }
}

object ScriptValidation {
  def apply[F[_]: Applicative](repository: ScriptRepositoryAlg[F]) =
    new ScriptValidation[F](repository)
}
