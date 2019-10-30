drop view if exists patients_to_examine;

create or replace view patients_to_examine
as
select jsonb_array_elements(items.item -> 'answer') -> 'valueCoding' as severity,
       SPLIT_PART(patientRef, '/', 2) as patient_id,
       p.resource as patient,
       cp.resource -> 'status' as care_plan_status
from (
         select jsonb_array_elements(r.resource -> 'item') as item,
                r.resource -> 'source' ->> 'reference' as patientRef
         from questionnaireResponse r
         where r.resource ->> 'questionnaire' = 'Questionnaire/Severity_criteria'
           and 'QuestionnaireResponse/' || r.id in (
             select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
             from clinicalImpression ci
             where ci.resource ->> 'status' = 'active'
               and ci.resource -> 'subject' ->> 'reference' = r.resource -> 'source' ->> 'reference'
         )
     ) as items
         left join patient p on p.id = SPLIT_PART(patientRef, '/', 2)
         left join careplan cp on cp.resource -> 'subject' ->> 'reference' = patientRef
where items.item ->> 'linkId' = 'Severity'
  and (cp.resource ->> 'status' = 'active' or
       cp.resource ->> 'status' = 'results_are_ready' or
       cp.resource ->> 'status' = 'waiting_results')
;