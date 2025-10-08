SET SCHEMA 'slaughter_house';
SET search_path = "slaughter_house";

DROP SCHEMA IF EXISTS slaughter_house CASCADE;

CREATE SCHEMA IF NOT EXISTS slaughter_house;

CREATE TABLE animal(
    id SERIAL PRIMARY KEY,
    weight int,
    type varchar(20)
);

CREATE TABLE animal_part(
    id SERIAL PRIMARY KEY,
    weight int,
    typePart varchar(20),
    animal_id int REFERENCES animal(id)
);

CREATE TABLE tray(
    id SERIAL PRIMARY KEY,
    max_weight int
);

CREATE TABLE animal_part_tray(
    animal_part_id int PRIMARY KEY REFERENCES animal_part(id),
    tray_id int REFERENCES tray(id)
);

CREATE TABLE product(
    id SERIAL PRIMARY KEY
);

CREATE TABLE package(
    id int PRIMARY KEY REFERENCES product(id)
);

CREATE TABLE half_animal(
    id int PRIMARY KEY REFERENCES product(id)
);

CREATE TABLE product_tray(
    package_id int REFERENCES package(id),
    half_animal_id int REFERENCES half_animal(id),
    tray_id int REFERENCES tray(id)
);

CREATE TABLE product_part(
    package_id int REFERENCES package(id),
    half_animal_id int REFERENCES half_animal(id),
    animal_part_id int REFERENCES animal_part(id)
);


