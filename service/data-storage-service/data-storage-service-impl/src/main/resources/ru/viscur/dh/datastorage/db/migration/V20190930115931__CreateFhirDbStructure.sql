create sequence if not exists transaction_id_seq
	as integer;

alter sequence transaction_id_seq owner to ${owner};

drop table if exists "account";
drop table if exists "account_history";
drop table if exists "activitydefinition";
drop table if exists "activitydefinition_history";
drop table if exists "adverseevent";
drop table if exists "adverseevent_history";
drop table if exists "allergyintolerance";
drop table if exists "allergyintolerance_history";
drop table if exists "appointment";
drop table if exists "appointment_history";
drop table if exists "appointmentresponse";
drop table if exists "appointmentresponse_history";
drop table if exists "auditevent";
drop table if exists "auditevent_history";
drop table if exists "basic";
drop table if exists "basic_history";
drop table if exists "binary";
drop table if exists "binary_history";
drop table if exists "biologicallyderivedproduct";
drop table if exists "biologicallyderivedproduct_history";
drop table if exists "bodystructure";
drop table if exists "bodystructure_history";
drop table if exists "capabilitystatement";
drop table if exists "capabilitystatement_history";
drop table if exists "careplan";
drop table if exists "careplan_history";
drop table if exists "careteam";
drop table if exists "careteam_history";
drop table if exists "chargeitem";
drop table if exists "chargeitem_history";
drop table if exists "claim";
drop table if exists "claim_history";
drop table if exists "claimresponse";
drop table if exists "claimresponse_history";
drop table if exists "clinicalimpression";
drop table if exists "clinicalimpression_history";
drop table if exists "codesystem";
drop table if exists "codesystem_history";
drop table if exists "communication";
drop table if exists "communication_history";
drop table if exists "communicationrequest";
drop table if exists "communicationrequest_history";
drop table if exists "compartmentdefinition";
drop table if exists "compartmentdefinition_history";
drop table if exists "composition";
drop table if exists "composition_history";
drop table if exists "concept";
drop table if exists "concept_history";
drop table if exists "conceptmap";
drop table if exists "conceptmap_history";
drop table if exists "condition";
drop table if exists "condition_history";
drop table if exists "consent";
drop table if exists "consent_history";
drop table if exists "contract";
drop table if exists "contract_history";
drop table if exists "coverage";
drop table if exists "coverage_history";
drop table if exists "detectedissue";
drop table if exists "detectedissue_history";
drop table if exists "device";
drop table if exists "device_history";
drop table if exists "devicecomponent";
drop table if exists "devicecomponent_history";
drop table if exists "devicemetric";
drop table if exists "devicemetric_history";
drop table if exists "devicerequest";
drop table if exists "devicerequest_history";
drop table if exists "deviceusestatement";
drop table if exists "deviceusestatement_history";
drop table if exists "diagnosticreport";
drop table if exists "diagnosticreport_history";
drop table if exists "documentmanifest";
drop table if exists "documentmanifest_history";
drop table if exists "documentreference";
drop table if exists "documentreference_history";
drop table if exists "eligibilityrequest";
drop table if exists "eligibilityrequest_history";
drop table if exists "eligibilityresponse";
drop table if exists "eligibilityresponse_history";
drop table if exists "encounter";
drop table if exists "encounter_history";
drop table if exists "endpoint";
drop table if exists "endpoint_history";
drop table if exists "enrollmentrequest";
drop table if exists "enrollmentrequest_history";
drop table if exists "enrollmentresponse";
drop table if exists "enrollmentresponse_history";
drop table if exists "entrydefinition";
drop table if exists "entrydefinition_history";
drop table if exists "episodeofcare";
drop table if exists "episodeofcare_history";
drop table if exists "eventdefinition";
drop table if exists "eventdefinition_history";
drop table if exists "examplescenario";
drop table if exists "examplescenario_history";
drop table if exists "expansionprofile";
drop table if exists "expansionprofile_history";
drop table if exists "explanationofbenefit";
drop table if exists "explanationofbenefit_history";
drop table if exists "familymemberhistory";
drop table if exists "familymemberhistory_history";
drop table if exists "flag";
drop table if exists "flag_history";
drop table if exists "goal";
drop table if exists "goal_history";
drop table if exists "graphdefinition";
drop table if exists "graphdefinition_history";
drop table if exists "group";
drop table if exists "group_history";
drop table if exists "guidanceresponse";
drop table if exists "guidanceresponse_history";
drop table if exists "healthcareservice";
drop table if exists "healthcareservice_history";
drop table if exists "imagingstudy";
drop table if exists "imagingstudy_history";
drop table if exists "immunization";
drop table if exists "immunization_history";
drop table if exists "immunizationevaluation";
drop table if exists "immunizationevaluation_history";
drop table if exists "immunizationrecommendation";
drop table if exists "immunizationrecommendation_history";
drop table if exists "implementationguide";
drop table if exists "implementationguide_history";
drop table if exists "invoice";
drop table if exists "invoice_history";
drop table if exists "iteminstance";
drop table if exists "iteminstance_history";
drop table if exists "library";
drop table if exists "library_history";
drop table if exists "linkage";
drop table if exists "linkage_history";
drop table if exists "list";
drop table if exists "list_history";
drop table if exists "location";
drop table if exists "location_history";
drop table if exists "measure";
drop table if exists "measure_history";
drop table if exists "measurereport";
drop table if exists "measurereport_history";
drop table if exists "media";
drop table if exists "media_history";
drop table if exists "medication";
drop table if exists "medication_history";
drop table if exists "medicationadministration";
drop table if exists "medicationadministration_history";
drop table if exists "medicationdispense";
drop table if exists "medicationdispense_history";
drop table if exists "medicationrequest";
drop table if exists "medicationrequest_history";
drop table if exists "medicationstatement";
drop table if exists "medicationstatement_history";
drop table if exists "medicinalproduct";
drop table if exists "medicinalproduct_history";
drop table if exists "medicinalproductauthorization";
drop table if exists "medicinalproductauthorization_history";
drop table if exists "medicinalproductclinicals";
drop table if exists "medicinalproductclinicals_history";
drop table if exists "medicinalproductdevicespec";
drop table if exists "medicinalproductdevicespec_history";
drop table if exists "medicinalproductingredient";
drop table if exists "medicinalproductingredient_history";
drop table if exists "medicinalproductpackaged";
drop table if exists "medicinalproductpackaged_history";
drop table if exists "medicinalproductpharmaceutical";
drop table if exists "medicinalproductpharmaceutical_history";
drop table if exists "messagedefinition";
drop table if exists "messagedefinition_history";
drop table if exists "messageheader";
drop table if exists "messageheader_history";
drop table if exists "metadataresource";
drop table if exists "metadataresource_history";
drop table if exists "namingsystem";
drop table if exists "namingsystem_history";
drop table if exists "nutritionorder";
drop table if exists "nutritionorder_history";
drop table if exists "observation";
drop table if exists "observation_history";
drop table if exists "observationdefinition";
drop table if exists "observationdefinition_history";
drop table if exists "occupationaldata";
drop table if exists "occupationaldata_history";
drop table if exists "operationdefinition";
drop table if exists "operationdefinition_history";
drop table if exists "operationoutcome";
drop table if exists "operationoutcome_history";
drop table if exists "organization";
drop table if exists "organization_history";
drop table if exists "organizationrole";
drop table if exists "organizationrole_history";
drop table if exists "parameters";
drop table if exists "parameters_history";
drop table if exists "patient";
drop table if exists "patient_history";
drop table if exists "paymentnotice";
drop table if exists "paymentnotice_history";
drop table if exists "paymentreconciliation";
drop table if exists "paymentreconciliation_history";
drop table if exists "person";
drop table if exists "person_history";
drop table if exists "plandefinition";
drop table if exists "plandefinition_history";
drop table if exists "practitioner";
drop table if exists "practitioner_history";
drop table if exists "practitionerrole";
drop table if exists "practitionerrole_history";
drop table if exists "procedure";
drop table if exists "procedure_history";
drop table if exists "processrequest";
drop table if exists "processrequest_history";
drop table if exists "processresponse";
drop table if exists "processresponse_history";
drop table if exists "productplan";
drop table if exists "productplan_history";
drop table if exists "provenance";
drop table if exists "provenance_history";
drop table if exists "questionnaire";
drop table if exists "questionnaire_history";
drop table if exists "questionnaireresponse";
drop table if exists "questionnaireresponse_history";
drop table if exists "relatedperson";
drop table if exists "relatedperson_history";
drop table if exists "requestgroup";
drop table if exists "requestgroup_history";
drop table if exists "researchstudy";
drop table if exists "researchstudy_history";
drop table if exists "researchsubject";
drop table if exists "researchsubject_history";
drop table if exists "riskassessment";
drop table if exists "riskassessment_history";
drop table if exists "schedule";
drop table if exists "schedule_history";
drop table if exists "sequence";
drop table if exists "sequence_history";
drop table if exists "servicerequest";
drop table if exists "servicerequest_history";
drop table if exists "slot";
drop table if exists "slot_history";
drop table if exists "specimen";
drop table if exists "specimen_history";
drop table if exists "specimendefinition";
drop table if exists "specimendefinition_history";
drop table if exists "structuredefinition";
drop table if exists "structuredefinition_history";
drop table if exists "structuremap";
drop table if exists "structuremap_history";
drop table if exists "subscription";
drop table if exists "subscription_history";
drop table if exists "substance";
drop table if exists "substance_history";
drop table if exists "substancepolymer";
drop table if exists "substancepolymer_history";
drop table if exists "substancereferenceinformation";
drop table if exists "substancereferenceinformation_history";
drop table if exists "substancespecification";
drop table if exists "substancespecification_history";
drop table if exists "supplydelivery";
drop table if exists "supplydelivery_history";
drop table if exists "supplyrequest";
drop table if exists "supplyrequest_history";
drop table if exists "task";
drop table if exists "task_history";
drop table if exists "terminologycapabilities";
drop table if exists "terminologycapabilities_history";
drop table if exists "testreport";
drop table if exists "testreport_history";
drop table if exists "testscript";
drop table if exists "testscript_history";
drop table if exists "transaction";
drop table if exists "usersession";
drop table if exists "usersession_history";
drop table if exists "valueset";
drop table if exists "valueset_history";
drop table if exists "verificationresult";
drop table if exists "verificationresult_history";
drop table if exists "visionprescription";
drop table if exists "visionprescription_history";

