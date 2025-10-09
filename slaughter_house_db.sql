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
                             product_id int REFERENCES  product(id),
                             tray_id int REFERENCES tray(id)
);

CREATE TABLE product_part(
                             product_id int REFERENCES  product(id),
                             animal_part_id int REFERENCES animal_part(id)
);

INSERT INTO animal VALUES (1123, 129.0, 'pig');
INSERT INTO animal VALUES (1244, 1200.0, 'cow');

INSERT INTO animal_part VALUES (1233, 58.9, 'leg', 1123);
INSERT INTO animal_part VALUES (1243, 88.9, 'tail', 1244);
INSERT INTO animal_part VALUES (1223, 8.9, 'ear', 1244);

INSERT INTO tray VALUES (1222, 90.0);
INSERT INTO tray VALUES (1272, 80.0);

INSERT INTO animal_part_tray VALUES (1233, 1222);
INSERT INTO animal_part_tray VALUES (1243, 1222);
INSERT INTO animal_part_tray VALUES (1223, 1272);

INSERT INTO product VALUES (1);
INSERT INTO product VALUES (2);

INSERT INTO package VALUES (1);
INSERT INTO half_animal VALUES (2);


INSERT INTO product_tray VALUES (1,  1222);
INSERT INTO product_part VALUES (1, 1233);
INSERT INTO product_part VALUES (1,  1243);
INSERT INTO product_tray VALUES (2, 1272);
INSERT INTO product_part VALUES (2, 1223);