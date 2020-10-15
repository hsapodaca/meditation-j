package io.github.hsapodaca.alg

import enumeratum._
import io.github.hsapodaca.config

// Database
case class Entity(
    id: Option[Long],
    entityName: String,
    summary: String,
    script: String,
    `type`: EntityType
)

case class EntityRelationship(
    id: Option[Long],
    primaryEntityId: Long,
    targetEntityId: Long,
    `type`: EntityRelationshipType =
      EntityRelationshipType.TherapistHasMeditation
)

// Database and JSON
sealed trait EntityType extends EnumEntry
case object EntityType
    extends Enum[EntityType]
    with CirceEnum[EntityType]
    with DoobieEnum[EntityType] {
  case object Therapist extends EntityType
  case object Meditation extends EntityType
  val values = findValues
}

sealed trait EntityRelationshipType extends EnumEntry
case object EntityRelationshipType
    extends Enum[EntityRelationshipType]
    with CirceEnum[EntityRelationshipType]
    with DoobieEnum[EntityRelationshipType] {
  case object TherapistHasMeditation extends EntityRelationshipType
  val values = findValues
}

case class StatusInfo(
    status: String = "UP",
    defaultMeditation: String = config.defaultMeditation.name,
    defaultTherapist: String = config.defaultTherapist.name
)

case class MeditationReader(therapist: Entity, meditation: Entity)

// Validation Errors
sealed trait ValidationError extends Product with Serializable
case object EntityNotFoundError extends ValidationError
case class EntityAlreadyExistsError(m: Entity) extends ValidationError