drop type if exists resource_status cascade;
create type resource_status as enum ('created', 'updated', 'deleted', 'recreated');

alter type resource_status owner to ${owner};

drop type if exists _resource cascade;
create type _resource as
(
	id text,
	txid bigint,
	ts timestamp with time zone,
	resource_type text,
	status resource_status,
	resource jsonb
);

alter type _resource owner to ${owner};

create table transaction
(
	id serial not null
		constraint transaction_pkey
			primary key,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource jsonb
);

alter table transaction owner to ${owner};

create table devicerequest
(
	id text not null
		constraint devicerequest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DeviceRequest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table devicerequest owner to ${owner};

create table devicerequest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DeviceRequest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint devicerequest_history_pkey
		primary key (id, txid)
);

alter table devicerequest_history owner to ${owner};

create table servicerequest
(
	id text not null
		constraint servicerequest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ServiceRequest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table servicerequest owner to ${owner};

create table servicerequest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ServiceRequest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint servicerequest_history_pkey
		primary key (id, txid)
);

alter table servicerequest_history owner to ${owner};

create table devicecomponent
(
	id text not null
		constraint devicecomponent_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DeviceComponent'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table devicecomponent owner to ${owner};

create table devicecomponent_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DeviceComponent'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint devicecomponent_history_pkey
		primary key (id, txid)
);

alter table devicecomponent_history owner to ${owner};

create table devicemetric
(
	id text not null
		constraint devicemetric_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DeviceMetric'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table devicemetric owner to ${owner};

create table devicemetric_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DeviceMetric'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint devicemetric_history_pkey
		primary key (id, txid)
);

alter table devicemetric_history owner to ${owner};

create table usersession
(
	id text not null
		constraint usersession_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'UserSession'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table usersession owner to ${owner};

create table usersession_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'UserSession'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint usersession_history_pkey
		primary key (id, txid)
);

alter table usersession_history owner to ${owner};

create table careplan
(
	id text not null
		constraint careplan_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CarePlan'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table careplan owner to ${owner};

create table careplan_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CarePlan'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint careplan_history_pkey
		primary key (id, txid)
);

alter table careplan_history owner to ${owner};

create table observation
(
	id text not null
		constraint observation_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Observation'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table observation owner to ${owner};

create table observation_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Observation'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint observation_history_pkey
		primary key (id, txid)
);

alter table observation_history owner to ${owner};

create table enrollmentrequest
(
	id text not null
		constraint enrollmentrequest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EnrollmentRequest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table enrollmentrequest owner to ${owner};

create table enrollmentrequest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EnrollmentRequest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint enrollmentrequest_history_pkey
		primary key (id, txid)
);

alter table enrollmentrequest_history owner to ${owner};

create table "group"
(
	id text not null
		constraint group_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Group'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table "group" owner to ${owner};

create table group_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Group'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint group_history_pkey
		primary key (id, txid)
);

alter table group_history owner to ${owner};

create table messagedefinition
(
	id text not null
		constraint messagedefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MessageDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table messagedefinition owner to ${owner};

create table messagedefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MessageDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint messagedefinition_history_pkey
		primary key (id, txid)
);

