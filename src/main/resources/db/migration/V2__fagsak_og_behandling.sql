CREATE SEQUENCE fagsak_seq INCREMENT BY 50 START WITH 1000000 NO CYCLE;
CREATE SEQUENCE behandling_seq INCREMENT BY 50 START WITH 1000000 NO CYCLE;

CREATE TABLE fagsak
(
    id                  BIGINT PRIMARY KEY DEFAULT nextval('fagsak_seq'),
    versjon             BIGINT       NOT NULL DEFAULT 0,
    opprettet_av        VARCHAR      NOT NULL,
    opprettet_tidspunkt TIMESTAMP(3) NOT NULL,
    endret_av           VARCHAR,
    endret_tidspunkt    TIMESTAMP(3)
);

CREATE TABLE behandling
(
    id                  BIGINT PRIMARY KEY DEFAULT nextval('behandling_seq'),
    fk_fagsak_id        BIGINT       NOT NULL REFERENCES fagsak (id),
    versjon             BIGINT       NOT NULL DEFAULT 0,
    opprettet_av        VARCHAR      NOT NULL,
    opprettet_tidspunkt TIMESTAMP(3) NOT NULL,
    endret_av           VARCHAR,
    endret_tidspunkt    TIMESTAMP(3)
);

CREATE INDEX behandling_fk_fagsak_id_idx ON behandling (fk_fagsak_id);
