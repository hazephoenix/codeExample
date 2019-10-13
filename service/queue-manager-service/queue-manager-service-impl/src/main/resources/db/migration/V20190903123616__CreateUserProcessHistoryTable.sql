CREATE SEQUENCE user_process_history_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE user_process_history (
    id              BIGINT PRIMARY KEY DEFAULT nextval('user_process_history_seq'),
    user_id         BIGINT,
    office_id       BIGINT,
    status          VARCHAR(20) NOT NULL,
    fire_date       DATE NOT NULL,
    duration        INTEGER
);

COMMENT ON TABLE user_process_history IS 'История работы с пациентами';
COMMENT ON COLUMN user_process_history.id IS 'Id';
-- todo