alter table messagedefinition_history owner to ${owner};

create table appointment
(
	id text not null
		constraint appointment_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Appointment'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table appointment owner to ${owner};

create table appointment_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Appointment'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint appointment_history_pkey
		primary key (id, txid)
);

alter table appointment_history owner to ${owner};

create table biologicallyderivedproduct
(
	id text not null
		constraint biologicallyderivedproduct_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'BiologicallyDerivedProduct'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table biologicallyderivedproduct owner to ${owner};

create table biologicallyderivedproduct_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'BiologicallyDerivedProduct'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint biologicallyderivedproduct_history_pkey
		primary key (id, txid)
);

alter table biologicallyderivedproduct_history owner to ${owner};

create table questionnaireresponse
(
	id text not null
		constraint questionnaireresponse_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'QuestionnaireResponse'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table questionnaireresponse owner to ${owner};

create table questionnaireresponse_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'QuestionnaireResponse'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint questionnaireresponse_history_pkey
		primary key (id, txid)
);

alter table questionnaireresponse_history owner to ${owner};

create table episodeofcare
(
	id text not null
		constraint episodeofcare_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EpisodeOfCare'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table episodeofcare owner to ${owner};

create table episodeofcare_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EpisodeOfCare'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint episodeofcare_history_pkey
		primary key (id, txid)
);

alter table episodeofcare_history owner to ${owner};

create table substancepolymer
(
	id text not null
		constraint substancepolymer_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SubstancePolymer'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table substancepolymer owner to ${owner};

create table substancepolymer_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SubstancePolymer'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint substancepolymer_history_pkey
		primary key (id, txid)
);

alter table substancepolymer_history owner to ${owner};

create table processresponse
(
	id text not null
		constraint processresponse_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ProcessResponse'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table processresponse owner to ${owner};

create table processresponse_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ProcessResponse'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint processresponse_history_pkey
		primary key (id, txid)
);

alter table processresponse_history owner to ${owner};

create table supplydelivery
(
	id text not null
		constraint supplydelivery_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SupplyDelivery'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table supplydelivery owner to ${owner};

create table supplydelivery_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SupplyDelivery'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint supplydelivery_history_pkey
		primary key (id, txid)
);

alter table supplydelivery_history owner to ${owner};

create table adverseevent
(
	id text not null
		constraint adverseevent_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'AdverseEvent'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table adverseevent owner to ${owner};

create table adverseevent_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'AdverseEvent'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint adverseevent_history_pkey
		primary key (id, txid)
);

alter table adverseevent_history owner to ${owner};

create table iteminstance
(
	id text not null
		constraint iteminstance_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ItemInstance'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table iteminstance owner to ${owner};

create table iteminstance_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ItemInstance'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint iteminstance_history_pkey
		primary key (id, txid)
);

alter table iteminstance_history owner to ${owner};

create table endpoint
(
	id text not null
		constraint endpoint_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Endpoint'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table endpoint owner to ${owner};

create table endpoint_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Endpoint'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint endpoint_history_pkey
		primary key (id, txid)
);

alter table endpoint_history owner to ${owner};

create table substancereferenceinformation
(
	id text not null
		constraint substancereferenceinformation_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SubstanceReferenceInformation'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table substancereferenceinformation owner to ${owner};

create table substancereferenceinformation_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SubstanceReferenceInformation'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint substancereferenceinformation_history_pkey
		primary key (id, txid)
);

alter table substancereferenceinformation_history owner to ${owner};

create table compartmentdefinition
(
	id text not null
		constraint compartmentdefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CompartmentDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table compartmentdefinition owner to ${owner};

create table compartmentdefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CompartmentDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint compartmentdefinition_history_pkey
		primary key (id, txid)
);

alter table compartmentdefinition_history owner to ${owner};

create table detectedissue
(
	id text not null
		constraint detectedissue_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DetectedIssue'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table detectedissue owner to ${owner};

create table detectedissue_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DetectedIssue'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint detectedissue_history_pkey
		primary key (id, txid)
);

alter table detectedissue_history owner to ${owner};

create table medicationadministration
(
	id text not null
		constraint medicationadministration_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicationAdministration'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicationadministration owner to ${owner};

create table medicationadministration_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicationAdministration'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicationadministration_history_pkey
		primary key (id, txid)
);

alter table medicationadministration_history owner to ${owner};

create table implementationguide
(
	id text not null
		constraint implementationguide_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ImplementationGuide'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table implementationguide owner to ${owner};

create table implementationguide_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ImplementationGuide'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint implementationguide_history_pkey
		primary key (id, txid)
);

alter table implementationguide_history owner to ${owner};

create table goal
(
	id text not null
		constraint goal_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Goal'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table goal owner to ${owner};

create table goal_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Goal'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint goal_history_pkey
		primary key (id, txid)
);

alter table goal_history owner to ${owner};

create table communication
(
	id text not null
		constraint communication_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Communication'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table communication owner to ${owner};

create table communication_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Communication'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint communication_history_pkey
		primary key (id, txid)
);

alter table communication_history owner to ${owner};

create table schedule
(
	id text not null
		constraint schedule_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Schedule'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table schedule owner to ${owner};

create table schedule_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Schedule'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint schedule_history_pkey
		primary key (id, txid)
);

alter table schedule_history owner to ${owner};

create table documentreference
(
	id text not null
		constraint documentreference_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DocumentReference'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table documentreference owner to ${owner};

create table documentreference_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DocumentReference'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint documentreference_history_pkey
		primary key (id, txid)
);

alter table documentreference_history owner to ${owner};

create table coverage
(
	id text not null
		constraint coverage_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Coverage'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table coverage owner to ${owner};

create table coverage_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Coverage'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint coverage_history_pkey
		primary key (id, txid)
);

alter table coverage_history owner to ${owner};

create table auditevent
(
	id text not null
		constraint auditevent_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'AuditEvent'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table auditevent owner to ${owner};

create table auditevent_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'AuditEvent'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint auditevent_history_pkey
		primary key (id, txid)
);

alter table auditevent_history owner to ${owner};

create table messageheader
(
	id text not null
		constraint messageheader_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MessageHeader'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table messageheader owner to ${owner};

create table messageheader_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MessageHeader'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint messageheader_history_pkey
		primary key (id, txid)
);

alter table messageheader_history owner to ${owner};

create table contract
(
	id text not null
		constraint contract_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Contract'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table contract owner to ${owner};

create table contract_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Contract'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint contract_history_pkey
		primary key (id, txid)
);

alter table contract_history owner to ${owner};

create table sequence
(
	id text not null
		constraint sequence_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Sequence'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table sequence owner to ${owner};

create table sequence_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Sequence'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint sequence_history_pkey
		primary key (id, txid)
);

alter table sequence_history owner to ${owner};

