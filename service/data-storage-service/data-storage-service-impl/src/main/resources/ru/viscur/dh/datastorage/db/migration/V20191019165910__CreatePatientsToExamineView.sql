drop view if exists patients_to_examine;

create or replace view patients_to_examine
as
select
       SPLIT_PART(respPractRef, '/', 2) as resp_practitioner_id,
       SPLIT_PART(patientRef, '/', 2) as patient_id,
       severity as severity,
       cp.resource ->> 'status' as care_plan_status,
       SPLIT_PART(oq.officeOfQueue, '/', 2) as queue_office_id,
       p.resource as patient
from
         (
             select ci.resource -> 'assessor' ->> 'reference' as respPractRef,
                    ci.resource -> 'subject' ->> 'reference'  as patientRef,
                    ci.resource -> 'extension' ->> 'severity' as severity
             from clinicalImpression ci
             where ci.resource ->> 'status' = 'active'
                 and ci.resource -> 'assessor' is not null
         ) as ci
         join patient p on p.id = SPLIT_PART(patientRef, '/', 2)
         join (select *
               from careplan cp
               where cp.resource ->> 'status' in (
                      'active',
                      'results_are_ready',
                      'waiting_results'
                   )
             ) cp
             on cp.resource -> 'subject' ->> 'reference' = patientRef
         left join (
            select q.resource-> 'location' ->>'reference' as officeOfQueue,
                   q.resource
            from queueitem q
         ) oq
         on oq.resource -> 'subject' ->> 'reference' = patientRef
order by cp.resource ->> 'created'
;