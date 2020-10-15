package io.github.hsapodaca.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.syntax.all._
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import io.github.hsapodaca.alg.{Script, ScriptRepositoryAlg}

private object ScriptSQL {

  def select(id: Long): Query0[Script] =
    sql"""
    SELECT id, entity_id, script 
    FROM scripts 
    WHERE id = $id
    """.query[Script]

  def selectByEntityId(entity_id: Long): Query0[Script] =
    sql"""
    SELECT id, entity_id, script 
    FROM scripts 
    WHERE entity_id = $entity_id
    """.query[Script]

  def select(limit: Int, offset: Int): Query0[Script] =
    sql"""
    SELECT id, entity_id, script
    FROM scripts
    LIMIT ${limit.toLong} OFFSET ${offset.toLong}
    """.query[Script]

  def insertValues(s: Script): Update0 =
    sql"""
    INSERT INTO scripts (entity_id, script)
    VALUES (${s.entityId}, ${s.script})
    """.update

  def deleteFrom(id: Long): Update0 =
    sql"""
    DELETE FROM scripts
    WHERE id = $id
    """.update

  def updateValues(id: Long, s: Script): Update0 =
    sql"""
    UPDATE scripts
    SET entity_id = ${s.entityId}, script = ${s.script}
    WHERE id = $id
    """.update
}

class ScriptRepository[F[_]](val xa: Transactor[F])(implicit
    ev: Bracket[F, Throwable]
) extends ScriptRepositoryAlg[F] {

  import ScriptSQL._

  override def get(id: Long): F[Option[Script]] = select(id).option.transact(xa)

  override def getByEntityId(entity_id: Long): F[Option[Script]] =
    selectByEntityId(entity_id).option.transact(xa)

  override def list(limit: Int, offset: Int): F[List[Script]] =
    select(limit, offset).to[List].transact(xa)

  override def create(script: Script): F[Script] = {
    insertValues(script)
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => script.copy(id = Some(id)))
      .transact(xa)
  }

  override def update(
      script: Script
  ): F[Option[Script]] =
    OptionT
      .fromOption[ConnectionIO](script.id)
      .semiflatMap { id =>
        updateValues(id, script).run
          .as(script)
      }
      .value
      .transact(xa)

  override def delete(id: Long): F[Int] = {
    deleteFrom(id).run.transact(xa)
  }
}

object ScriptRepository {
  def apply[F[_]](
      xa: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): ScriptRepository[F] =
    new ScriptRepository(xa)
}