create table testreport
(
	id text not null
		constraint testreport_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'TestReport'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table testreport owner to ${owner};

create table testreport_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'TestReport'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint testreport_history_pkey
		primary key (id, txid)
);

alter table testreport_history owner to ${owner};

create table codesystem
(
	id text not null
		constraint codesystem_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CodeSystem'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table codesystem owner to ${owner};

create table codesystem_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CodeSystem'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint codesystem_history_pkey
		primary key (id, txid)
);

alter table codesystem_history owner to ${owner};

create table plandefinition
(
	id text not null
		constraint plandefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'PlanDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table plandefinition owner to ${owner};

create table plandefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'PlanDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint plandefinition_history_pkey
		primary key (id, txid)
);

alter table plandefinition_history owner to ${owner};

create table invoice
(
	id text not null
		constraint invoice_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Invoice'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table invoice owner to ${owner};

create table invoice_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Invoice'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint invoice_history_pkey
		primary key (id, txid)
);

alter table invoice_history owner to ${owner};

create table claimresponse
(
	id text not null
		constraint claimresponse_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ClaimResponse'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table claimresponse owner to ${owner};

create table claimresponse_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ClaimResponse'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint claimresponse_history_pkey
		primary key (id, txid)
);

alter table claimresponse_history owner to ${owner};

create table chargeitem
(
	id text not null
		constraint chargeitem_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ChargeItem'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table chargeitem owner to ${owner};

create table chargeitem_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ChargeItem'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint chargeitem_history_pkey
		primary key (id, txid)
);

alter table chargeitem_history owner to ${owner};

create table bodystructure
(
	id text not null
		constraint bodystructure_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'BodyStructure'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table bodystructure owner to ${owner};

create table bodystructure_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'BodyStructure'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint bodystructure_history_pkey
		primary key (id, txid)
);

alter table bodystructure_history owner to ${owner};

create table parameters
(
	id text not null
		constraint parameters_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Parameters'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table parameters owner to ${owner};

create table parameters_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Parameters'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint parameters_history_pkey
		primary key (id, txid)
);

alter table parameters_history owner to ${owner};

create table clinicalimpression
(
	id text not null
		constraint clinicalimpression_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ClinicalImpression'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table clinicalimpression owner to ${owner};

create table clinicalimpression_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ClinicalImpression'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint clinicalimpression_history_pkey
		primary key (id, txid)
);

alter table clinicalimpression_history owner to ${owner};

create table familymemberhistory
(
	id text not null
		constraint familymemberhistory_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'FamilyMemberHistory'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table familymemberhistory owner to ${owner};

create table familymemberhistory_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'FamilyMemberHistory'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint familymemberhistory_history_pkey
		primary key (id, txid)
);

alter table familymemberhistory_history owner to ${owner};

create table medicinalproductauthorization
(
	id text not null
		constraint medicinalproductauthorization_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductAuthorization'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicinalproductauthorization owner to ${owner};

create table medicinalproductauthorization_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductAuthorization'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicinalproductauthorization_history_pkey
		primary key (id, txid)
);

alter table medicinalproductauthorization_history owner to ${owner};

create table "binary"
(
	id text not null
		constraint binary_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Binary'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table "binary" owner to ${owner};

create table binary_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Binary'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint binary_history_pkey
		primary key (id, txid)
);

alter table binary_history owner to ${owner};

create table composition
(
	id text not null
		constraint composition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Composition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table composition owner to ${owner};

create table composition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Composition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint composition_history_pkey
		primary key (id, txid)
);

alter table composition_history owner to ${owner};

create table practitionerrole
(
	id text not null
		constraint practitionerrole_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'PractitionerRole'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table practitionerrole owner to ${owner};

create table practitionerrole_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'PractitionerRole'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint practitionerrole_history_pkey
		primary key (id, txid)
);

alter table practitionerrole_history owner to ${owner};

create table healthcareservice
(
	id text not null
		constraint healthcareservice_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'HealthcareService'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table healthcareservice owner to ${owner};

create table healthcareservice_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'HealthcareService'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint healthcareservice_history_pkey
		primary key (id, txid)
);

alter table healthcareservice_history owner to ${owner};

create table patient
(
	id text not null
		constraint patient_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Patient'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table patient owner to ${owner};

create table patient_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Patient'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint patient_history_pkey
		primary key (id, txid)
);

alter table patient_history owner to ${owner};

create table entrydefinition
(
	id text not null
		constraint entrydefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EntryDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table entrydefinition owner to ${owner};

create table entrydefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EntryDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint entrydefinition_history_pkey
		primary key (id, txid)
);

alter table entrydefinition_history owner to ${owner};

create table medicationdispense
(
	id text not null
		constraint medicationdispense_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicationDispense'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicationdispense owner to ${owner};

create table medicationdispense_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicationDispense'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicationdispense_history_pkey
		primary key (id, txid)
);

alter table medicationdispense_history owner to ${owner};

create table deviceusestatement
(
	id text not null
		constraint deviceusestatement_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DeviceUseStatement'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table deviceusestatement owner to ${owner};

create table deviceusestatement_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DeviceUseStatement'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint deviceusestatement_history_pkey
		primary key (id, txid)
);

alter table deviceusestatement_history owner to ${owner};

create table structuremap
(
	id text not null
		constraint structuremap_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'StructureMap'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table structuremap owner to ${owner};

create table structuremap_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'StructureMap'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint structuremap_history_pkey
		primary key (id, txid)
);

alter table structuremap_history owner to ${owner};

create table immunizationevaluation
(
	id text not null
		constraint immunizationevaluation_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ImmunizationEvaluation'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table immunizationevaluation owner to ${owner};

create table immunizationevaluation_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ImmunizationEvaluation'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint immunizationevaluation_history_pkey
		primary key (id, txid)
);

alter table immunizationevaluation_history owner to ${owner};

create table library
(
	id text not null
		constraint library_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Library'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table library owner to ${owner};

create table library_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Library'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint library_history_pkey
		primary key (id, txid)
);

alter table library_history owner to ${owner};

create table basic
(
	id text not null
		constraint basic_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Basic'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table basic owner to ${owner};

create table basic_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Basic'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint basic_history_pkey
		primary key (id, txid)
);

alter table basic_history owner to ${owner};

create table slot
(
	id text not null
		constraint slot_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Slot'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table slot owner to ${owner};

create table slot_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Slot'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint slot_history_pkey
		primary key (id, txid)
);

alter table slot_history owner to ${owner};

create table activitydefinition
(
	id text not null
		constraint activitydefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ActivityDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table activitydefinition owner to ${owner};

create table activitydefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ActivityDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint activitydefinition_history_pkey
		primary key (id, txid)
);

alter table activitydefinition_history owner to ${owner};

create table specimen
(
	id text not null
		constraint specimen_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Specimen'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table specimen owner to ${owner};

create table specimen_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Specimen'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint specimen_history_pkey
		primary key (id, txid)
);

