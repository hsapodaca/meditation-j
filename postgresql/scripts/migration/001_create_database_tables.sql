-- Entities: ie therapists available to speak with patient or meditations
create table entities(
   id int primary key    not null,
   entity_name text  not null unique,
   summary        text,
   type           varchar(30) not null
);
create index entities_type_index on entities(type);

-- Scripts for the entities
create table scripts(
   id int primary key     not null,
   entity_id              int,
   script                 text,
   constraint fk_scripts_therapist
      foreign key(entity_id)
         references entities(id)
);