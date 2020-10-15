-- Scripted entities: ie therapists or meditations
CREATE TABLE entities(
  id BIGSERIAL primary key not null,
  entity_name TEXT not null,
  summary TEXT not null,
  type VARCHAR(30) not null,
  script text not null,
  UNIQUE(entity_name, type)
);
CREATE INDEX entities_type_index ON entities(type);

-- ie: therapists can have meditations
CREATE TABLE entity_relationships(
  id BIGSERIAL primary key not null,
  primary_entity_id BIGSERIAL not null,
  target_entity_id BIGSERIAL not null,
  type VARCHAR(30) not null,
  constraint fk_primary_entity foreign key(primary_entity_id) references entities(id) ON DELETE CASCADE,
  constraint fk_target_entity foreign key(target_entity_id) references entities(id) ON DELETE CASCADE,
  UNIQUE(primary_entity_id, target_entity_id, type)
)