alter table specimen_history owner to ${owner};

create table diagnosticreport
(
	id text not null
		constraint diagnosticreport_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DiagnosticReport'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table diagnosticreport owner to ${owner};

create table diagnosticreport_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DiagnosticReport'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint diagnosticreport_history_pkey
		primary key (id, txid)
);

alter table diagnosticreport_history owner to ${owner};

create table subscription
(
	id text not null
		constraint subscription_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Subscription'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table subscription owner to ${owner};

create table subscription_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Subscription'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint subscription_history_pkey
		primary key (id, txid)
);

alter table subscription_history owner to ${owner};

create table requestgroup
(
	id text not null
		constraint requestgroup_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'RequestGroup'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table requestgroup owner to ${owner};

create table requestgroup_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'RequestGroup'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint requestgroup_history_pkey
		primary key (id, txid)
);

alter table requestgroup_history owner to ${owner};

create table provenance
(
	id text not null
		constraint provenance_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Provenance'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table provenance owner to ${owner};

create table provenance_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Provenance'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint provenance_history_pkey
		primary key (id, txid)
);

alter table provenance_history owner to ${owner};

create table medicinalproduct
(
	id text not null
		constraint medicinalproduct_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProduct'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicinalproduct owner to ${owner};

create table medicinalproduct_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProduct'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicinalproduct_history_pkey
		primary key (id, txid)
);

alter table medicinalproduct_history owner to ${owner};

create table organizationrole
(
	id text not null
		constraint organizationrole_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'OrganizationRole'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table organizationrole owner to ${owner};

create table organizationrole_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'OrganizationRole'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint organizationrole_history_pkey
		primary key (id, txid)
);

alter table organizationrole_history owner to ${owner};

create table practitioner
(
	id text not null
		constraint practitioner_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Practitioner'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table practitioner owner to ${owner};

create table practitioner_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Practitioner'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint practitioner_history_pkey
		primary key (id, txid)
);

alter table practitioner_history owner to ${owner};

create table medicinalproductpackaged
(
	id text not null
		constraint medicinalproductpackaged_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductPackaged'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicinalproductpackaged owner to ${owner};

create table medicinalproductpackaged_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductPackaged'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicinalproductpackaged_history_pkey
		primary key (id, txid)
);

alter table medicinalproductpackaged_history owner to ${owner};

create table flag
(
	id text not null
		constraint flag_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Flag'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table flag owner to ${owner};

create table flag_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Flag'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint flag_history_pkey
		primary key (id, txid)
);

alter table flag_history owner to ${owner};

create table explanationofbenefit
(
	id text not null
		constraint explanationofbenefit_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ExplanationOfBenefit'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table explanationofbenefit owner to ${owner};

create table explanationofbenefit_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ExplanationOfBenefit'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint explanationofbenefit_history_pkey
		primary key (id, txid)
);

alter table explanationofbenefit_history owner to ${owner};

create table linkage
(
	id text not null
		constraint linkage_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Linkage'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table linkage owner to ${owner};

create table linkage_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Linkage'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint linkage_history_pkey
		primary key (id, txid)
);

alter table linkage_history owner to ${owner};

create table operationoutcome
(
	id text not null
		constraint operationoutcome_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'OperationOutcome'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table operationoutcome owner to ${owner};

create table operationoutcome_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'OperationOutcome'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint operationoutcome_history_pkey
		primary key (id, txid)
);

alter table operationoutcome_history owner to ${owner};

create table medicinalproductpharmaceutical
(
	id text not null
		constraint medicinalproductpharmaceutical_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductPharmaceutical'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicinalproductpharmaceutical owner to ${owner};

create table medicinalproductpharmaceutical_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductPharmaceutical'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicinalproductpharmaceutical_history_pkey
		primary key (id, txid)
);

alter table medicinalproductpharmaceutical_history owner to ${owner};

create table immunization
(
	id text not null
		constraint immunization_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Immunization'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table immunization owner to ${owner};

create table immunization_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Immunization'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint immunization_history_pkey
		primary key (id, txid)
);

alter table immunization_history owner to ${owner};

create table researchsubject
(
	id text not null
		constraint researchsubject_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ResearchSubject'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table researchsubject owner to ${owner};

create table researchsubject_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ResearchSubject'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint researchsubject_history_pkey
		primary key (id, txid)
);

alter table researchsubject_history owner to ${owner};

create table expansionprofile
(
	id text not null
		constraint expansionprofile_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ExpansionProfile'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table expansionprofile owner to ${owner};

create table expansionprofile_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ExpansionProfile'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint expansionprofile_history_pkey
		primary key (id, txid)
);

alter table expansionprofile_history owner to ${owner};

create table eligibilityrequest
(
	id text not null
		constraint eligibilityrequest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EligibilityRequest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table eligibilityrequest owner to ${owner};

create table eligibilityrequest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EligibilityRequest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint eligibilityrequest_history_pkey
		primary key (id, txid)
);

alter table eligibilityrequest_history owner to ${owner};

create table medicinalproductclinicals
(
	id text not null
		constraint medicinalproductclinicals_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductClinicals'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicinalproductclinicals owner to ${owner};

create table medicinalproductclinicals_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductClinicals'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicinalproductclinicals_history_pkey
		primary key (id, txid)
);

alter table medicinalproductclinicals_history owner to ${owner};

create table occupationaldata
(
	id text not null
		constraint occupationaldata_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'OccupationalData'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table occupationaldata owner to ${owner};

create table occupationaldata_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'OccupationalData'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint occupationaldata_history_pkey
		primary key (id, txid)
);

alter table occupationaldata_history owner to ${owner};

create table paymentnotice
(
	id text not null
		constraint paymentnotice_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'PaymentNotice'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table paymentnotice owner to ${owner};

create table paymentnotice_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'PaymentNotice'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint paymentnotice_history_pkey
		primary key (id, txid)
);

alter table paymentnotice_history owner to ${owner};

create table namingsystem
(
	id text not null
		constraint namingsystem_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'NamingSystem'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table namingsystem owner to ${owner};

create table namingsystem_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'NamingSystem'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint namingsystem_history_pkey
		primary key (id, txid)
);

alter table namingsystem_history owner to ${owner};

create table medicationstatement
(
	id text not null
		constraint medicationstatement_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicationStatement'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicationstatement owner to ${owner};

create table medicationstatement_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicationStatement'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicationstatement_history_pkey
		primary key (id, txid)
);

alter table medicationstatement_history owner to ${owner};

create table enrollmentresponse
(
	id text not null
		constraint enrollmentresponse_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EnrollmentResponse'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table enrollmentresponse owner to ${owner};

create table enrollmentresponse_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EnrollmentResponse'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint enrollmentresponse_history_pkey
		primary key (id, txid)
);

