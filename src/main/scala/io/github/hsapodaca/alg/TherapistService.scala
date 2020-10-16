package io.github.hsapodaca.alg

import cats._
import cats.data.EitherT

class TherapistService[F[_]](
    entities: EntityService[F],
    relationships: RelationshipService[F]
) {
  def create(mr: MeditationReader)(implicit
      M: Monad[F]
  ): EitherT[F, ItemAlreadyExistsError.type, MeditationReader] =
    for {
      m <- entities.create(mr.meditation)
      meditationId = m.id.getOrElse(1L)
      t <- entities.create(mr.therapist)
      therapistId = t.id.getOrElse(1L)
      _ <- relationships.create(
          EntityRelationship(
            None,
            therapistId,
            meditationId,
            EntityRelationshipType.TherapistHasMeditation
          )
        )
      saved = MeditationReader(m, t)
    } yield saved

  def get(id: Long)(implicit
      M: Monad[F]
  ): EitherT[F, EntityNotFoundError.type, MeditationReader] =
    for {
      m <- entities.get(id)
      r <- relationships.getByEntityId(m.id.getOrElse(1L))
      t <- entities.get(r.targetEntityId)
      mr = MeditationReader(m, t)
    } yield mr
}

object TherapistService {
  def apply[F[_]](
      entities: EntityService[F],
      relationships: RelationshipService[F]
  ): TherapistService[F] =
    new TherapistService[F](entities, relationships)
}
