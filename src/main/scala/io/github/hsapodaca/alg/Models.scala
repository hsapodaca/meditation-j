package io.github.hsapodaca.alg

import java.time.LocalDateTime

import enumeratum._

case class Entity(
    id: Option[Long],
    entityName: String,
    summary: String,
    script: String,
    `type`: EntityType
)

case class Script(
    steps: List[Action]
)

case class Action(
    `type`: ActionType,
    waitFor: Option[Int],
    text: Option[String]
)

sealed trait ActionType extends EnumEntry
case object ActionType extends Enum[ActionType] with CirceEnum[ActionType] {
  val values = findValues
  case object Speech extends ActionType
  case object Pause extends ActionType
}

case class EntityRelationship(
    id: Option[Long],
    primaryEntityId: Long,
    targetEntityId: Long,
    `type`: EntityRelationshipType = EntityRelationshipType.FriendHasMeditation
)

sealed trait EntityType extends EnumEntry
case object EntityType
    extends Enum[EntityType]
    with CirceEnum[EntityType]
    with DoobieEnum[EntityType] {
  val values = findValues

  case object Friend extends EntityType
  case object Meditation extends EntityType
}

sealed trait EntityRelationshipType extends EnumEntry
case object EntityRelationshipType
    extends Enum[EntityRelationshipType]
    with CirceEnum[EntityRelationshipType]
    with DoobieEnum[EntityRelationshipType] {
  val values = findValues
  case object FriendHasMeditation extends EntityRelationshipType
}

sealed trait SystemStatus extends EnumEntry
case object SystemStatus
    extends Enum[SystemStatus]
    with CirceEnum[SystemStatus] {
  val values = findValues

  case object Up extends SystemStatus
}

case class StatusInfo(
    status: SystemStatus = SystemStatus.Up,
    meditation: String,
    friend: String
)

case class Meditator(friend: Entity, meditation: Entity)

case class Error(code: ErrorCode, title: String)
sealed trait ErrorCode extends EnumEntry
case object ErrorCode extends Enum[ErrorCode] with CirceEnum[ErrorCode] {
  val values = findValues
  case object MD400 extends ErrorCode
  case object MD404 extends ErrorCode
  case object MD500 extends ErrorCode
  case object MD409 extends ErrorCode
}

// Validation Errors
sealed trait ValidationError extends Product with Serializable
sealed trait MeditatorCreationError extends Product with Serializable
case object MeditatorNotFoundError extends ValidationError
case object EntityAlreadyExistsError
    extends ValidationError
    with MeditatorCreationError
case object EntityNotFoundError extends ValidationError
case object MeditatorEntityNamesMatchError
    extends ValidationError
    with MeditatorCreationError
case object MeditatorAlreadyExistsError
    extends ValidationError
    with MeditatorCreationError
case object MeditatorCreationFailedError
    extends ValidationError
    with MeditatorCreationError
