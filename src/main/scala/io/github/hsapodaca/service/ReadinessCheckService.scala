package io.github.hsapodaca.service

import cats._
import io.github.hsapodaca.alg.StatusInfo

class ReadinessCheckService[F[_]] {
  def check(): StatusInfo = StatusInfo()
}
object ReadinessCheckService {
  def apply[F[_]](): ReadinessCheckService[F] = new ReadinessCheckService[F]()
}
