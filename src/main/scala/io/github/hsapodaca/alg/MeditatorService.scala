package io.github.hsapodaca.alg

import cats._
import cats.data.EitherT

class MeditatorService[F[_]](
    entities: EntityService[F],
    relationships: RelationshipService[F]
) {
  def create(mr: Meditator)(implicit
                            M: Monad[F]
  ): EitherT[F, ItemAlreadyExistsError.type, Meditator] = {
    for {
      f <- entities.create(mr.friend)
      fId = f.id.get
      m <- entities.create(mr.meditation)
      mId = m.id.get
      saved <- relationships.create(
        EntityRelationship(
          None,
          fId,
          mId,
          EntityRelationshipType.FriendHasMeditation
        )
      )
    } yield Meditator(f, m)
  }

  def get(id: Long)(implicit
      M: Monad[F]
  ): EitherT[F, EntityNotFoundError.type, Meditator] =
    for {
      p <- entities.get(id)
      t <- entities.getByParentId(id)
      mr = Meditator(p, t)
    } yield mr
}

object MeditatorService {
  def apply[F[_]](
      entities: EntityService[F],
      relationships: RelationshipService[F]
  ): MeditatorService[F] =
    new MeditatorService[F](entities, relationships)
}
