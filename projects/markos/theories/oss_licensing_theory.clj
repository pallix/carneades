;;; Copyright (c) 2012 Fraunhofer Gesellschaft
;;; Licensed under the EUPL V.1.1

(ns eu.markosproject.licensing.oss-licensing-theory
  (:require  [carneades.engine.dublin-core :as dc]
             [carneades.engine.theory :as t]
             [carneades.engine.argument :as a]
             [carneades.engine.statement :as s]
             [carneades.owl.import :as owl]
             [carneades.project.fs :as project]
             [taoensso.timbre :as timbre :refer [debug error spy]]))

;; (def oss-licensing-ontology
;;   (try
;;     (owl/import-from-project "markos" "ontologies/MARKOS/markos-licenses.owl")
;;     (catch Exception e
;;       (error "Error loading markos-copyright.owl")
;;       (error "If you don't use the MARKOS project, ignore this.")
;;       (error "Error:" e)
;;       {})))

(def copyright-theory
  (project/load-theory "markos" "copyright_theory"))

(def oss-licensing-theory
  (t/make-theory
   :header
   (dc/make-metadata
    :title "Theory of Open Source Software Licensing"
    :creator "Tom Gordon"
    :description {:en "A theory of open source licensing for the MARKOS
project."})

   ;; :imports [copyright-theory]
   
   :namespaces
   { "oss"   "http://www.markosproject.eu/ontologies/oss-licenses#",
     "copyright" "http://www.markosproject.eu/ontologies/copyright#",
     "owl" "http://www.w3.org/2002/07/owl#",
     "rdf" "http://www.w3.org/1999/02/22-rdr-syntax-ns#"
     "rdfs" "http://www.w3.org/2000/01/rdf-schema#"
     "xsd" "http://www.w3.org/2001/XMLSchema#"
     "foaf" "http://xmlns.com/foaf/0.1/"
     "lic" "http://www.markosproject.eu/ontologies/licenses#"
     "soft" "http://www.markosproject.eu/ontologies/software#"
     "top" "http://www.markosproject.eu/ontologies/top#"
     "dc" "http://purl.org/dc/terms/"
     "ec" "http://www.markosproject.eu/ontologies/markos-event-calculus#"}

   :language
   (into (t/make-language
          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/oss-licenses#linked
           :forms {:en (t/make-form :positive "%s is linked to %s."
                                    :negative "%s is not linked to %s."
                                    :question "Is %s linked to %s?")})
          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/licenses#licenseTemplate
           :forms {:en (t/make-form :positive "%s is licensed using %s."
                                    :negative "%s is not licensed using %s."
                                    :question "Is %s licensed using %s?")})
          (t/make-concept
           :symbol 'http://www.markosproject.eu/ontologies/oss-licenses#ReciprocalLicenseTemplate
           :forms {:en (t/make-form :positive "%s is a reciprocal license."
                                    :negative "%s is not reciprocal license."
                                    :question "Is %s a reciprocal license?")})
          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/licenses#coveringLicense
           :forms {:en (t/make-form :positive "%s has a license: %s."
                                    :negative "%s does not have a license: %s."
                                    :question "Does %s have %s as its license?")})
          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/licenses#template
           :forms {:en (t/make-form :positive "The template license of %s is %s."
                                    :negative "The template license of %s is not %s."
                                    :question "Is the template license of %s the %s?")})
          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/top#containedEntity
           :forms {:en (t/make-form :positive "%s contains %s."
                                    :negative "%s does not contain %s."
                                    :question "Does %s contain %s?")})
          (t/make-concept
           :symbol 'http://www.markosproject.eu/ontologies/software#Library
           :forms {:en (t/make-form :positive "%s is a library."
                                    :negative "%s is not a library."
                                    :question "Is %s a library?")})
          (t/make-concept
           :symbol 'http://www.markosproject.eu/ontologies/software#SoftwareRelease
           :forms {:en (t/make-form :positive "%s is a software release."
                                    :negative "%s is not a software release."
                                    :question "Is %s a software release?")})
          (t/make-concept
           :symbol 'http://www.markosproject.eu/ontologies/licenses#CopyrightLicenseTemplate
           :forms {:en (t/make-form :positive "%s is a license."
                                    :negative "%s is not a license."
                                    :question "Is %s a license?")})
          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/copyright#compatibleWith
           :forms {:en (t/make-form :positive "%s is compatible with %s."
                                    :negative "%s is not compatible with %s."
                                    :question "Is %s compatible with %s?")})
          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/software#provenanceRelease
           :forms {:en (t/make-form :positive "The provenance release of %s is %s."
                                    :negative "The provenance release of %s is not %s."
                                    :question "Is the provenance release of %s %s?")})

          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/software#releasedSoftware
           :forms {:en (t/make-form :positive "The project %s has released %s."
                                    :negative "The project %s has not released %s."
                                    :question "Did the project %s release %s?")})

          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/copyright#derivedFrom
           :forms {:en (t/make-form :positive "%s is derived from %s."
                                    :negative "%s is not derived from %s."
                                    :question "Is %s derived from %s?")})

          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/software#usedSoftwareEntity
           :forms {:en (t/make-form :positive "%s uses %s."
                                    :negative "%s does not use %s."
                                    :question "Does %s use %s?")})

          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/oss-licenses#dynamicallyLinkedSoftwareRelease
           :forms {:en (t/make-form :positive "%s is dynamically linked to %s."
                                    :negative "%s is not dynamically linked to %s."
                                    :question "Is %s dynamically linked to  %s?")})

          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/software#dynamicallyLinkedEntity
           :forms {:en (t/make-form :positive "%s is dynamically linked to %s."
                                    :negative "%s is not dynamically linked to %s."
                                    :question "Is %s dynamically linked to  %s?")})

          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/oss-licenses#staticallyLinkedSoftwareRelase
           :forms {:en (t/make-form :positive "%s is statically linked to %s."
                                    :negative "%s is not statically linked to %s."
                                    :question "Is %s statically linked to  %s?")})

          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/software#implementingLanguage
           :forms {:en (t/make-form :positive "%s is implemented in the programming language %s."
                                    :negative "%s is not implemented in the programming language %s."
                                    :question "Is %s implemented in the programming langauge %s?")})

          (t/make-role
           :symbol 'http://www.markosproject.eu/ontologies/software#usedCompiler
           :forms {:en (t/make-form :positive "%s was compiled using %s."
                                    :negative "%s was not compiled using %s."
                                    :question "Was %s compiled using %s?")})

         (t/make-role
          :symbol 'http://www.markosproject.eu/ontologies/software#implementedAPIOfSoftwareRelease
          :forms {:en (t/make-form :positive "%s is an implementation of the %s API."
                                   :negative "%s is not an implementation of the %s API."
                                   :question "Is %s an implementation of the %s API?")})

         (t/make-role
          :symbol 'http://www.markosproject.eu/ontologies/software#previousVersion
          :forms {:en (t/make-form :positive "A previous version of  %s is %s."
                                   :negative "It is not the case that
                                   a previous version of %s is %s."
                                   :question "Is it the case that a
                                   previous version of %s is %s?")})

          ) ; end of make-language


         (:language copyright-theory))

   :sections
   [;; (t/make-section
    ;;  :id 'ontology-axioms
    ;;  :header (dc/make-metadata :title "Open Source Software Ontology axioms"
    ;;                            :description {:en ""})
    ;;  :schemes {};; (:axioms oss-licensing-ontology)
    ;;  )

    (t/make-section
     :id 'oss-license-analysis-rules
     :header (dc/make-metadata 
              :title "Open Source Software License Analysis Rules"
              :description {:en ""})

     :schemes
     [

      (t/make-scheme
       :id 'software-works
       :header (dc/make-metadata
                :title "Copyright protection of software releases"
                :description {:en "A software release is a work
                protected by copyright."})
       :conclusion '(copyright:ProtectedWork ?SE)
       :premises [(a/pm '(soft:SoftwareRelease ?SE))])

      (t/make-scheme
       :id 'use-by-derivation
       :header (dc/make-metadata
                :title "Use by derivation"
                :description {:en "One way to use a copyrighted work
                is to derive a new work from it."})
       :conclusion '(copyright:workUsed (derivation ?W1 ?W2) ?w2)
       :premises [(a/pm '(copyright:derivedFrom ?W1 ?W2))])

      (t/make-scheme
       :id 'software-release-license-template
       :header (dc/make-metadata
                :title "Software Release License Template"
                :description {:en "Presumably, the license template
                applied to a software release is the same as the
                project of the release."})
       :conclusion '(lic:licenseTemplate ?R ?T)
       :premises [(a/pm '(soft:releasedSoftware ?P ?R))
                  (a/pm '(lic:licenseTemplate ?P ?T))])
      
      (t/make-scheme
       :id 'default-licensing
       :weight 0.25
       :header (dc/make-metadata
                :title "Default licensing"
                :description {:en "Presumably, a work may be licensed
                   using any license template."})
       :conclusion '(copyright:mayBeLicensedUsing ?W ?T)
       :premises [(a/pm '(lic:CopyrightLicenseTemplate ?T))])

      (t/make-scheme
       :id 'strong-reciprocity
       :header (dc/make-metadata
                :title "Strong Reciprocity"
                :description {:en "A work W1 may not use a license
                template T1 if the work is derived from a work W2
                licensed using a reciprocal license template T2,
                unless T1 is compatible with T2."})
       :pro false
       :conclusion '(copyright:mayBeLicensedUsing ?W1 ?T1)
       :premises [(a/pm '(lic:CopyrightLicenseTemplate ?T1))
                  (a/pm '(copyright:derivedFrom ?W1 ?W2))
                  (a/pm '(lic:licenseTemplate ?W2 ?T2))
                  (a/pm '(oss:StrongReciprocalLicenseTemplate ?T2))]
       :exceptions [(a/pm '(copyright:compatibleWith ?T1 ?T2))])

      (t/make-scheme
       :id 'linking-exception-reciprocity
       :header (dc/make-metadata
                :title "LGPLv2.1 Reciprocity"
                :description {:en "A work W1 may not use a license
                template T1 if the work is derived from a work W2
                licensed using a weak recipricoal license template
                with a linking exeption by any means other than
                linking, unless T1 is compatible with T2."})
       :pro false
       :conclusion '(copyright:mayBeLicensedUsing ?W1 ?T1)
       :premises [(a/pm '(lic:CopyrightLicenseTemplate ?T1))
                  (a/pm '(oss:derivedFromOtherThanByLinking ?W1 ?W2))
                  (a/pm '(lic:licenseTemplate ?W2 ?L2))
                  (a/pm '(oss:LinkingExceptionReciprocalLicenseTemplate ?L2))]
       :exceptions [(a/pm '(copyright:compatibleWith ?T1 ?T2))])

      (t/make-scheme
       :id 'epl-reciprocity1
       :header (dc/make-metadata
                :title "EPL-1.0 Reciprocity 1"
                :description {:en "A work W1 may not use a license
                template T1 if the work is derived from a work W2
                licensed using the EPL-1.0 license template by any
                means other than linking, unless T1 is compatible with
                the EPL-1.0 license template. This rule does not
                apply if linking creates a derivative work, as a
                matter of law."})
       :pro false
       :conclusion '(copyright:mayBeLicensedUsing ?W1 ?T1)
       :premises [(a/pm '(lic:CopyrightLicenseTemplate ?T1))
                  (a/pm '(oss:derivedFromOtherThanByLinking ?W1 ?W2))
                  (a/pm '(lic:licenseTemplate ?W2 EPL-1.0))]
       :exceptions [(a/pm '(copyright:compatibleWith ?T1 EPL-1.0))
                    (a/pm '(valid derivation-by-linking))])

      ;; To do: check how to properly namespace "valid"

      (t/make-scheme
       :id 'epl-reciprocity2
       :header (dc/make-metadata
                :title "EPL-1.0 Reciprocity 2"
                :description {:en "A work W1 may not use a license
                template T1 if the work is derived from a work W2
                licensed using the EPL-1.0, by any means, unless T1 is
                compatible with the EPL-1.0 license template. This
                rule applies only if linking creates a derivative
                work, as a matter of law."})
       :pro false
       :conclusion '(copyright:mayBeLicensedUsing ?W1 ?T1)
       :premises [(a/pm '(lic:CopyrightLicenseTemplate ?T1))
                  (a/pm '(copyright:derivedFrom ?W1 ?W2))
                  (a/pm '(lic:licenseTemplate ?W2 EPL-1.0))]
       :exceptions [(a/pm '(copyright:compatibleWith ?T1 EPL-1.0))
                    (a/make-premise :positive false
                                    :statement
                                    (s/make-statement :atom '(valid derivation-by-linking)))])

      ;; To do: check how to properly namespace "valid" 

      (t/make-scheme
       :id 'derived-from-other-than-by-linking1
       :header (dc/make-metadata
                :title "Relation between linking and implementing an API"
                :description {:en "Implementing an API creates a
                derivative work of an API, but need not involve linking to
                the object code of interface definitions."})
       :pro true
       :conclusion '(oss:derivedFromOtherThanByLinking ?R1 ?R2)
       :premises [(a/pm '(oss:implementedAPIOfSoftwareRelease ?R1 ?R2))])

      
      (t/make-scheme
       :id 'derived-from-other-than-by-linking2
       :header (dc/make-metadata
                :title "Relationship between linking and modification"
                :description {:en "Modifying source code creates a
                derivative work of the source code, but does not
                necessarily require linking to the binary code from
                the original version."})
       :pro true
       :conclusion '(oss:derivedFromOtherThanByLinking ?R1 ?R2)
       :premises [ (a/pm '(oss:modificationOf ?R1 ?R2))])

      (t/make-scheme
       :id 'entity-reciprocity
       :header (dc/make-metadata
                :title "Strong Reciprocity"
                :description {:en "A software entity E1 may not use a
                license template T1 if its provenance release W1 is
                derived from a work W2 licensed using a strong
                reciprocal license template T2, unless T1 is
                compatible with T2."})
       :pro false
       :conclusion '(copyright:mayBeLicensedUsing ?E1 ?T1)
       :premises [(a/pm '(soft:SoftwareEntity ?E1))
                  (a/pm '(soft:provenanceRelease ?E1 ?W1))
                  (a/pm '(copyright:derivedFrom ?W1 ?W2))
                  (a/pm '(lic:licenseTemplate ?W2 ?T2))
                  (a/pm '(oss:ReciprocalLicenseTemplate ?T2))]
       :exceptions [(a/pm '(copyright:compatibleWith ?T1 ?T2))])

      (t/make-scheme
       :id 'modifications-only-reciprocity
       :header (dc/make-metadata
                :title "Modifications Only Reciprocity"
                :description {:en "A work W1 may not use a license
                template T1 if the work is derived via modifications
                of one or more files from a work W2 and W2 is licensed
                using a weak recipricoal license template, T2,
                requiring reciprocity for file modifications, unless
                T1 is compatible with T2."})
       :pro false
       :conclusion '(copyright:mayBeLicensedUsing ?W1 ?T1)
       :premises [(a/pm '(lic:CopyrightLicenseTemplate ?T1))
                  (a/pm '(oss:modificationOfSoftwareRelease ?W1 ?W2))
                  (a/pm '(lic:licenseTemplate ?W2 ?T2))
                  (a/pm '(oss:ModificationsOnlyReciprocalLicenseTemplate ?T2))]
       :exceptions [(a/pm '(copyright:compatibleWith ?T1 ?T2))])

      (t/make-scheme
       :id 'compatible-reflexive
       :header (dc/make-metadata
                :title "Reflexivity of compatible"
                :description 
                {:en "A license template is compatible with itself."})
       :conclusion '(copyright:compatibleWith ?T1 ?T1)
       :premises [(a/pm '(lic:CopyrightLicenseTemplate ?T1))])

      (t/make-scheme
       :id 'gpl3-compatibility-with-gpl2
       :header (dc/make-metadata
                :title "Compatiblity of the GPLv3 and GPLv2 License Templates"
                :description {:en "The GPL-3.0 license template is
                presumably compatible with the earlier GPL-2.0
                version. This presumption does not hold if the
                copyright owner of the software published using the
                GPL-2.0 license template did not include the option to
                apply later versions of the GPL in the copyright
                notice attached to the software. The copyright notice
                recommended by the Free Software Foundation includes
                this option.  Note: The presumption can be overridden
                manually using an undercutting argument. It cannot be
                formulated as an exception here, because the copyright
                notice is a property of the instance of the license
                template. To handle this exception automatically, the
                compatibleWith relation would need to be modified to
                compare license instances, rather than licence
                templates. We have decided not to do this, since
                reasoning about license compatibility issues at the
                level of instances would increase the complexity of
                the model and analysis by an amount that does not
                seem justified by the amount this exception is
                relevant in practice."})
       :conclusion '(copyright:compatibleWith GPL-3.0 GPL-2.0)
       :premises [])

      (t/make-scheme
       :id 'gpl3-compatibility-with-gpl-le-2
       :header (dc/make-metadata
                :title "Compatiblity of the GPL-3.0 and GPL-LE-2.0 License Templates"
                :description {:en "The GPL-3.0 license template is
                presumably compatible with the earlier GPL-LE-2.0
                version. This presumption does not hold if the
                copyright owner of the software published using the
                GPL-LE-v2 license template did not include the option
                to apply later versions of the GPL in the copyright
                notice attached to the software. The copyright notice
                recommended by the Free Software Foundation includes
                this option. See the gpl3-compatibility-with-gpl2
                rule for further information."})
       :conclusion '(copyright:compatibleWith GPL-3.0 GPL-LE-2.0)
       :premises [])


      (t/make-scheme
       :id 'derivation-by-linking
       :header (dc/make-metadata
                :title "Linking Creates a Derivative Work"
                :description {:en "Linking creates a derivative work."})
       :conclusion '(copyright:derivedFrom ?R1 ?R2)
       :premises [ (a/pm '(oss:linkedSoftwareRelease ?R1 ?R2)) ])

      (t/make-scheme
       :id 'static-linking
       :header (dc/make-metadata
                :title "Static Linking"
                :description {:en "Static linking is a form of linking."})
       :conclusion '(copyright:linkedSoftwareRelease ?R1 ?R2)
       :premises [(a/pm '(oss:staticallyLinkedSoftwareRelease ?R1 ?R2)) ])

      (t/make-scheme
       :id 'dynamic-linking
       :header (dc/make-metadata
                :title "Dynamic Linking"
                :description {:en "Dynamic linking is a form of linking."})
       :conclusion '(copyright:linkedSoftwareRelease ?R1 ?R2)
       :premises [(a/pm '(oss:dynamicallyLinkedSoftwareRelease ?R1 ?R2)) ])

      (t/make-scheme
       :id 'oracle-v-google
       :header (dc/make-metadata
                :title "Derivation by Implementing an API"
                :description {:en "See Oracle America, Inc. v. Google,
                Inc., United States Court of Appeals for the Federal
                Circuit, 2013-1021, -1022, May 9, 2014"})
       :conclusion '(copyright:derivedFrom ?R1 ?R2)
       :premises [(a/pm '(oss:implementedAPIOfSoftwareRelease ?R1 ?R2))])

    (t/make-scheme
     :id 'derivation-by-modification
     :header (dc/make-metadata
              :title "Derivation by Modification"
              :description {:en "A work created by modifying a work is
              derived from it. "})
     :conclusion '(copyright:derivedFrom ?R1 ?R2)
     :premises [ (a/pm '(oss:modificationOfSoftwareRelease ?R1 ?R2)) ]) 

    (t/make-scheme
       :id 'modification-by-forking
       :header (dc/make-metadata
                :title "Modification by Forking"
                :description {:en "A fork of a software release presumably
                includes modifications of the original version."})
       :conclusion '(oss:modificationOfSoftwareRelease ?R1 ?R2)
       :premises [(a/pm '(soft:softwareFork ?R2 ?R1))])

    ])]))
