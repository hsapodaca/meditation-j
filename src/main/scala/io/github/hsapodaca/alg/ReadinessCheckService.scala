package io.github.hsapodaca.alg

class ReadinessCheckService[F[_]] {
  def check(): StatusInfo = StatusInfo()
}
object ReadinessCheckService {
  def apply[F[_]](): ReadinessCheckService[F] = new ReadinessCheckService[F]()
}
