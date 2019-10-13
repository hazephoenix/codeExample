CREATE SEQUENCE offices_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE offices (
    id             BIGINT PRIMARY KEY DEFAULT nextval('offices_seq'),
    name           VARCHAR(50),
    -- TODO точно один тип на один кабинет?
    survey_type_id BIGINT,
    status         VARCHAR(20),
    updated_at     DATE
);

COMMENT ON TABLE offices IS 'Кабинеты';

COMMENT ON COLUMN offices.id IS 'Id';
COMMENT ON COLUMN offices.name IS 'Название';
