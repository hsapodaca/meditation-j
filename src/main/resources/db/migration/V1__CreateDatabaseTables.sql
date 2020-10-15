-- Entities: ie therapists available to speak with patient or meditations
CREATE TABLE entities(
   id           BIGSERIAL primary key    not null,
   entity_name  TEXT  not null unique,
   summary      TEXT not null,
   type         VARCHAR(30) not null
);
create index entities_type_index on entities(type);

-- Scripts for the entities
create table scripts(
   id           BIGSERIAL primary key     not null,
   entity_id    BIGSERIAL not null,
   script       text not null,
   constraint fk_scripts_therapist
      foreign key(entity_id)
         references entities(id)
);