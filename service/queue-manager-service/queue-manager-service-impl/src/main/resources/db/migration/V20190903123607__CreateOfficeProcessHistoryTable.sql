CREATE SEQUENCE office_process_history_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE office_process_history (
    id              BIGINT PRIMARY KEY DEFAULT nextval('office_process_history_seq'),
    survey_type_id  BIGINT,
    status          VARCHAR(20) NOT NULL,
    fire_date       DATE NOT NULL,
    duration        INTEGER,
    user_type       VARCHAR(20),
    user_diagnostic VARCHAR(20),
    user_age_group  INTEGER
);

COMMENT ON TABLE office_process_history IS 'История работы кабинетов';
COMMENT ON COLUMN office_process_history.id IS 'Id';
-- todo
