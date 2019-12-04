delete from valueset r where r.resource ->> 'url' = 'ValueSet/Upper_respiratory_airway';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Upper_respiratory_airway';

select resource_create('{"resourceType": "ValueSet", "id": "Upper_respiratory_airway", "url": "ValueSet/Upper_respiratory_airway", "name": "Upper_respiratory_airway", "title": "Результат осмотра верхних дыхательных путей", "description": "Результат осмотра верхних дыхательных путей", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Upper_respiratory_airway:1.Airways_not_passable_(asphyxia)_or_not_breathing", "code": "Airways_not_passable_(asphyxia)_or_not_breathing", "system": "ValueSet/Upper_respiratory_airway", "display": "Дыхательные пути не проходимы (асфиксия) или не дышит"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Upper_respiratory_airway:2.Airways_passable", "code": "Airways_passable", "system": "ValueSet/Upper_respiratory_airway", "display": "Дыхательные пути проходимы"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Consciousness_assessment';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Consciousness_assessment';

select resource_create('{"resourceType": "ValueSet", "id": "Consciousness_assessment", "url": "ValueSet/Consciousness_assessment", "name": "Consciousness_assessment", "title": "Оценка уровня сознания", "description": "Оценка уровня сознания", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Consciousness_assessment:1.Coma,_ongoing_generalized_cramps", "code": "Coma,_ongoing_generalized_cramps", "system": "ValueSet/Consciousness_assessment", "display": "Кома, продолжающиеся генерализованные судороги"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Consciousness_assessment:2.Stun", "code": "Stun", "system": "ValueSet/Consciousness_assessment", "display": "Оглушение, сопор"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Consciousness_assessment:3.Clear_mind", "code": "Clear_mind", "system": "ValueSet/Consciousness_assessment", "display": "Ясное сознание"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Patient_can_stand';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Patient_can_stand';

select resource_create('{"resourceType": "ValueSet", "id": "Patient_can_stand", "url": "ValueSet/Patient_can_stand", "name": "Patient_can_stand", "title": "Пациент может стоять", "description": "Пациент может стоять", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Patient_can_stand:1.Not_count", "code": "Not_count", "system": "ValueSet/Patient_can_stand", "display": "Не учитывается"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Patient_can_stand:2.Cant_stand", "code": "Cant_stand", "system": "ValueSet/Patient_can_stand", "display": "Не может стоять"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Patient_can_stand:3.Can_stand", "code": "Can_stand", "system": "ValueSet/Patient_can_stand", "display": "Может стоять"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Severity';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Severity';

select resource_create('{"resourceType": "ValueSet", "id": "Severity", "url": "ValueSet/Severity", "name": "Severity", "title": "Результат сортировки", "description": "Результат сортировки", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Severity:1.RED", "code": "RED", "system": "ValueSet/Severity", "display": "Красный"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Severity:2.YELLOW", "code": "YELLOW", "system": "ValueSet/Severity", "display": "Желтый"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Severity:3.GREEN", "code": "GREEN", "system": "ValueSet/Severity", "display": "Зеленый"}'::jsonb);