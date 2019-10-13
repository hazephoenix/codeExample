CREATE SEQUENCE route_sheet_items_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE route_sheet_items (
    id               BIGINT PRIMARY KEY DEFAULT nextval('route_sheet_items_seq'),
    user_id          BIGINT NOT NULL,
    survey_type_id   BIGINT,
    priority         NUMERIC(5,2),
    visited          BOOL,
    onum             INTEGER
);

COMMENT ON TABLE route_sheet_items IS 'Элементы в маршрутных листах';
COMMENT ON COLUMN route_sheet_items.id IS 'Id';