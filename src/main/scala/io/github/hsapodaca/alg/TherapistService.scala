package io.github.hsapodaca.alg

import cats._
import cats.data.EitherT
import io.github.hsapodaca.config._

class TherapistService[F[_]](
    entities: EntityService[F],
    relationships: RelationshipService[F]
) {

  def init()(implicit
      M: Monad[F]
  ): EitherT[F, EntityAlreadyExistsError, MeditationReader] = {
    val t = Entity(
      None,
      defaultTherapist.name,
      defaultTherapist.summary,
      defaultTherapist.script,
      EntityType.Therapist
    )
    val m = Entity(
      None,
      defaultMeditation.name,
      defaultMeditation.summary,
      defaultMeditation.script,
      EntityType.Meditation
    )
    val mr = MeditationReader(t, m)
    createMeditationReader(mr)
  }

  def createMeditationReader(mr: MeditationReader)(implicit
      M: Monad[F]
  ): EitherT[F, EntityAlreadyExistsError, MeditationReader] =
    for {
      m <- entities.create(mr.meditation)
      meditationId = m.id.getOrElse(1L)
      t <- entities.create(mr.therapist)
      therapistId = t.id.getOrElse(1L)
      _ <- EitherT.liftF(
        relationships.create(
          EntityRelationship(
            None,
            therapistId,
            meditationId,
            EntityRelationshipType.TherapistHasMeditation
          )
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
