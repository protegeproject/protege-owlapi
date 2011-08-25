#|
AI: AR will model by Sept. 28 (as discussed on previous call) or we go
with generic treatment approach.
- need to capture data transformation of collection of measurements
from the AT3 assay.
-- They used a statistical test (i.e., generated a P-value) but not
clear which test (don't want to say t-test if don't know for sure).

AR has feedback from BS who suggests:

blinding - model as disclosure process, and 'lacks disclosure to
subjects/researchers' for single/double blinded trials

modelling which person (and inferring group) treated with active vs
control compound - is OK

BS suggests adding a new subtype of subject role 'to be treated with
fucoidan during study fucoidan study' - will be realised in an instance
of a study, is a defined class specialized by context. Needs a better label.

AI: AR will model both blinding and to be treated role  examples for the
fucoidan example in the instance file

Discussion on modelling the dosing regime - 3g per day over 12 days. JF
wants to model this precisely. AR suggests modelling each daily dose as
a separate process and using an aggregate process to combine these sub
part processes. HP raised question what's the output/input as the input
for day2/dose 2 has to be the specified output of day1/dose 1 process,
and these need to be linked in order and referenced to specific
participants - day1->day2 , etc. Will be easier to model as a single
process where the plan specifies the dose - less granlar though

http://clinicaltrials.gov/ct2/info/glossary

BLIND: A randomized trial is "Blind" if the participant is not told
which arm of the trial he is on. A clinical trial is "Blind" if
participants are unaware on whether they are in the experimental or
control arm of the study. also called masked. (See Single Blind Study
and Double Blind Study).

INFORMED CONSENT: The process of learning the key facts about a
clinical trial before deciding whether or not to participate. It is
also a continuing process throughout the study to provide information
for participants. To help someone decide whether or not to
participate, the doctors and nurses involved in the trial explain the
details of the study.
|#

(register-namespace "obifce:" "http://purl.obolibrary.org/obo/obi/example/fucoidan/fc_")

(register-namespace "obisoon:" "http://purl.obolibrary.org/obo/TMPOBI_")

(register-namespace "iaosoon:" "http://purl.obolibrary.org/obo/TMPIAO_")

(defun fcuri (i)
  (make-uri nil (format nil "obifce:~4,'0d" i)))

(defun fcobiuri (i)
  (make-uri nil (format nil "obisoon:~7,'0d" (+ i))))

(defun fciaouri (i)
  (make-uri nil (format nil "iaosoon:~7,'0d" (+ i))))

(defun 4obi () (editor-note "2009/09/28 Alan Ruttenberg. This class is a class that should be added to OBI. It was motivated by the Fucoidan trial use case"))

(defun 4import ()
  (editor-note "2009/09/28 Alan Ruttenberg. This class is a class that should be mireoted in to OBI. It was motivated by the Fucoidan trial use case"))

(defun 4iao ()
  (editor-note "2009/09/28 Alan Ruttenberg. This class is a class that should be added to IAO. It was motivated by the Fucoidan trial use case"))

(defun fcusecase ()
  (editor-note "2009/09/28 Alan Ruttenberg. Fucoidan-use-case"))

(defun source-fucoidan-paper ()
  (definition-source "PMID:19696660"))

(defun metadata-complete ()
  (annotation !'has curation status'@obi !'metadata complete'@obi))

(defun uncurated ()
  (annotation !'has curation status'@obi !'uncurated'@obi))

(defun signedalan () (definition-editor "Person:Alan Ruttenberg"))
(defun signedbjoern () (definition-editor "Person:Bjoern Peters"))
(defun signedhelen () (definition-editor "Person:Helen Parkinson"))
(defun editor-note (note) (annotation !editor-note note))
(defun definition (note) (annotation !definition note))
(defun definition-source (note) (annotation !definition-source note))
(defun definition-editor (note) (annotation !definition-editor note))
(defun example-of-usage (note) (annotation !example-of-usage note))
(defun junk-relation ()
  (editor-note "2009/10/19 Alan Ruttenberg. Named 'junk' relation useful in restrictions, but not a real instance relationship"))

(defun blood-assay-unfinished (class) (class class :partial (editor-note "2009/10/18 Alan Ruttenberg. This assay was added during the fucoidan use case exercise but still needs to be fleshed out. Only the AT-III assay has more carefully specified inputs and outputs")))

