DROP TABLE IF EXISTS item CASCADE;
CREATE TABLE item
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY,
    item_name VARCHAR(10),
    price     INTEGER,
    quantity  INTEGER,
    PRIMARY KEY (id)
);