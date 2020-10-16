-- Scripted entities
create TABLE entities(
  id BIGSERIAL primary key not null,
  entity_name TEXT not null,
  summary TEXT not null,
  type VARCHAR(30) not null,
  script text not null,
  UNIQUE(entity_name)
);
create INDEX entities_type_index ON entities(type);



-- ie: friends can have meditations
create TABLE entity_relationships(
  id BIGSERIAL primary key not null,
  primary_entity_id BIGSERIAL not null,
  target_entity_id BIGSERIAL not null,
  type VARCHAR(30) not null,
  constraint fk_primary_entity foreign key(primary_entity_id) references entities(id) ON delete CASCADE,
  constraint fk_target_entity foreign key(target_entity_id) references entities(id) ON delete CASCADE,
  UNIQUE(primary_entity_id, target_entity_id, type)
)