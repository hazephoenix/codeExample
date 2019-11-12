-- Создаем оператор, который делает в точности то же самое,
-- что PostgreSQL-оператор ?

-- Hibernate использует оператор ? для позиционных аргументов
drop operator #-# (jsonb, text);

CREATE OPERATOR #-#(
  PROCEDURE = jsonb_exists_any,
  LEFTARG = jsonb,
  RIGHTARG = text[],
  RESTRICT = contsel,
  JOIN = contjoinsel);