alter table enrollmentresponse_history owner to ${owner};

create table nutritionorder
(
	id text not null
		constraint nutritionorder_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'NutritionOrder'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table nutritionorder owner to ${owner};

create table nutritionorder_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'NutritionOrder'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint nutritionorder_history_pkey
		primary key (id, txid)
);

alter table nutritionorder_history owner to ${owner};

create table questionnaire
(
	id text not null
		constraint questionnaire_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Questionnaire'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table questionnaire owner to ${owner};

create table questionnaire_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Questionnaire'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint questionnaire_history_pkey
		primary key (id, txid)
);

alter table questionnaire_history owner to ${owner};

create table account
(
	id text not null
		constraint account_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Account'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table account owner to ${owner};

create table account_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Account'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint account_history_pkey
		primary key (id, txid)
);

alter table account_history owner to ${owner};

create table eventdefinition
(
	id text not null
		constraint eventdefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EventDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table eventdefinition owner to ${owner};

create table eventdefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EventDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint eventdefinition_history_pkey
		primary key (id, txid)
);

alter table eventdefinition_history owner to ${owner};

create table medicinalproductdevicespec
(
	id text not null
		constraint medicinalproductdevicespec_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductDeviceSpec'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicinalproductdevicespec owner to ${owner};

create table medicinalproductdevicespec_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductDeviceSpec'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicinalproductdevicespec_history_pkey
		primary key (id, txid)
);

alter table medicinalproductdevicespec_history owner to ${owner};

create table substancespecification
(
	id text not null
		constraint substancespecification_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SubstanceSpecification'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table substancespecification owner to ${owner};

create table substancespecification_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SubstanceSpecification'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint substancespecification_history_pkey
		primary key (id, txid)
);

alter table substancespecification_history owner to ${owner};

create table communicationrequest
(
	id text not null
		constraint communicationrequest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CommunicationRequest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table communicationrequest owner to ${owner};

create table communicationrequest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CommunicationRequest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint communicationrequest_history_pkey
		primary key (id, txid)
);

alter table communicationrequest_history owner to ${owner};

create table specimendefinition
(
	id text not null
		constraint specimendefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SpecimenDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table specimendefinition owner to ${owner};

create table specimendefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SpecimenDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint specimendefinition_history_pkey
		primary key (id, txid)
);

alter table specimendefinition_history owner to ${owner};

create table verificationresult
(
	id text not null
		constraint verificationresult_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'VerificationResult'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table verificationresult owner to ${owner};

create table verificationresult_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'VerificationResult'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint verificationresult_history_pkey
		primary key (id, txid)
);

alter table verificationresult_history owner to ${owner};

create table documentmanifest
(
	id text not null
		constraint documentmanifest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DocumentManifest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table documentmanifest owner to ${owner};

create table documentmanifest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'DocumentManifest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint documentmanifest_history_pkey
		primary key (id, txid)
);

alter table documentmanifest_history owner to ${owner};

create table eligibilityresponse
(
	id text not null
		constraint eligibilityresponse_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EligibilityResponse'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table eligibilityresponse owner to ${owner};

create table eligibilityresponse_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'EligibilityResponse'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint eligibilityresponse_history_pkey
		primary key (id, txid)
);

alter table eligibilityresponse_history owner to ${owner};

create table task
(
	id text not null
		constraint task_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Task'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table task owner to ${owner};

create table task_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Task'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint task_history_pkey
		primary key (id, txid)
);

alter table task_history owner to ${owner};

create table valueset
(
	id text not null
		constraint valueset_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ValueSet'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table valueset owner to ${owner};

create table valueset_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ValueSet'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint valueset_history_pkey
		primary key (id, txid)
);

alter table valueset_history owner to ${owner};

create table claim
(
	id text not null
		constraint claim_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Claim'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table claim owner to ${owner};

create table claim_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Claim'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint claim_history_pkey
		primary key (id, txid)
);

alter table claim_history owner to ${owner};

create table examplescenario
(
	id text not null
		constraint examplescenario_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ExampleScenario'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table examplescenario owner to ${owner};

create table examplescenario_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ExampleScenario'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint examplescenario_history_pkey
		primary key (id, txid)
);

alter table examplescenario_history owner to ${owner};

create table researchstudy
(
	id text not null
		constraint researchstudy_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ResearchStudy'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table researchstudy owner to ${owner};

create table researchstudy_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ResearchStudy'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint researchstudy_history_pkey
		primary key (id, txid)
);

alter table researchstudy_history owner to ${owner};

create table medicationrequest
(
	id text not null
		constraint medicationrequest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicationRequest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicationrequest owner to ${owner};

create table medicationrequest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicationRequest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicationrequest_history_pkey
		primary key (id, txid)
);

alter table medicationrequest_history owner to ${owner};

create table measure
(
	id text not null
		constraint measure_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Measure'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table measure owner to ${owner};

create table measure_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Measure'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint measure_history_pkey
		primary key (id, txid)
);

alter table measure_history owner to ${owner};

create table list
(
	id text not null
		constraint list_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'List'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table list owner to ${owner};

create table list_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'List'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint list_history_pkey
		primary key (id, txid)
);

alter table list_history owner to ${owner};

create table encounter
(
	id text not null
		constraint encounter_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Encounter'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table encounter owner to ${owner};

create table encounter_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Encounter'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint encounter_history_pkey
		primary key (id, txid)
);

alter table encounter_history owner to ${owner};

create table capabilitystatement
(
	id text not null
		constraint capabilitystatement_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CapabilityStatement'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table capabilitystatement owner to ${owner};

create table capabilitystatement_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CapabilityStatement'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint capabilitystatement_history_pkey
		primary key (id, txid)
);

alter table capabilitystatement_history owner to ${owner};

create table visionprescription
(
	id text not null
		constraint visionprescription_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'VisionPrescription'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table visionprescription owner to ${owner};

create table visionprescription_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'VisionPrescription'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint visionprescription_history_pkey
		primary key (id, txid)
);

alter table visionprescription_history owner to ${owner};

create table riskassessment
(
	id text not null
		constraint riskassessment_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'RiskAssessment'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table riskassessment owner to ${owner};

create table riskassessment_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'RiskAssessment'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint riskassessment_history_pkey
		primary key (id, txid)
);

alter table riskassessment_history owner to ${owner};

create table immunizationrecommendation
(
	id text not null
		constraint immunizationrecommendation_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ImmunizationRecommendation'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table immunizationrecommendation owner to ${owner};

create table immunizationrecommendation_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ImmunizationRecommendation'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint immunizationrecommendation_history_pkey
		primary key (id, txid)
);

alter table immunizationrecommendation_history owner to ${owner};

create table processrequest
(
	id text not null
		constraint processrequest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ProcessRequest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table processrequest owner to ${owner};

create table processrequest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ProcessRequest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint processrequest_history_pkey
		primary key (id, txid)
);