(defmacro with-mireot-terms (var-id-parent-source-tuples &body body)
  (setq var-id-parent-source-tuples (eval-uri-reader-macro var-id-parent-source-tuples))
  (if var-id-parent-source-tuples
      (destructuring-bind (var id parent source &optional label) (car var-id-parent-source-tuples)
	  `(let ((,var ,id))
	     (list
	     ,(if (and (consp parent)
		       (eq (car parent) :individual))
		  `(individual ,var (type ,(second parent)) (fcusecase)
			      (annotation !'imported from'@obi  ,source)
			      ;(if ,label (label ,label))
			      )
		  `(class ,var :partial ,parent (fcusecase)
			 (annotation !'imported from'@obi ,source)
			 ;(if ,label (label ,label))
			 ))
	      (with-mireot-terms ,(cdr var-id-parent-source-tuples)
		,@body))))
      `(list ,@body)))

(define-ontology investigation-use-case
    (:base (uri-full !obo:obi/investigation-use-case.owl) :about
	   (uri-full !obo:obi/investigation-use-case.owl))
  (ontology-annotation !owl:versionInfo "$Revision: 2564 $")
  (ontology-annotation !protegeowl:defaultLanguage "en")
  (let ((*default-uri-label-source* :obi)
	(molecular-entity !chebi:23367))
    (with-obo-metadata-uris
      (object-property !'is_specified_output_of'@)   (object-property !'has_specified_output'@)(object-property !'contains'@)
      (object-property !'has_specified_input'@)(object-property !'is_manufactured_by'@)(object-property !'function_of'@)
      (object-property !'is about'@)
      (object-property !'denotes'@)
      (object-property !'realizes'@)  (object-property !'is_realized_by'@) (object-property !'bearer_of'@)
      (object-property !'inheres_in'@) (object-property !'role_of'@) (object-property !'has grain'@)
      (object-property !'has_quality'@) (object-property !oborel:has_part) (object-property !oborel:part_of)
      (object-property !oborel:has_participant) (object-property !'has_role'@)
      (object-property !'has measurement unit label'@) (datatype-property !'has measurement value'@)
      (annotation-property !rdfs:label)  (annotation-property !owl:versionInfo)
      (annotation-property !definition (label "definition"))
      (annotation-property !definition-source (label "definition source"))
      (annotation-property !definition-editor (label "definition editor"))
      (annotation-property !editor-note (label "editor note"))
      (annotation-property !'imported from'@)
      (object-property !'achieves_planned_objective'@)
      (owl-imports !obo:obi.owl)
      (with-mireot-terms ((time-unit !unit:0000003 !'measurement unit label'@ !oboont:UO "time unit")
			  (mouth !oboont:FMA#FMA_49184
				 (manch (and !snap:MaterialEntity (some !'part_of'@ !'homo sapiens'@))) !oboont:FMA "mouth")
			  (mass !pato:0000125 !'quality'@ !oboont:PATO "mass")
			  (mass-unit !unit:0000002 !'measurement unit label'@ !oboont:UO "mass unit")
			  (gram !unit:00000021 (:individual mass-unit) !oboont:UO "gram")
			  (blood-coagulation !go:007596 !span:Process !obo:GO "blood coagulation")
			  (serpinc1-product !<http://purl.org/obo/owl/PRO#PRO_000003252> !'protein'@ !oboont:PRO  "human Antithrombin-III protein") 
			  (sodium-citrate !<http://purl.org/obo/owl/CHEBI#CHEBI_32142> molecular-entity !oboont:CHEBI "sodium citrate dihydrate") 
			  (edta !<http://purl.org/obo/owl/CHEBI#CHEBI_42191> molecular-entity !oboont:CHEBI "EDTA"))
	;; https://sourceforge.net/tracker/?func=detail&aid=2873869&group_id=125463&atid=703818

	;; put these uris into a http://purl.obolibary.org/obo/obi/example/OBIX_xxxxx
	(let ((fucoidan-75% (fcuri 1))
	      (rm-lowenthal (fcuri 2))
	      (lowenthal-pi-role (fcuri 3))
	      (lowenthal-study-plan (fcuri 4))
	      (fucoidan-investigation-planning (fcuri 5))
	      (fucoidan-study-enrollment (fcuri 6))
	      (fucoidan-study-execution (fcuri 7))
	      (fucoidan-investigation (fcuri 8))
	      (fucoidan-study-design (fcuri 9))
	      (fucoidan-drug-role (fcuri 10))
	      (guar-gum-role (fcuri 11))
	      (tube (fcuri 12))
	      (control-group (fcuri 13))
	      (treated-group (fcuri 14))
	      (control-subject (fcuri 15))
	      (treated-subject (fcuri 16))
	      (to-be-treated-with-guar-gum-role (fcuri 17))
	      (to-be-treated-with-fucoidan-role (fcuri 18))
	      (subject1 (fcuri 19))
	      (subject2 (fcuri 20))
	      (guar-gum-capsule-for-fucoidan-study (fcuri 21))
	      (fucoidan-capsule-for-fucoidan-study (fcuri 22))
	      (mass-of-3-grams (fcuri 23))
	      (mass-of-2point25-grams (fcuri 24))
	      (mass-of-point75-grams (fcuri 25))
	      (single-treatment-of-fucoidan-in-fucoidan-study (fcuri 26))
	      (single-treatment-of-placebo-in-fucoidan-study (fcuri 27))
	      (fucoidan-treatment-portion (fcuri 29))
	      (fucoidan-hospital (fcuri 30))
	      (fucoidan-sample-taking (fcuri 31))
	      (fucoidan-at-iii-berichrome-assay (fcuri 32))
	      (fucoidan-hypothesis (fcuri 33))
	      (fucoidan-conclusion (fcuri 34))
	      (statistical-test (fcuri 35))
	      (interpreting-fucoidan-study-data (fcuri 36))
	      (p-value (fcuri 37))
	      (fucoidan-cohort-assignment (fcuri 38))
	      (measured-data (fcuri 39))
	      ;; the following should move into obi proper - here for now to be able to keep track of them
;	      (informed-consent-process (fcobiuri 1))
;	      (informed-consent-document-agreement-by-patient (fcobiuri 2))
;	      (informing-subject-of-study-arm (fcobiuri 3))
;	      (informing-investigator-of-subject-study-arm (fcobiuri 3))
;	      (informing-investigator-of-subject-study-arm (fcobiuri 4))
;	      (single-blind-study-execution (fcobiuri 5))
;	      (double-blind-study-execution (fcobiuri 6))
;	      (to-be-treated-with-placebo-role (fcobiuri 7))
;	      (to-be-treated-with-active-ingredient-role (fcobiuri 8))
;	      (pill (fcobiuri 9))
;	      (capsule (fcobiuri 10))
;	      (capsule-shell (fcobiuri 11))
;	      (filled-capsule (fcobiuri 12))
;	      (oral-ingestion-of-pill (fcobiuri 13))
;	      (treatment-portion-of-study-execution (fcobiuri 14))
;	      (unblinding-process (fcobiuri 15))
;	      (hospital (fcobiuri 16))
;	      (is-member-of (fcobiuri 17))
;	      (aptt (fcobiuri 18))
;	      (at-iii (fcobiuri 19))
;	      (thrombin-time (fcobiuri 20))
;	      (Antifactor-Xa (fcobiuri 21))
;	      (prothrombin-time (fcobiuri 22))
;	      (sysmex (fcobiuri 23))
;	      (sysmex-ca-6000 (fcobiuri 24))
;	      (berichrome-atIII-kit (fcobiuri 25))
;	      (at-iii-berichrome-assay (fcobiuri 26))
;	      (anticoagulant-containing-test-tube (fcobiuri 27))
;	      (anticoagulant-tube-storage-of-blood (fcobiuri 28))


	      ;; using ids 30-42 for days.

	      ;; the following should move into iao proper - here for now to be able to keep track of them

	      (is-quality-measured-as !obo:IAO_0000417)
;	      (time-measurement-datum !obo:IAO_0000416)
;	      (is-temporal-measure-of !obo:IAO_0000413)
	      (conclusion !obo:IAO_0000144)
	      (hypothesis !obo:IAO_0000415)
;	      (is-quality-specification-of !obo:IAO_0000418)
;	      (quality-is-specified-as !obo:IAO_0000419)
	      (mass-measurement-datum !obo:IAO_0000414)

	      )
	  (macrolet ((mass-measured-as-grams-def (n)
		       `(manch (and mass
				    (some is-quality-measured-as
					  (and mass-measurement-datum
					       (has !'has measurement unit label'@ gram)
					       (has !'has measurement value'@ (literal ,n !xsd:float)))))))
		     (blood-assay (name label example def defsource &optional objective)
		       `(class ,name (label ,label) 
			       (fcusecase)
			       (signedalan)
			       (4obi)
			       (example-of-usage ,(format nil "PMID:19696660#~a" example))
			       (definition ,def)
			       (definition-source ,defsource)
			       :partial
			       (manch (and !'assay'@
					   (some !'has_specified_input'@ !'blood serum specimen'@)
					   (some !'has_specified_output'@ 
						 (and !'scalar measurement datum'@ 
						      (some !'is about'@ blood-coagulation)))
					   ,(if objective `(some !'achieves_planned_objective'@ ,objective))
					   )))))

	    (list
;	     (class informed-consent-process :partial)
;	     (class informed-consent-document-agreement-by-patient :partial)
;	     (class informing-subject-of-study-arm :partial)
;	     (class informing-investigator-of-subject-study-arm :partial)
;	     (class informing-investigator-of-subject-study-arm :partial)
;	     (class single-blind-study-execution :partial)
;	     (class double-blind-study-execution :partial)
;	     (class to-be-treated-with-placebo-role :partial)
;	     (class to-be-treated-with-active-ingredient-role :partial)
;	     (class pill :partial)
					;		(class capsule :partial)
;	     (class capsule-shell :partial)
;	     (class filled-capsule :partial)
;	     (class oral-ingestion-of-pill :partial)
;	     (class treatment-portion-of-study-execution :partial)
;	     (class unblinding-process :partial)
;	     (class hospital :partial)
	     (object-property !'is member of'@)
;	     (class aptt :partial)
;	     (class at-iii :partial)
;	     (class thrombin-time :partial)
;	     (class Antifactor-Xa :partial)
;	     (class prothrombin-time :partial)
;	     (class sysmex :partial)
;	     (class sysmex-ca-6000 :partial)
;	     (class berichrome-atIII-kit :partial)
;	     (class at-iii-berichrome-assay :partial)
;	     (class anticoagulant-containing-test-tube :partial)
;	     (class anticoagulant-tube-storage-of-blood :partial)

	     (object-property is-quality-measured-as)
;; 	     (object-property is-quality-measured-as
;; 	       (label "is quality measured as")
;; 	       (definition "inverse of the relation of is quality measurement of")
;; 	       (junk-relation)
;; 	       (4iao)
;; 	       (signedalan)
;; 	       (metadata-complete)
;; 	       (inverse-of !'is quality measurement of'@))

;; 	     (object-property is-quality-specification-of
;; 	       (label "is quality specification of")
;; 	       (definition "a relation between a data item and a quality of a material entity where the material entity is the specified output of a material transformation which achieves an objective specification that indicates the intended value of the specified quality.")
;; 	       (4iao)
;; 	       (signedbjoern)
;; 	       (signedalan)
;; 	       (metadata-complete)
;; 	       )
	       
;; 	     (object-property quality-is-specified-as
;; 	       (label "quality is specified as")
;; 	       (definition "inverse of the relation of is quality specification of")
;; 	       (junk-relation)
;; 	       (4iao)
;; 	       (signedbjoern)
;; 	       (signedalan)
;; 	       (metadata-complete)
;; 	       (inverse-of is-quality-specification-of))

;; 	     (object-property is-temporal-measure-of
;; 	       (label "is duration of")
;; 	       (definition "relates a process to a time-measurement-datum that represents the duration of the process")
;; 	       (domain !span:Process)
;; 	       (range time-measurement-datum)
;; 	       (super !'is about'@)
;; 	       (metadata-complete)
;; 	       (4iao)
;; 	       (signedalan))
	     
;; 	     (class time-measurement-datum :partial
;; 		    (label "time measurement datum")
;; 		    (definition "A scalar measurement datum that is the result of measuring a temporal interval")
;; 		    (signedalan)
;; 		    (metadata-complete)
;; 		    (4iao)
;; 		    (fcusecase)
;; 		    (manch (and !'scalar measurement datum'@ 
;; 				(all is-temporal-measure-of  !span:Process)
;; 				(all !'has measurement unit label'@ time-unit))))
	     

					;	       (object-property is-member-of)
					;		 (label "is member of")
					;		 (definition "Relating a legal person to an organization in the case where the legal person has a role as member of the organization")
					;		 (definition-source "Person:Alan Ruttenberg")
					;		 (definition-source "Person:Helen Parkinson")
					;		 (signedalan)
					;		 (signedhelen)
					;		 (range !'organization'@)
					;		 (fcusecase)
					;		 (editor-note "2009/10/01 Alan Ruttenberg. Barry prefers generic is-member-of. Question of what the range should be. For now organization. Is organization a population? Would the same relation be used to record members of a population")
					;		 (4obi))


	     ;; involved in running the study
	     (individual (fcusecase) rm-lowenthal
			 (type !taxon:9606))

	     (individual (fcusecase) rm-lowenthal
			 (label "RM Lowenthal")
			 (metadata-complete)
			 (type (manch 
					   (some !'bearer_of'@ !'investigation agent role'@)))
			 (definition-source "PMID:19696660")
			 (value !'is member of'@ fucoidan-hospital)
			 (metadata-complete))


	     ;; the investigation as process
	
	     ;; Probably should be generalized to IAO - communication
	     ;; process, with information giver and receiver role analogous
	     ;; to target of material addition role

					; 	       (class (fcusecase) informed-consent-process 
					; 		      (definition "One or more processes in which subject is taught key facts about a clinical trial both before deciding whether or not to participate, and throughout the study. Agents of the investivation, such as doctors and nurses involved in the trial, explain the details of the study.")
					; 		      (4obi)
					; 		      (definition-source "http://clinicaltrials.gov/ct2/info/glossary#informed")
					; 		      (editor-note "09/28/2009 Alan Ruttenberg: This is made a subclass of the higher level processual entity in BFO because I don't want to take a stand on whether it is a process aggregate. Analogous to the situation with Material entity.")
					; 		      (signedalan)
					; 		      (label "informed consent process")
					; 		      :partial (manch (and !span:ProcessualEntity
					; 					   (some !oborel:has_participant
					; 						 (some !'has_role'@ !'investigation agent role'@))
					; 					   (some !oborel:has_participant
					; 						 (manch (some !'has_role'@ !'study subject role'@)))
					; 					   )))

	
	     ;; (class (fcusecase) informed-consent-document-agreement-by-patient
	     ;; 		      (definition "A process in which a subject receives an informed consent document and agrees that they have understood it")
	     ;; 		      (signedalan)
	     ;; 		      (4obi)
	     ;; 		      (editor-note "09/28/2009 Alan Ruttenberg. There's a need for a general process like this in IAO - document and person in, signed document (and associated obligations, rights, out")
	     ;; 		      (label "subject agrees they understand informed consent document")
	     ;; 		      :partial
	     ;; 		      (manch (and !'planned process'@
	     ;; 				  (some !oborel:part_of informed-consent-process))))

	     ;; 	       (class (fcusecase) informing-subject-of-study-arm :partial
	     ;; 		      (4obi)
	     ;; 		      (label "informing subject of study arm")
	     ;; 		      (definition "A process in which the subject is made aware of which study arm they are participating in, for example whether they are receiving a placebo or a treatment with an investigational compound.")
	     ;; 		      (signedalan)
	     ;; 		      (editor-note "09/28/2009 Alan Ruttenberg. This and the class informing-investigator-of-study-arm are defined in order to solve the question of how to represent single and double blind experiments. To represent the aspect of blinding pertaining to subjects (happens in single and double blinding) we say that that the study execution doesn't include any processes of this sort")
	     ;; 		      (manch (and !span:Process
	     ;; 				  (some !oborel:has_participant
	     ;; 					(some !'has_role'@ !'study subject role'@))
	     ;; 				  (some !oborel:part_of informed-consent-process))))
	       
	     ;; 	       (class (fcusecase) informing-investigator-of-subject-study-arm :partial
	     ;; 		      (4obi)
	     ;; 		      (label "informing investigator of subject study arm")
	     ;; 		      (definition "A process in which an investigator is made aware of which study arm that a patient is participating in, for example whether they are receiving a placebo or a treatment with an investigational compound.")
	     ;; 		      (signedalan)
	     ;; 		      (editor-note "09/28/2009 Alan Ruttenberg. This and the class informing-subject-of-study-arm are defined in order to solve the question of how to represent single and double blind experiments. To represent the aspect of double blinding pertaining to investigators, we say that the study execution doesn't include any processes of this sort")
	     ;; 		      (manch (and !span:Process
	     ;; 				  (some !oborel:has_participant
	     ;; 					(some !'has_role'@ !'investigation agent role'@))
	     ;; 				  (some !'has_specified_input'@
	     ;; 					(some !'denotes'@ (some !'bearer_of'@ !'study subject role'@)))
	     ;; 				  )))
	     
	     ;; 	       (class (fcusecase) treatment-portion-of-study-execution :partial (manch (and !'planned process'@
	     ;; 											    (some !oborel:part_of !'study design execution'@)))
	     ;; 		      (label "treatment portion of study execution")
	     ;; 		      (signedalan)
	     ;; 		      (4obi)
	     ;; 		      (definition "A planned process, part of a study design execution, during which the treatment of subjects is ongoing")
	     ;; 		      (editor-note "09/28/2009 Alan Ruttenberg. Needed because we have to have a process to scope blinding over"))


	     ;; 	       (class (fcusecase) single-blind-study-execution :complete
	     ;; 		      (4obi)
	     ;; 		      (label "single blind study execution")
	     ;; 		      (definition "A single blind study execution is defined as any study execution in which the subjects are not informed of which study arm they are part of during the portion of the trial when the subjects are being treated")
	     ;; 		      (signedalan)
	     ;; 		      (definition-source "http://clinicaltrials.gov/ct2/info/glossary#single")
	     ;; 		      (manch (and treatment-portion-of-study-execution
	     ;; 				  (all !oborel:has_part (not informing-subject-of-study-arm)))))

	     ;; 	       (class (fcusecase) double-blind-study-execution 
	     ;; 		      (label "double blind study execution")
	     ;; 		      (4obi)
	     ;; 		      (definition "A double blind study execution is defined as any study execution in which neither the subjects nor the investigators are informed of which study arm the subjects are part of during the portion of the trial when the subjects are being treated")
	     ;; 		      (signedalan)
	     ;; 		      (definition-source "http://clinicaltrials.gov/ct2/info/glossary#double")	   
	     ;; 		      :complete
	     ;; 		      (manch (and treatment-portion-of-study-execution
	     ;; 				  (all !oborel:has_part (not (or informing-subject-of-study-arm
	     ;; 								 informing-investigator-of-subject-study-arm
	     ;; 								 ))))))

	     ;; 	       (class (fcusecase) unblinding-process :partial (manch (and !'planned process'@
	     ;; 									  (some !oborel:part_of !'study design execution'@)
	     ;; 									  (some !oborel:part_of informing-subject-of-study-arm)))
	     ;; 		      (label "unblinding process")
	     ;; 		      (definition "The part of the study execution in which the subjects are told what study arm they are in and in which the investigators are told which subjects are in which trials")
	     ;; 		      (signedalan)
	     ;; 		      (4obi))
	  
	     (individual (fcusecase) fucoidan-investigation
			 (label "the overall investigation that includes the fucoidan study")
			 (type !'investigation'@)
			 (signedhelen)
			 (metadata-complete)
			 (source-fucoidan-paper)
			 (value !oborel:has_part fucoidan-study-execution)
			 (value !oborel:has_part fucoidan-investigation-planning))

	     (individual (fcusecase) lowenthal-study-plan
			 (label "RM Lowenthal's plan to develop a study design for fucoidan study")
			 (type !'plan'@)
			 (signedhelen)
			 (uncurated)
			 (source-fucoidan-paper)
			 (value !'inheres_in'@ rm-lowenthal)
			 (value !'is_realized_by'@ fucoidan-investigation-planning))

	     (individual (fcusecase) fucoidan-investigation-planning
			 (label "planning the fucoidan study")
			 (type !'planning'@)
			 (signedhelen)
			 (uncurated)
			 (source-fucoidan-paper)
			 (value !'has_specified_output'@ fucoidan-study-design)
			 (value !'realizes'@ lowenthal-study-plan))

	     (individual (fcusecase) fucoidan-study-execution
			 (label "study design execution in fucoidan investigation")
			 (signedhelen)
			 (uncurated)
			 (source-fucoidan-paper)
			 (type 
			  (manch (and 
				  !'study design execution'@
				  (all !oborel:has_part (not !'informing subject of study arm'@)))))
			 )
	     	     
	     (individual (fcusecase) fucoidan-treatment-portion
			 (label "treatment portion of study design execution in fucoidan investigation")
			 (signedalan)
			 (source-fucoidan-paper)
			 (uncurated)
			 (type 
			  (manch (and 
				  !'treatment portion of study execution'@
				  (all !oborel:has_part (not !'informing subject of study arm'@))
				  (some !oborel:has_part fucoidan-sample-taking)
				  (some !oborel:has_part !'anticoagulant tube storage of blood specimen'@))))
			 (value !oborel:part_of fucoidan-study-execution)
			 )

	     (individual (fcusecase) fucoidan-study-design
			 (label "Plan for pilot fucoidan study")
			 (signedhelen)
			 (uncurated)
			 (type !'parallel group design'@obi)
			 (value !'is_specified_output_of'@ fucoidan-investigation-planning)
			 (editor-note "2009/10 Helen Parkinson: This should be a more specific subclass of study design. Parallel group and reference design were suggested. Need to further investigate and determine disjoints."))

	     (individual (fcusecase) fucoidan-study-enrollment
			 (label "enrolling subjects for fucoidan study")
			 (uncurated)
			 (signedhelen)
			 (type !'human subject enrollment'@))

	     ;; subjects - there are going to be a lot of instances of these. 

					; 	       (class (fcusecase) to-be-treated-with-active-ingredient-role
					; 		      :partial !'study subject role'@
					; 		      (4obi)
					; 		      (label "to be treated with active ingredient role")
					; 		      (definition "A study subject role which begins to exist when a subject is assigned to be one of those who will receive active ingredient, and is realized in a study execution in which they receive the active ingredient")
					; 		      (signedalan)
					; 		      )

	     (class (fcusecase) to-be-treated-with-fucoidan-role
		    :complete
		    (manch (and !'to be treated with active ingredient role'@
				(some !'is_realized_by'@ single-treatment-of-fucoidan-in-fucoidan-study)
				))
		    (label "role of subject to be treated with fucoidan in the fucoidan pilot study")
		    (definition "role of any subject in the fucoidan study who is to be treated with fucoidan pilot study as active ingredient")
		    (metadata-complete)
		    (source-fucoidan-paper)
		    (signedalan)
		    )

	     ;; 	       (class (fcusecase) oral-ingestion-of-pill
	     ;; 		      (label "oral ingestion of pill")
	     ;; 		      (definition "An adding a material entity to target with the entity is a pill and the target is the mouth")
	     ;; 		      (4obi)
	     ;; 		      (signedalan)
	     ;; 		      :complete
	     ;; 		      (manch (and (some !'realizes'@ (and !'material to be added role'@
	     ;; 							  (some !'role_of'@ pill)))
	     ;; 				  (some !'realizes'@
	     ;; 					(and !'target of material addition role'@
	     ;; 					     (some !'role_of'@ mouth)))
	     ;; 				  (some !'has_specified_input'@ pill)
	     ;; 				  )))
	   
	     ;; (class (fcusecase) filled-capsule (4obi) (label "filled capsule")
	     ;; 		      (definition "A pill in the form of a small rounded gelatinous container with medicine inside.")
	     ;; 		      (definition-source "http://www.golovchenko.org/cgi-bin/wnsearch?q=capsule#2n")
	     ;; 		      (signedalan)
	     ;; 		      :partial (manch (and pill (some !'has_part'@ capsule-shell))))

	     ;; 	       (class (fcusecase) pill (label "pill")
	     ;; 		      (4obi)
	     ;; 		      (signedalan)
	     ;; 		      (definition "A dose of medicine or placebo in the form of a small pellet.")
	     ;; 		      (definition-source "http://www.golovchenko.org/cgi-bin/wnsearch?q=pill#2n")
	     ;; 		      :partial !snap:MaterialEntity)

	     ;; 	       (class (fcusecase) capsule-shell (4obi) (label "capsule shell")
	     ;; 		      (definition "a small rounded gelatinous container")
	     ;; 		      (signedalan)
	     ;; 		      (definition-source "http://www.golovchenko.org/cgi-bin/wnsearch?q=capsule#2n")
	     ;; 		      :partial !snap:MaterialEntity)


					;	       (class (fcusecase) edta (label "EDTA") 
					;		      :partial molecular-entity)

	     (class molecular-entity :partial)

;; 	     (class (fcusecase) mass-measurement-datum
;; 		    (4iao)
;; 		    (label "mass measurement datum")
;; 		    (signedalan)
;; 		    (definition "A scalar measurement datum that is the result of measurement of mass quality")
;; 		    (metadata-complete)
;; 		    :partial 
;; 		    (manch (and !'scalar measurement datum'@ 
;; 				(all !'has measurement unit label'@ mass-unit)
;; 				(all !'is quality measurement of'@ mass))))
	     
	     (class (fcusecase) mass-of-3-grams (label "mass measured to be 3 grams") :complete
		    (mass-measured-as-grams-def 3.0)
		    (definition "A mass quality that has been measured, to the precision of whatever instrument did the measuring, to be 3 grams")
		    (editor-note "2009/10/18 Alan Ruttenberg. OBO/OBI doesn't yet have a standard way of representing determinable qualities. This is a strategy that works however, and is arguable more accurate. The class members are those mass quality instances that are measured to be a specific number of grams. An actualy mass of exactly some specified number of grams is, statistically speaking, highly improbable")
		    )
	     
	     (class (fcusecase) mass-of-2point25-grams (label "mass measured to be 2.25 grams") :complete
		    (definition "A mass quality that has been measured, to the precision of whatever instrument did the measuring, to be 2.25 grams")
		    (mass-measured-as-grams-def 2.25)
		    (editor-note "2009/10/18 Alan Ruttenberg. OBO/OBI doesn't yet have a standard way of representing determinable qualities. This is a strategy that works however, and is arguable more accurate. The class members are those mass quality instances that are measured to be a specific number of grams. An actualy mass of exactly some specified number of grams is, statistically speaking, highly improbable"))
		    
	     (class (fcusecase) mass-of-point75-grams (label "mass measured to be .75 grams") :complete
		    (definition "A mass quality that has been measured, to the precision of whatever instrument did the measuring, to be .75 grams")
		    (mass-measured-as-grams-def .75)
		    (editor-note "2009/10/18 Alan Ruttenberg. OBO/OBI doesn't yet have a standard way of representing determinable qualities. This is a strategy that works however, and is arguable more accurate. The class members are those mass quality instances that are measured to be a specific number of grams. An actualy mass of exactly some specified number of grams is, statistically speaking, highly improbable"))
	       
	     (class (fcusecase) guar-gum-capsule-for-fucoidan-study
		    (label "guar gum capsule for fucoidan study")
		    (definition "guar gum capsule used in the fucoidan study cited as definition source")
		    (source-fucoidan-paper)
		    (metadata-complete)
		    :complete
		    (manch (and !'filled capsule'@
				(some !'has_part'@ 
				      (and
				       !'guar gum'@
				       (some !'has_quality'@ mass-of-3-grams))
				      !'capsule shell'@))))

	     (class (fcusecase) fucoidan-capsule-for-fucoidan-study :complete
		    (label "fucoidan capsule used in fucoidan pilot study")
		    (definition "A single capsule with 75% by weight fucoidan. A total of 3 grams of this capsule is given to treated subjects.")
		    (editor-note "2009-11-17 Alan Ruttenberg. It is unclear from the paper whether each capsule is 3 grams or whether multiple capsules weighing a total of 3 grams are administered")
		    (metadata-complete)
		    (source-fucoidan-paper)
		    (manch (and !'filled capsule'@
				(some !'has_part'@
				      (and
				       (some !'has grain'@ !'fucoidan'@)
				       (some !'has_quality'@ mass-of-2point25-grams)
				       (some !'has_role'@ !'test substance role'@)))
				(some !'has_part'@
				      (and
				       (some !'has_quality'@ mass-of-point75-grams)))
				(some !'has_part'@ !'capsule shell'@)
				)))

					; http://journals.prous.com/journals/servlet/xmlxsl/pk_journals.xml_summaryn_pr?p_JournalId=6&p_RefId=948919
					; http://www.vitacost.com/Doctors-Best-Best-Fucoidan-70#IngredientFacts

	     (class (fcusecase) single-treatment-of-placebo-in-fucoidan-study 
		    (label "single treatment of guar gum in fucoidan pilot study")
		    (signedalan)
		    (source-fucoidan-paper)
		    (uncurated)
		    :complete
		    (manch (and !'oral ingestion of pill'@
				(some !'has_specified_input'@ guar-gum-capsule-for-fucoidan-study)
				(some !'realizes'@ (and !'material to be added role'@
							(some !'role_of'@ guar-gum-capsule-for-fucoidan-study)))
				(has !'part_of'@ fucoidan-study-execution)
				)))


	     (class (fcusecase) single-treatment-of-fucoidan-in-fucoidan-study 
		    (label "single treatment of fucoidan in fucoidan study")
		    (signedalan)
		    (source-fucoidan-paper)
		    (uncurated)
		    :complete
		    (manch (and !'oral ingestion of pill'@
				(some !'has_specified_input'@ fucoidan-capsule-for-fucoidan-study)
				(some !'realizes'@ (and !'material to be added role'@
							(some !'role_of'@
							      fucoidan-capsule-for-fucoidan-study)))
				(has !'part_of'@ fucoidan-study-execution)
				)))

	     ;; 	       (class (fcusecase) to-be-treated-with-placebo-role :partial !'study subject role'@
	     ;; 		      (4obi)
	     ;; 		      (label "to be treated with placebo role")
	     ;; 		      (signedalan)
	     ;; 		      (definition "A study subject role which begins to exist when a subject is assigned to be one of those who will receive a placebo, and realized in a study execution in which they receive the placebo")
	     ;; 		      )

	     (class (fcusecase) to-be-treated-with-guar-gum-role :complete
		    (manch (and !'to be treated with placebo role'@
				(some !'is_realized_by'@ single-treatment-of-placebo-in-fucoidan-study)))
		    (label "role of subject to be treated with guar gum in the fucoidan pilot study")
		    (source-fucoidan-paper)
		    (metadata-complete)
		    (definition "Role of any subject in the fucoidan study who is to be treated with guar gum in the pilot study as placebo")
		    (signedalan))

	     (class (fcusecase) control-subject :complete
		    (manch (and !'homo sapiens'@
				(some !'bearer_of'@ to-be-treated-with-guar-gum-role)))
		    (label "subject in control arm of fucoidan pilot study")
		    (source-fucoidan-paper)
		    (metadata-complete)
		    (definition "Exactly those subjects who are assigned to be treated with active ingredient in the fucoidan pilot study")
		    (signedalan))

	     (class (fcusecase) treated-subject :complete
		    (manch (and !'homo sapiens'@
				(some !'bearer_of'@ to-be-treated-with-fucoidan-role)))
		    (label "subject in treated arm of fucoidan pilot study")
		    (metadata-complete)
		    (source-fucoidan-paper)
		    (definition "Exactly those subjects who are assigned to be treated with placebo in the fucoidan pilot study")
		    (signedalan))

	     (class fucoidan-sample-taking
	       (label "Taking blood specimen from subject in fucoidan study")
	       (source-fucoidan-paper)
	       (uncurated)
	       :complete
	       (manch (and !'collecting specimen from organism'@
			   (some !'has_specified_input'@ (or control-subject treated-subject))
			   (some !'has_specified_output'@ !'blood serum specimen'@))))

	     
	     ;; 	       (class anticoagulant-containing-test-tube :partial 
	     ;; 		      (label "anticoagulant-containing test tube")
	     ;; 		      (definition "A 'blue top' test tube that contains anticoagulant for storing blood specimens'")
	     ;; 		      (fcusecase) (4obi) (signedalan)
	     ;; 		      (manch (and !'test tube'@ 
	     ;; 				  (some !'contains'@
	     ;; 					(some !'has_part'@ (some !'has grain'@ sodium-citrate))
	     ;; 					(some !'has_part'@ (some !'has grain'@ edta))))))

	     ;; 	       (class anticoagulant-tube-storage-of-blood
	     ;; 		 (label "anticoagulant tube storage of blood specimen")
	     ;; 		 (definition "Storage of a blood specimen in a tube with anticoagulant")
	     ;; 		 :partial
	     ;; 		 (manch (and !'storage'@
	     ;; 			     (some !'has_specified_output'@
	     ;; 				   (and anticoagulant-containing-test-tube
	     ;; 					(some !'contains'@ !'blood serum specimen'@)))
	     ;; 			     (some !'has_specified_input'@ !'blood serum specimen'@)))
	     ;; 		 (fcusecase)
	     ;; 		 (4obi)
	     ;; 		 (signedalan))


	     ;; 	       (blood-assay aptt "activated partial thromboplastin time (aPTT) assay" "The activated partial thromboplastin time (aPTT) was determined using Dade Actin FSL activated PTT reagent."
	     ;; 			    "An activated partial thromboplastin time (aPTT) assay is a an assay measuring the efficacy of both the 'intrinsic' (now referred to as the contact activation pathway) and the common coagulation pathways. In order to activate the intrinsic pathway, phospholipid, an activator (such as silica, celite, kaolin, ellagic acid), and calcium (to reverse the anticoagulant effect of the oxalate) are mixed into the plasma sample . The time is measured until a thrombus (clot) forms."
	     ;; 			    "WEB:http://en.wikipedia.org/wiki/Partial_thromboplastin_time@2008/10/06")
	     ;; 	       (blood-assay-unfinished aptt)

	     ;; 	       (blood-assay at-iii "antithrombin-III (AT-III) assay" "The antithrombin-III (AT-III) was determined using Berichrom Antithrombin-III (A)."
	     ;; 			    "A test to measure the amount of antithrombin III in blood."
	     ;; 			    "WEB:http://www.muschealth.com/lab/content.aspx?id=150006@2009/08/06"
	     ;; 			    !'analyte measurement objective'@
	     ;; 			    )
	     
	     ;; 	       (class at-iii :partial
	     ;; 		      (manch (some !'realizes'@
	     ;; 				   (and !'analyte role'@
	     ;; 					(some !'role_of'@
	     ;; 					      (and !'scattered molecular aggregate'@ (some !'has grain'@ serpinc1-product)))))))

	     ;; 	       ;; (class at-iii-berichrome-assay :partial (manch (and at-iii
	     ;; ;; 								   (exactly !'has_specified_output'@ 1)
	     ;; ;; 								   (some !'has_specified_output'@ !'scalar measurement datum'@)
	     ;; ;; 								   (some !'realizes'@
	     ;; ;; 									 (and !'reagent role'@
	     ;; ;; 									      (some !'role_of'@ berichrome-atIII-kit)))
	     ;; ;; 								   (some !'has_participant'@ berichrome-atIII-kit)))
								 
	     ;; ;; 		      (label "antithrombin-III (AT-III) berichrome assay")
	     ;; ;; 		      (definition "An antithrombin-III (AT-III) assay in which exogenous bovine thrombin and heparin are added to test plasma to form a thrombin-heparin-AT complex. The residual thrombin not bound then hydrolyzes the p-nitroalanine substrate to produce a yellow color, which is read at 405 nm. The intensity of color produced is inversely proportional to the AT present. A calibration is done with standard human plasma reagent and results for a given speciment are reported as a percentage relative to the standard")
	     ;; ;; 		      (definition-source "WEB:http://www.clinchem.org/cgi/content/full/43/9/1783@2009/08/06")
	     ;; ;; 		      (editor-note "todo Reagents from Berichrom(r) Antithrombin III (A) and standard human plasma")
	     ;; ;; 		      (fcusecase)
	     ;; ;; 		      (4obi))

	     ;; 	       (class berichrome-atIII-kit (label "Berichrom(r) Antithrombin III (A) Kit") :partial !'processed material'@
	     ;; 		      (definition "For the chromogenic determination of antithrombin III. Autoanalyzer method for undiluted samples. For the quantitative chromogenic determination of the functional activity of antithrombin III in plasma on autoanalyzers for the diagnosis of diminished AT III synthesis, increased consumption, and for monitoring substitution therapy. Berichrom(r) Antithrombin III (A) is used for the rapid determination of the physiologically active antithrombin III and permits the diagnsis of congenital and acquired antithrombin III deficiency, a condition frequently associated with an increased risk of thrombosis. Acquired antithrombin III deficiencies frequently occur due to consumption following major operations or due to disseminated intravascular coagulation (DIC) in cases of septicaemia, nephroses, liver parenchymal damage (hepatitis, drug intoxication, alcoholism) and oestrogen-containing contraceptives. The test permits early detection of patients at increased risk for thrombosis. Kit contains: 6 x for 5.0 mL Thrombin (bovine), 3 x for 3.0 mL Substrate Reagent, 1 x 30.0 mL Buffer Solution")
	     ;; 		      (definition-source "WEB:http://www.dadebehring.com/edbna2/ebusiness/products/productDetail.jsp?sDiscipline=Hemostasis&FirstLevelOID=-13075&sCategory_Name=BCS&SecondLevelOID=-13895&ThirdLevelOID=-13904&selectedProductType=Houtput-Assays+-+non+US&sProductName=OWWR15&PROD_OID=44198@2009/08/06")
	     ;; 		      (signedalan)
	     ;; 		      (fcusecase)
	     ;; 		      (4obi))

	     ;; 	       (individual sysmex (label "Sysmex Corporation, Kobe, Japan")
	     ;; 			   (type !'organization'@)
	     ;; 			   (definition-source "WEB:http://www.sysmex.com/@2009/08/06")
	     ;; 			   (fcusecase)
	     ;; 			   (4obi))

	     (individual fucoidan-cohort-assignment 
	       (label "group assignment for fucoidan study")
	       (definition "A group assignment process in which participants in the fucoidan study are assigned to either the control or treated arm of the study")
	       (fcusecase)
	       (signedalan)
	       (type
		(manch (and !'group assignment'@
			    (some !'has_specified_input'@ !'homo sapiens'@)
			    (some !'has_specified_output'@
				  (or to-be-treated-with-guar-gum-role
				      to-be-treated-with-fucoidan-role))))))
			      

	     ;; 	       (class sysmex-ca-6000 (label "Sysmex CA-6000 Coagulation Analyzer")
	     ;; 		      (definition "The Sysmex CA-6000 automated coagulation analyzer is a random access instrument that is capable of performing 20 clot-based and chromogenic assays")
	     ;; 		      (definition-source "web:http://www.clinchem.org/cgi/content/full/43/9/1783@2009/08/06")
	     ;; 		      (signedalan)
	     ;; 		      (4obi)
	     ;; 		      (fcusecase)
	     ;; 		      :partial (manch (and !'instrument'@
	     ;; 					   (has !'is_manufactured_by'@ sysmex))))

	     (class fucoidan-at-iii-berichrome-assay :partial !'antithrombin-III (AT-III) berichrome assay'@)
	     (class fucoidan-at-iii-berichrome-assay
	       (label "antithrombin assay in the fucoidan study")
	       (definition "antithrombin assay in the fucoidan study, which used the Berichrom(r) Antithrombin III (A) Kit for reagents and the Sysmex CA-6000 Coagulation Analyzer for measurement")
	       (source-fucoidan-paper)
	       (fcusecase)
	       (signedalan)
	       (metadata-complete)
	       :partial
	       (manch (and (some !'has_specified_input'@ (some !'is_specified_output_of'@ fucoidan-sample-taking))
			   (exactly !'has_specified_input'@ 1)
			   (some !'realizes'@
				 (some !'function_of'@ !'Sysmex CA-6000 Coagulation Analyzer'@))
			   (some !'has_participant'@ !'Sysmex CA-6000 Coagulation Analyzer'@ ))))

	     (class measured-data :partial !'scalar measurement datum'@)
	     (class measured-data (label "measured AT3 levels of treated subjects on day 1 and day 4 in the fucoidan pilot study") 
		    (definition "The class of measurment datum that are those outputs of antithrombin assays on the subjects taken on day 1 or day 4 of the study")
	       (source-fucoidan-paper)
	       (fcusecase)
	       (signedalan)
		    :complete
		    (manch (some !'is_specified_output_of'@ fucoidan-at-iii-berichrome-assay)))
									  
		    
	     ;; 	       (blood-assay thrombin-time "thrombin time assay" "The thrombin time was determined using thromboclotin assay kit."
	     ;; 			    "A  thrombin time assay is on in which after liberating the plasma from whole blood by centrifugation, bovine Thrombin is added to the sample of plasma. The clot is formed and is detected optically or mechanically by a coagulation instrument. The time between the addition of the thrombin and the clot formation is recorded as the thrombin clotting time"
	     ;; 			    "WEB:http://en.wikipedia.org/wiki/Thrombin_time@2009/10/06"
	     ;; 			    )
	     ;; 	       (blood-assay-unfinished thrombin-time)


	     ;; 	       (blood-assay Antifactor-Xa "spectrolyse heparin antifactor-Xa assay" "Antifactor-Xa (anti-Xa) was determined using spectrolyse heparin (Xa) (Trinity Biotech plc, Bray, County Wicklow, Ireland)."
	     ;; 			    "A Spectrolyse Heparin (Xa) assay is intended for the quantitative determination of therapeutic Heparin in human plasma.

	     ;; The principle inhibitor of Thrombin, Factor Xa and other coagulation serine proteases in plasma is Antithrombin III. The rate of inhibition, under normal conditions, is slow, but can be increased several thousand-fold by Heparin. This mechanism accounts for the anticoagulant effect of Heparin. Low Molecular Weight Therapeutic Heparin (LMWH) preparations appear to catalyze the reaction between Factor Xa and Antithrombin III more readily than the reaction between Thrombin and Antithrombin III while standard Heparin catalyzes both reactions equally. The Factor Xa inhibition test is the most useful test for assaying the widest variety of therapeutic Heparin preparations. In this method, when both Factor Xa and Antithrombin III are present in excess, the rate of Factor Xa inhibition is directly proportional to the Heparin concentration. The residual Factor Xa activity, measured with a Factor Xa-specific chromogenic substrate, is inversely proportional to the Heparin concentration."
	     ;; 			    "WEB:http://www.kordia.nl/en/product/hemostasis/specialty_kits__reagens/598/spectrolyse_heparin_anti_xa@2009/08/06"
	     ;; 			    )

	     ;; 	       (blood-assay-unfinished Antifactor-Xa)

	     ;; 	       (blood-assay prothrombin-time "prothrombin time assay" "The prothrombin time (PT) was quantitatively determined using RecombiPlasTin (Instrumentation Laboratory Company, Lexington, Massachusetts, USA)."
	     ;; 			    "The prothrombin time is an assay most commonly measured using blood plasma. Blood is drawn into a test tube containing liquid citrate, which acts as an anticoagulant by binding the calcium in a sample. The blood is mixed, then centrifuged to separate blood cells from plasma. In newborns, whole blood is used. The plasma is analyzed by a biomedical scientist on an automated instrument at 37 degrees C, which takes a sample of the plasma. An excess of calcium is added (thereby reversing the effects of citrate), which enables the blood to clot again. For an accurate measurement the proportion of blood to citrate needs to be fixed; many laboratories will not perform the assay if the tube is underfilled and contains a relatively high concentration of citrate. If the tube is underfilled or overfilled with blood, the standardized dilution of 1 part anticoagulant to 9 parts whole blood is no longer valid. For the prothrombin time test the appropriate sample is the blue top tube, or sodium citrate tube, which is a liquid anticoagulant. Tissue factor (also known as factor III or thromboplastin) is added, and the time the sample takes to clot is measured optically. Some laboratories use a mechanical measurement, which eliminates interferences from lipemic and icteric samples. The prothrombin ratio is the prothrombin time for a patient, divided by the result for control plasma."
	     ;; 			    "WEB:http://en.wikipedia.org/wiki/Prothrombin_time@2009/10/06"
	     ;; 			    )
	     ;; 	       (blood-assay-unfinished prothrombin-time)

	     #|
	     Coagulation tests 

	     All tests were performed on the 

	     Sysmex CA6000 (Sysmex Corporation, Kobe, Japan) automated instrument

	     http://www.clinchem.org/cgi/content/full/43/9/1783
	     AT: Exogenous bovine thrombin and heparin are added to test plasma to form a thrombin-heparin-AT complex. The residual thrombin not bound then hydrolyzes the p-nitroalanine substrate to produce a yellow color, which is read at 405 nm. The intensity of color produced is inversely proportional to the AT present. A single calibration curve was performed at the onset and used for the duration of sample testing.



	     citrated plasma samples

	     According to the manufacturer's specifications (Dade Behring, Marburg, Germany)

	     The activated partial thromboplastin time (aPTT) was determined using Dade Actin FSL activated PTT reagent.

	     The antithrombin-III (AT-III) was determined using Berichrom Antithrombin-III (A).

	     http://www.dadebehring.com/edbna2/ebusiness/products/productDetail.jsp?sDiscipline=Hemostasis&FirstLevelOID=-13075&sCategory_Name=BCS&SecondLevelOID=-13895&ThirdLevelOID=-13904&selectedProductType=H-Assays+-+non+US&sProductName=OWWR15&PROD_OID=44198

	     5 or 15 ml kits

	     http://www.freepatentsonline.com/5646007.html

	     sodium citrate is anticoagulant
	     http://books.google.com/books?id=cHAjsUgegpQC&pg=RA1-PA472&lpg=RA1-PA472&dq=%22Substrate+Reagent%22+coagulation&source=bl&ots=qvOEvCHnG5&sig=r2KyNzH3K1FIyenMHzKP3e2tpI4&hl=en&ei=zPDLSt2kLs_ElAequYXcBQ&sa=X&oi=book_result&ct=result&resnum=1#v=onepage&q=%22Substrate%20Reagent%22%20coagulation&f=false



	     The thrombin time was determined using thromboclotin assay kit.

	     Antifactor-Xa (anti-Xa) was determined using spectrolyse heparin (Xa) (Trinity Biotech plc, Bray, County Wicklow, Ireland).

	     The prothrombin time (PT) was quantitatively determined using RecombiPlasTin (Instrumentation Laboratory Company, Lexington, Massachusetts, USA). 
	     |#

	     
	     ;; antithrombin-III increased  from 113.5 to 117% (n U 10, P U 0.03).
	     ;; The percentages are 

	     ;; http://www.doctorslounge.com/hematology/labs/inr.htm

 	     ;; (class (fcusecase) hospital
;;  	       (label "hospital")
;;  	       (definition "A medical organization at which sick or injured people are given clinical care")
;;  	       (definition-source "http://www.golovchenko.org/cgi-bin/wnsearch?q=hospital#2n")
;;  	       (editor-note "Helen and Alan modified the wording from the wordnet definition")
;;  	       (4obi)
;;  	       (signedalan)
;;  	       (signedhelen)
;;  	       (example-of-usage "human ethics approval was obtained from the Southern Tasmania Health & Medical Human Research Ethics Committee and the Royal Hobart Hospital Research Ethics Committee [pmid:19696660]")
;;  	       :partial !'organization'@)

	     (individual (fcusecase) fucoidan-hospital
	       (label "Royal Hobart Hospital")
	       (definition-source "http://www.dhhs.tas.gov.au/hospitals/royal_hobart")
	       (type !'hospital'@)
	       (metadata-complete)
	       (signedhelen)
	       (signedalan))
	     
	     (individual fucoidan-hypothesis
	       (label "fucoidan may have anticoagulant activity in vivo")
	       (type hypothesis)
	       (definition "The hypothesis that fucoidan may have anticoagulant activity in vivo, as expressed, slightly obliquely, in the first paragraph of the abstract of the paper 'Pilot clinical study to evaluate the anticoagulant activity of fucoidan', by Lowenthal et. al.")
	       (signedalan)
	       (fcusecase)
	       (editor-note "2009/10/12 Alan Ruttenberg. There isn't a clear statement of hypothesis in the paper but one can infer the hypothesis from the paper's abstract, relevant bit cited in definition source.")
	       (definition-source "PMID:19696660#Seaweed-derived heparin-like substances such as fucoidan have been extensively studied in vitro as potential blood anticoagulants. However, there have been no human studies investigating the anticoagulant activity of fucoidan when administered orally. This pilot clinical trial was aimed to assess the safety and clinical effects of fucoidan ingestion on hemostasis as well as study its in-vitro anticoagulant activity."))

	     (individual fucoidan-conclusion 
	       (type conclusion)
	       (label "fucoidan has a small statistically significant effect on AT3 level but no useful clinical effect as in-vivo anticoagulant")
	       (definition "The conclusion that fucoidan has a small statistically significant effect on AT3 level but no useful useful clinical effect as in-vivo anticoagulant, as expressed in the final paragraph of the paper first paragraph of the abstract of the paper 'Pilot clinical study to evaluate the anticoagulant activity of fucoidan', by Lowenthal et. al.")
	       (definition-source "PMID:19696660#In conclusion, this study demonstrated that a small quantity of bioavailable fucoidan, given orally, had a modest but significant effect on some of the coagulation assays, in particular, the intrinsic pathway. Changes in the coagulation tests were still within reference ranges and unlikely in themselves to be 'clinically valuable'.")
	       (fcusecase)
	       (signedalan)
	       (value !'is_specified_output_of'@ interpreting-fucoidan-study-data)
	       )

	     (individual interpreting-fucoidan-study-data
	       (label "interpreting fucoidan study data")
	       (type !'interpreting data'@)
	       (value !'has_specified_input'@ p-value)
	       (value !'has_specified_output'@ fucoidan-conclusion)
	       (value !'has_participant'@ rm-lowenthal)
	       (definition "The process of interpreting the results of the statistical analyses on the fucoidan study assays")
	       (definition-source "PMID:19696660")
	       (signedalan)
	       (fcusecase))

;; 	      (class conclusion (label "conclusion textual entity")
;;  		    :partial !'information content entity'@
;;  		    (definition "A conclusion is a textual entity that expresses the results of reasoning about a problem")
;; 		    (example-of-usage "Conclusions are are typically found towards the end of scientific papers")
;;  		    (signedalan)
;;  		    (4iao)
;;  		    (fcusecase)
;; 		    (metadata-complete)
;;  		    (example-of-usage "The conclusion that fucoidan does not have a useful clinical effect as an in-vivo anticoagulant is in the text of a 'Pilot clinical study to evaluate the anticoagulant activity of fucoidan', by Lowenthal et.al.PMID:19696660 along with the results upon which the conclusion is based "))

;; 	     (class conclusion (label "conclusion")
;; 		    :partial !'textual entity'@
;; 		    (definition "A conclusion is a textual entity that expresses the results of reasoning about a problem, for instance as is typically found towards the end of scientific papers.")
;; 		    (signedalan)
;; 		    (4obi)
;; 		    (fcusecase)
;; 		    (example-of-usage "that fucoidan has a small statistically significant effect on AT3 level but no useful clinical effect as in-vivo anticoagulant, a paraphrase of part of the last paragraph of the discussion section of the paper 'Pilot clinical study to evaluate the anticoagulant activity of fucoidan', by Lowenthal et. al.PMID:19696660"))

;;  	     (class hypothesis (label "hypothesis textual entity")
;;  		    :partial !'textual entity'@
;; 		    (definition "A textual entity that expresses an assertion that is intended to be tested.")
;;  		    (signedalan)
;; 		    (4iao)
;; 		    (fcusecase)
;;  		    (example-of-usage "The hypothesis that fucoidan has a useful clinical effect as
;; an in-vivo anticoagulant and the test of that hypothesis is in the text of a 'Pilot clinical study to evaluate
;; the anticoagulant activity of fucoidan', by Lowenthal et. al.PMID:19696660"))

	     (individual statistical-test 
 	       (type !'statistical hypothesis test'@))

	     (individual statistical-test
 	       (type (manch (all !'has_specified_input'@
				 (some !'is_specified_output_of'@ fucoidan-at-iii-berichrome-assay))
			    ))

	       (label "Test of significance of Antithrombin-III level change between day 1 and day 4")
	       (definition-source "PMID:1969666#AT-III increased significantly from 113.5% at baseline to 117% after 4 days n = 10, P =  0.02; Table 2")
	       (signedalan)
	       (fcusecase)
	       (value !'has_specified_output'@ p-value)
	       )

	     (individual p-value
	       (type !'p-value'@)
	       (label "p-value of 0.02 from test of significance of Antithrombin-III level fucoidan study")
	       (definition-source "PMID:1969666#AT-III increased significantly from 113.5% at baseline to 117% after 4 days (n = 10, P =  0.02; Table 2)")
	       (signedalan)
	       (fcusecase)
	       (value !'has measurement value'@ (literal 0.02 !xsd:float))
	       )
	     )))))))
#|
	     From Call summary 7 Oct 09
	     1. We have now completed enough detail for the clinical trials use case to support the original aim - to show the breadth of OBI vs the glucose use case which shows depth

	     2. We discussed what we need to complete and in what granularity, remaining issues are
	     2.1 Statistical test - discussion on what are the inputs and we don't have enough info in the paper on what the test was. AR and JF will work offline to model what they think is going on
	     2.2 Hypothesis - we understand this to be 'fucoidan may have anticoagulant activity in vivo. result is a small stat sig effect of AT3 anticoagulant effect, author interpretation indicates no useful clinical effect.
	     2.3 Modelling the dosing regime - we decided to do this using day processes instances, with subparts, and these will be related using preceded by. AR will work on this. 
	     We understand that

	     we will not model time triggers now
	     we do not need instances for all patients, one representative for each group is enough
	     we will not work on this use case in Philly, but working on it later is desirable
	     |#
