package io.github.hsapodaca

import cats.syntax.all._
import cats.effect.{Async, Blocker, ContextShift, Effect, IO, Resource}
import config._
import _root_.doobie.Transactor

import scala.concurrent.ExecutionContext

package object doobie {

  lazy val testEc = ExecutionContext.Implicits.global

  implicit lazy val testCs = IO.contextShift(testEc)

  lazy val testTransactor = initializedTransactor[IO].unsafeRunSync()

  def getTransactor[F[_]: Async: ContextShift](
      cfg: DatabaseConnection
  ): Transactor[F] =
    Transactor.fromDriverManager[F](
      cfg.driver,
      cfg.url,
      cfg.user,
      cfg.password
    )

  def initializedTransactor[F[_]: Effect: Async: ContextShift]
      : F[Transactor[F]] =
    for {
      _ <- DatabaseConfig.init(config.databaseConnection)
    } yield getTransactor(config.databaseConnection)
}