alter table processrequest_history owner to ${owner};

create table relatedperson
(
	id text not null
		constraint relatedperson_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'RelatedPerson'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table relatedperson owner to ${owner};

create table relatedperson_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'RelatedPerson'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint relatedperson_history_pkey
		primary key (id, txid)
);

alter table relatedperson_history owner to ${owner};

create table medication
(
	id text not null
		constraint medication_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Medication'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medication owner to ${owner};

create table medication_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Medication'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medication_history_pkey
		primary key (id, txid)
);

alter table medication_history owner to ${owner};

create table appointmentresponse
(
	id text not null
		constraint appointmentresponse_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'AppointmentResponse'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table appointmentresponse owner to ${owner};

create table appointmentresponse_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'AppointmentResponse'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint appointmentresponse_history_pkey
		primary key (id, txid)
);

alter table appointmentresponse_history owner to ${owner};

create table substance
(
	id text not null
		constraint substance_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Substance'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table substance owner to ${owner};

create table substance_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Substance'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint substance_history_pkey
		primary key (id, txid)
);

alter table substance_history owner to ${owner};

create table paymentreconciliation
(
	id text not null
		constraint paymentreconciliation_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'PaymentReconciliation'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table paymentreconciliation owner to ${owner};

create table paymentreconciliation_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'PaymentReconciliation'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint paymentreconciliation_history_pkey
		primary key (id, txid)
);

alter table paymentreconciliation_history owner to ${owner};

create table testscript
(
	id text not null
		constraint testscript_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'TestScript'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table testscript owner to ${owner};

create table testscript_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'TestScript'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint testscript_history_pkey
		primary key (id, txid)
);

alter table testscript_history owner to ${owner};

create table conceptmap
(
	id text not null
		constraint conceptmap_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ConceptMap'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table conceptmap owner to ${owner};

create table conceptmap_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ConceptMap'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint conceptmap_history_pkey
		primary key (id, txid)
);

alter table conceptmap_history owner to ${owner};

create table person
(
	id text not null
		constraint person_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Person'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table person owner to ${owner};

create table person_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Person'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint person_history_pkey
		primary key (id, txid)
);

alter table person_history owner to ${owner};

create table condition
(
	id text not null
		constraint condition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Condition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table condition owner to ${owner};

create table condition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Condition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint condition_history_pkey
		primary key (id, txid)
);

alter table condition_history owner to ${owner};

create table careteam
(
	id text not null
		constraint careteam_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CareTeam'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table careteam owner to ${owner};

create table careteam_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CareTeam'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint careteam_history_pkey
		primary key (id, txid)
);

alter table careteam_history owner to ${owner};

create table structuredefinition
(
	id text not null
		constraint structuredefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'StructureDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table structuredefinition owner to ${owner};

create table structuredefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'StructureDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint structuredefinition_history_pkey
		primary key (id, txid)
);

alter table structuredefinition_history owner to ${owner};

create table procedure
(
	id text not null
		constraint procedure_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Procedure'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table procedure owner to ${owner};

create table procedure_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Procedure'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint procedure_history_pkey
		primary key (id, txid)
);

alter table procedure_history owner to ${owner};

create table consent
(
	id text not null
		constraint consent_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Consent'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table consent owner to ${owner};

create table consent_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Consent'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint consent_history_pkey
		primary key (id, txid)
);

alter table consent_history owner to ${owner};

create table observationdefinition
(
	id text not null
		constraint observationdefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ObservationDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table observationdefinition owner to ${owner};

create table observationdefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ObservationDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint observationdefinition_history_pkey
		primary key (id, txid)
);

alter table observationdefinition_history owner to ${owner};

create table productplan
(
	id text not null
		constraint productplan_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ProductPlan'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table productplan owner to ${owner};

create table productplan_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ProductPlan'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint productplan_history_pkey
		primary key (id, txid)
);

alter table productplan_history owner to ${owner};

create table location
(
	id text not null
		constraint location_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Location'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table location owner to ${owner};

create table location_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Location'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint location_history_pkey
		primary key (id, txid)
);

alter table location_history owner to ${owner};

create table organization
(
	id text not null
		constraint organization_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Organization'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table organization owner to ${owner};

create table organization_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Organization'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint organization_history_pkey
		primary key (id, txid)
);

alter table organization_history owner to ${owner};

create table device
(
	id text not null
		constraint device_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Device'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table device owner to ${owner};

create table device_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Device'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint device_history_pkey
		primary key (id, txid)
);

alter table device_history owner to ${owner};

create table supplyrequest
(
	id text not null
		constraint supplyrequest_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SupplyRequest'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table supplyrequest owner to ${owner};

create table supplyrequest_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'SupplyRequest'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint supplyrequest_history_pkey
		primary key (id, txid)
);

alter table supplyrequest_history owner to ${owner};

create table allergyintolerance
(
	id text not null
		constraint allergyintolerance_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'AllergyIntolerance'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table allergyintolerance owner to ${owner};

create table allergyintolerance_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'AllergyIntolerance'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint allergyintolerance_history_pkey
		primary key (id, txid)
);

alter table allergyintolerance_history owner to ${owner};

create table operationdefinition
(
	id text not null
		constraint operationdefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'OperationDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table operationdefinition owner to ${owner};

create table operationdefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'OperationDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint operationdefinition_history_pkey
		primary key (id, txid)
);

alter table operationdefinition_history owner to ${owner};

create table imagingstudy
(
	id text not null
		constraint imagingstudy_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ImagingStudy'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table imagingstudy owner to ${owner};

create table imagingstudy_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'ImagingStudy'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint imagingstudy_history_pkey
		primary key (id, txid)
);

alter table imagingstudy_history owner to ${owner};

create table medicinalproductingredient
(
	id text not null
		constraint medicinalproductingredient_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductIngredient'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table medicinalproductingredient owner to ${owner};

create table medicinalproductingredient_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MedicinalProductIngredient'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint medicinalproductingredient_history_pkey
		primary key (id, txid)
);

alter table medicinalproductingredient_history owner to ${owner};

create table guidanceresponse
(
	id text not null
		constraint guidanceresponse_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'GuidanceResponse'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table guidanceresponse owner to ${owner};

create table guidanceresponse_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'GuidanceResponse'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint guidanceresponse_history_pkey
		primary key (id, txid)
);

alter table guidanceresponse_history owner to ${owner};

create table media
(
	id text not null
		constraint media_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Media'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table media owner to ${owner};

create table media_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Media'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint media_history_pkey
		primary key (id, txid)
);

alter table media_history owner to ${owner};

create table measurereport
(
	id text not null
		constraint measurereport_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MeasureReport'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table measurereport owner to ${owner};

create table measurereport_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MeasureReport'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint measurereport_history_pkey
		primary key (id, txid)
);

