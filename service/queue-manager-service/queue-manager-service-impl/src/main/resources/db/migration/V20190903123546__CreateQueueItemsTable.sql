CREATE SEQUENCE queue_items_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE queue_items (
    id           BIGINT PRIMARY KEY DEFAULT nextval('queue_items_seq'),
    user_id      BIGINT NOT NULL,
    office_id    BIGINT,
    est_duration INTEGER,
    onum         INTEGER
);

COMMENT ON TABLE queue_items IS 'Элементы в очередях в кабинеты';
COMMENT ON COLUMN queue_items.id IS 'Id';

