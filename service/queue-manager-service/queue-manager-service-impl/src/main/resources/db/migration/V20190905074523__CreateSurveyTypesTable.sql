CREATE SEQUENCE survey_types_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE survey_types (
    id          BIGINT PRIMARY KEY DEFAULT nextval('survey_types_seq'),
    name        VARCHAR(50)
);

COMMENT ON TABLE survey_types IS 'Кабинеты';

COMMENT ON COLUMN survey_types.id IS 'Id';
COMMENT ON COLUMN survey_types.name IS 'Название';
-- todo