alter table measurereport_history owner to ${owner};

create table graphdefinition
(
	id text not null
		constraint graphdefinition_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'GraphDefinition'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table graphdefinition owner to ${owner};

create table graphdefinition_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'GraphDefinition'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint graphdefinition_history_pkey
		primary key (id, txid)
);

alter table graphdefinition_history owner to ${owner};

create table terminologycapabilities
(
	id text not null
		constraint terminologycapabilities_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'TerminologyCapabilities'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table terminologycapabilities owner to ${owner};

create table terminologycapabilities_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'TerminologyCapabilities'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint terminologycapabilities_history_pkey
		primary key (id, txid)
);

alter table terminologycapabilities_history owner to ${owner};

create table metadataresource
(
	id text not null
		constraint metadataresource_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MetadataResource'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table metadataresource owner to ${owner};

create table metadataresource_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'MetadataResource'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint metadataresource_history_pkey
		primary key (id, txid)
);

alter table metadataresource_history owner to ${owner};

create table concept
(
	id text not null
		constraint concept_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Concept'::text,
	status resource_status not null,
	resource jsonb not null
);

alter table concept owner to ${owner};

create table concept_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'Concept'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint concept_history_pkey
		primary key (id, txid)
);

alter table concept_history owner to ${owner};

create or replace function fhirbase_genid() returns text
	language sql
as $$
select gen_random_uuid()::text
$$;

alter function fhirbase_genid() owner to ${owner};

create or replace function _fhirbase_to_resource(x _resource) returns jsonb
	language sql
as $$
select x.resource || jsonb_build_object(
  'resourceType', x.resource_type,
  'id', x.id,
  'meta', coalesce(x.resource->'meta', '{}'::jsonb) || jsonb_build_object(
    'lastUpdated', x.ts,
    'versionId', x.txid::text
  )
 );
$$;

alter function _fhirbase_to_resource(_resource) owner to ${owner};

create or replace function fhirbase_create(resource jsonb, txid bigint) returns jsonb
	language plpgsql
as $$
DECLARE
  _sql text;
  rt text;
  rid text;
  result jsonb;
BEGIN
    rt   := resource->>'resourceType';
    rid  := coalesce(resource->>'id', fhirbase_genid());
    _sql := format($SQL$
      WITH archived AS (
        INSERT INTO %s (id, txid, ts, status, resource)
        SELECT id, txid, ts, status, resource
        FROM %s
        WHERE id = $2
        RETURNING *
      ), inserted AS (
         INSERT INTO %s (id, ts, txid, status, resource)
         VALUES ($2, current_timestamp, $1, 'created', $3)
         ON CONFLICT (id)
         DO UPDATE SET
          txid = $1,
          ts = current_timestamp,
          status = 'recreated',
          resource = $3
         RETURNING *
      )

      select _fhirbase_to_resource(i.*) from inserted i

      $SQL$,
      rt || '_history', rt, rt, rt);

  EXECUTE _sql
  USING txid, rid, resource || ('{"id": "' || rid || '"}')::jsonb
  INTO result;

  return result;

END
$$;

alter function fhirbase_create(jsonb, bigint) owner to ${owner};

create or replace function fhirbase_create(resource jsonb) returns jsonb
	language sql
as $$
SELECT fhirbase_create(resource, nextval('transaction_id_seq'));
$$;

alter function fhirbase_create(jsonb) owner to ${owner};

create or replace function fhirbase_update(resource jsonb, txid bigint) returns jsonb
	language plpgsql
as $$
DECLARE
  _sql text ;
  rt text;
  rid text;
  result jsonb;
BEGIN
    rt   := resource->>'resourceType';
    rid  := resource->>'ident'->>'id';

    CASE WHEN (rid IS NULL) THEN
      RAISE EXCEPTION 'Resource does not have and id' USING HINT = 'Resource does not have and id';
    ELSE
    END CASE;

    _sql := format($SQL$
      WITH archived AS (
        INSERT INTO %s (id, txid, ts, status, resource)
        SELECT id, txid, ts, status, resource
        FROM %s
        WHERE id = $2
        RETURNING *
      ), inserted AS (
         INSERT INTO %s (id, ts, txid, status, resource)
         VALUES ($2, current_timestamp, $1, 'created', $3)
         ON CONFLICT (id)
         DO UPDATE SET
          txid = $1,
          ts = current_timestamp,
          status = 'updated',
          resource = $3
         RETURNING *
      )

      select _fhirbase_to_resource(i.*) from inserted i

      $SQL$,
      rt || '_history', rt, rt, rt);

  EXECUTE _sql
  USING txid, rid, resource || ('{"id": "' || rid || '"}')::jsonb
  INTO result;

  return result;

END
$$;

alter function fhirbase_update(jsonb, bigint) owner to ${owner};

create or replace function fhirbase_update(resource jsonb) returns jsonb
	language sql
as $$
SELECT fhirbase_update(resource, nextval('transaction_id_seq'));
$$;

alter function fhirbase_update(jsonb) owner to ${owner};

create or replace function fhirbase_read(resource_type text, id text) returns jsonb
	language plpgsql
as $$
DECLARE
  _sql text;
  result jsonb;
BEGIN
  _sql := format($SQL$
    SELECT _fhirbase_to_resource(row(r.*)::_resource) FROM %s r WHERE r.id = $1
  $SQL$,
  resource_type
  );

  EXECUTE _sql USING id INTO result;

  return result;
END
$$;

alter function fhirbase_read(text, text) owner to ${owner};

create or replace function fhirbase_delete(resource_type text, id text, txid bigint) returns jsonb
	language plpgsql
as $$
DECLARE
  _sql text;
  rt text;
  rid text;
  result jsonb;
BEGIN
    rt   := resource_type;
    rid  := id;
    _sql := format($SQL$
      WITH archived AS (
        INSERT INTO %s (id, txid, ts, status, resource)
        SELECT id, txid, ts, status, resource
        FROM %s WHERE id = $2
        RETURNING *
      ), deleted AS (
         INSERT INTO %s (id, txid, ts, status, resource)
         SELECT id, $1, current_timestamp, status, resource
         FROM %s WHERE id = $2
         RETURNING *
      ), dropped AS (
         DELETE FROM %s WHERE id = $2 RETURNING *
      )
      select _fhirbase_to_resource(i.*) from archived i

      $SQL$,
      rt || '_history', rt, rt || '_history', rt, rt);

  EXECUTE _sql
  USING txid, rid
  INTO result;

  return result;

END
$$;

alter function fhirbase_delete(text, text, bigint) owner to ${owner};

create or replace function fhirbase_delete(resource_type text, id text) returns jsonb
	language sql
as $$
SELECT fhirbase_delete(resource_type, id, nextval('transaction_id_seq'));
$$;

alter function fhirbase_delete(text, text) owner to ${owner};

