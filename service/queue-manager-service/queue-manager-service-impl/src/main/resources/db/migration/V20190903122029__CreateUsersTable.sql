CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE users (
    id          BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
    first_name  VARCHAR(20),
    last_name   VARCHAR(20),
    type        VARCHAR(20),
    status      VARCHAR(20),
    updated_at  DATE,
    diagnostic  VARCHAR(20),
    birth_date  DATE
);

COMMENT ON TABLE users IS 'Пациенты';

COMMENT ON COLUMN users.id IS 'Id';
COMMENT ON COLUMN users.first_name IS 'Имя';
COMMENT ON COLUMN users.last_name IS 'Фамилия';
COMMENT ON COLUMN users.type IS 'Тип';
