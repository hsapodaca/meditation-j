package io.github.hsapodaca.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.syntax.all._
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import io.github.hsapodaca.alg.{Script, ScriptRepositoryAlg}

class ScriptRepository[F[_]](val xa: Transactor[F])(implicit
    ev: Bracket[F, Throwable]
) extends ScriptRepositoryAlg[F] {
  override def get(id: Long): F[Option[Script]] = {
    sql"SELECT id, entity_id, script FROM scripts WHERE id = $id"
      .query[Script]
      .option
      .transact(xa)
  }

  override def getByEntityId(entity_id: Long): F[Option[Script]] = {
    sql"SELECT id, entity_id, script FROM scripts WHERE entity_id = $entity_id"
      .query[Script]
      .option
      .transact(xa)
  }

  override def list: F[List[Script]] = {
    sql"SELECT id, entity_id, script FROM scripts"
      .query[Script]
      .to[List]
      .transact(xa)
  }

  override def create(script: Script): F[Script] = {
    sql"INSERT INTO script (entity_id, script) VALUES (${script.entityId}, ${script.script})".update
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
        sql"UPDATE script SET script_name = ${script.entityId}, summary = ${script.script} WHERE id = ${id}".update.run
          .as(script)
      }
      .value
      .transact(xa)

  override def delete(id: Long): F[Int] = {
    sql"DELETE FROM script WHERE id = $id".update.run.transact(xa)
  }
}

object ScriptRepository {
  def apply[F[_]](
      xa: Transactor[F]
  )(implicit ev: Bracket[F, Throwable]): ScriptRepository[F] =
    new ScriptRepository(xa)
}
