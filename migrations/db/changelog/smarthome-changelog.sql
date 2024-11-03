--liquibase formatted sql

--changeset ghostofendless:1
CREATE SCHEMA IF NOT EXISTS smarthome;

--changeset ghostofendless:2
CREATE SEQUENCE IF NOT EXISTS smarthome.tokens_seq START WITH 1 INCREMENT BY 50 CACHE 1000;

--changeset ghostofendless:3
CREATE TABLE IF NOT EXISTS smarthome.token
(
    id    BIGINT DEFAULT nextval('smarthome.tokens_seq') PRIMARY KEY,
    token TEXT NOT NULL
);

--changeset ghostofendless:4
CREATE TABLE IF NOT EXISTS smarthome.user
(
    id    BIGINT PRIMARY KEY,
    token_id BIGINT REFERENCES smarthome.token (id)
);

