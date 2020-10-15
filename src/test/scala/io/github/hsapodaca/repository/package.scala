package io.github.hsapodaca.repository

import cats.syntax.all._
import cats.effect.{Async, ContextShift, Effect, IO}
import doobie.Transactor
import io.github.hsapodaca.config
import io.github.hsapodaca.config.{DatabaseConfig, DatabaseConnection}

import scala.concurrent.ExecutionContext

package object db {

  lazy val testEc = ExecutionContext.Implicits.global

  implicit lazy val testCs = IO.contextShift(testEc)

  lazy val testTransactor = initializedTransactor[IO].unsafeRunSync()

  def initializedTransactor[F[_]: Effect: Async: ContextShift]
      : F[Transactor[F]] =
    for {
      _ <- DatabaseConfig.init(config.databaseConnection)
    } yield getTransactor(config.databaseConnection)

  def getTransactor[F[_]: Async: ContextShift](
      cfg: DatabaseConnection
  ): Transactor[F] =
    Transactor.fromDriverManager[F](
      cfg.driver,
      cfg.url,
      cfg.user,
      cfg.password
    )
}
