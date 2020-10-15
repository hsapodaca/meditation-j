-- Scripts for the entities
CREATE TABLE scripts(
  id BIGSERIAL primary key not null,
  script text not null
);

-- Scripted entities: ie therapists or meditations
CREATE TABLE entities(
  id BIGSERIAL primary key not null,
  entity_name TEXT not null,
  summary TEXT not null,
  type VARCHAR(30) not null,
  script_id BIGSERIAL,
  constraint fk_scripts_entity foreign key(script_id) references scripts(id),
  UNIQUE(entity_name, type)
);
CREATE INDEX entities_type_index ON entities(type);