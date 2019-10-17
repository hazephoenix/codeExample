CREATE SEQUENCE queue_patients_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE queue_patients (
    id          BIGINT PRIMARY KEY DEFAULT nextval('queue_patients_seq'),
    first_name  VARCHAR(20),
    last_name   VARCHAR(20),
    type        VARCHAR(20),
    status      VARCHAR(20),
    updated_at  DATE,
    diagnostic  VARCHAR(20),
    birth_date  DATE
);

COMMENT ON TABLE queue_patients IS 'Пациенты';

COMMENT ON COLUMN queue_patients.id IS 'Id';
COMMENT ON COLUMN queue_patients.first_name IS 'Имя';
COMMENT ON COLUMN queue_patients.last_name IS 'Фамилия';
COMMENT ON COLUMN queue_patients.type IS 'Тип';
