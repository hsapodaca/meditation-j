package io.github.hsapodaca.alg

trait ScriptRepositoryAlg[F[_]] {

  def getScript(
      id: Long
  ): F[Option[Script]]

  def getScripts: F[List[Script]]

  def createScript(script: Script): F[Script]

  def updateScript(
      script: Script
  ): F[Option[Script]]

  def deleteScript(id: Long): F[Int]
}
