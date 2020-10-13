package com.mindful.meditation

package object model {

  case class Meditation(
      id: Option[Long],
      entityName: String,
      summary: String
  )
  case class Therapist(id: Option[Long], entityName: String, summary: String)

  case class Script(
      id: Option[Long],
      entityId: Long,
      script: String
  )
  case object EntityNotFoundError
  case object ScriptNotFoundError
}
