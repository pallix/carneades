;;; Copyright (c) 2011 Fraunhofer Gesellschaft 
;;; Licensed under the EUPL V.1.1


(ns carneades.engine.test-evaluation
  (:use clojure.test
        carneades.engine.statement
        carneades.engine.argument
        carneades.engine.scheme
        carneades.engine.argument-graph
        carneades.engine.argument-construction
        carneades.engine.argument-evaluation
        carneades.engine.caes
        carneades.engine.caes2
        carneades.engine.shell
        carneades.maps.lacij))

;; Example argument graphs to test whether arguments are being evaluated properly.
;; Henry Prakken suggested the following sources:

;; - Henry's 2010 paper in Argument and Computation
;; - Caminada & Amgoud's AIJ-2007 paper
;; - V. Lifschitz, "Benchmark problems for formal nonmonotonic
;;   reasoning," in Proceedings of the Second international Workshop
;;   on Non-monotonic Reasoning, 1989, pp. 202-219.

; Benchmark 1. The Tandem example
; Source:
; Baroni, P., Caminada, M., and Giacomin, M. An introduction to argumentation semantics. 
; The Knowledge Engineering Review 00, 0 (2004), 1-24.

(def jw (make-statement :text {:en "John wants to ride on the tandem."}))
(def mw (make-statement :text {:en "Mary wants to ride on the tandem."}))
(def sw (make-statement :text {:en "Suzy wants to ride on the tandem."}))
(def jt (make-statement :text {:en "John is riding on the tandem."}))
(def mt (make-statement :text {:en "Mary is riding on the tandem."}))
(def st (make-statement :text {:en "Suzy is riding on the tandem."}))
(def bottom (make-statement :text {:en "The claims are inconsistent."}))

(def A5 (make-argument :conclusion jt :premises [(pm jw)]))
(def A6 (make-argument :conclusion mt :premises [(pm mw)]))
(def A7 (make-argument :conclusion st :premises [(pm sw)]))
(def A8 (make-argument :strict true :conclusion (neg jt) :premises [(pm mt) (pm st)]))
(def A9 (make-argument :strict true :conclusion (neg mt) :premises [(pm jt) (pm st)]))
(def A10 (make-argument :strict true :conclusion (neg st) :premises [(pm mt) (pm jt)]))

(def tandem-graph 
  (-> (make-argument-graph)
      (enter-arguments [A5, A6, A7, A8, A9, A10])
      (assume [jt, mt, st])
      (accept [jw, mw, sw])))

(deftest test-tandem-carneades
   (is (= #{(:id jw) (:id mw) (:id sw)}
          (in-statements (evaluate caes2-evaluator tandem-graph)))))

; The following examples are from:
; Prakken, H. An abstract framework for argumentation with structured arguments. 
; Argument & Computation 1, (2010), 93-124.

; The bachelor example, ibid., page 9
; This example illustrates both the distinction between strict 
; and defeasible rules and the problem of handling one kind of cycle
; in argument graphs.

(def bachelor (make-statement :text {:en "Fred is a bachelor."}))
(def wears-ring (make-statement :text {:en "Fred wears a ring."}))
(def party-animal (make-statement :text {:en "Fred is a party animal."}))
(def married (make-statement :text {:en "Fred is married."}))
(def A1 (make-argument :id 'A1 :conclusion bachelor :premises [(pm party-animal)]))
(def A2 (make-argument :id 'A2 :conclusion married :premises [(pm wears-ring)]))
(def A3 (make-argument :id 'A3 :strict true :conclusion (neg married)  :premises [(pm bachelor)]))
(def A4 (make-argument :id 'A4 :strict true :conclusion (neg bachelor) :premises [(pm married)]))

(def bachelor-graph
  (-> (make-argument-graph)
      (enter-arguments [A2, A1, A4, A3])
      (assume [bachelor, married])  ; note: inconsistent assumptions
      (accept [wears-ring, party-animal])))

; The AIJ version of Carneades couldn't handle this example,
; because it couldn't handle cycles and didn't support strict arguments.
; Notice how Carneades handles this example differently than ASPIC+, since
; giving A2 more weight than A1 would not change the result.
; See pp 17-18 of ibid for a discussion of this issue.

(deftest test-bachelor-carneades
   (let [ag (evaluate caes2-evaluator bachelor-graph)]
      (is (and (undecided? ag (literal-atom bachelor))
               (undecided? ag (literal-atom married))))))

; The Frisian example, ibid., page 11

(def frisian (make-statement :text {:en "Wiebe is Frisian."}))
(def dutch (make-statement :text {:en "Wiebe is Dutch."}))
(def tall (make-statement :text {:en "Wiebe is Tall."}))

(def A5 (make-argument :strict true :conclusion dutch :premises [(pm frisian)]))
(def A6 (make-argument :conclusion tall :premises [(pm dutch)]))

; A5 is irrelevant in this example, if the premise of A6, that the person is Dutch,
; is assumed. The issue is whether all premises of arguments should be assumed true
; until questioned or attacked.  A5 provides a supporting argument, but unnecessarily
; since the premise hasn't been questioned.

(def frisian-graph 
  (-> (make-argument-graph)
      (enter-arguments [A5, A6])
      (assume [dutch])
      (accept [frisian])))

(deftest test-frisian-carneades
   (is (in? (evaluate caes2-evaluator frisian-graph) 
            (literal-atom tall))))

;; The next example shows how arguments can be constructed by instantiating schemes.
;; The scheme is instantiated manually and then used to construct arguments.
;; Schemes of this type, with a conclusion which is a scheme variable ranging
;; over arbitrary literals, cannot be used effectively with a backwards
;; chaining, goal-directed strategy, since the conclusion matches every goal
;; literal.  Thus, in this example, the scheme is first instantiated in a
;; data-driven, forwards-chaining manner, to construct a custom version
;; which can be used to construct arguments via backwards chaining.  In
;; this example the scheme is fully instantiated, but this method can
;; also be used to partially instantiate schemes, so long as the predicate
;; of the conclusion of the scheme is instantiated before trying to use
;; backwards chaining to construct arguments.

(def expert-witness-scheme
  (make-scheme 
    :name "Expert Witness Testimony"
    :conclusion '?P
    :premises [(make-premise :role "major" :statement '(expert ?E ?D)), 
               (make-premise :role "minor" :statement '(asserts ?E ?P))]
    :exceptions [(make-premise 
                   :role "reliable" 
                   :positive false
                   :statement '(reliable-as-source ?E)),
                 (make-premise 
                   :role "consistent" 
                   :statement '(not (consistent-with-other-witnesses ?P)))]
    :assumptions [(make-premise 
                    :role "credible"
                    :statement '(credible-expert ?E)),
                  (make-premise
                    :role "backup-evidence"
                    :statement '(based-on-evidence ?P))]))


(def expert-witness1
  (specialize-scheme 
    expert-witness-scheme
    {'?P '(has-cavities Susan)
     '?E 'Joe
     '?D 'dentistry}))            

(def max-goals 10)

(def generators 
  (list (generate-arguments-from-scheme expert-witness1)))

(def case1-facts 
  '((expert Joe dentistry)
    (asserts Joe (has-cavities Susan))))

(def query '(has-cavities Susan))

(def expert-witness-graph
  (construct-arguments query max-goals case1-facts generators))

(deftest test-expert-witness-carneades
   (is (in? (evaluate caes2-evaluator expert-witness-graph) 
            '(has-cavities Susan))))

; The library example, ibid., page 17

(def snores (make-statement :text {:en "The person is snoring in the library."}))
(def professor (make-statement :text {:en "The person is a professor."}))
(def misbehaves (make-statement :text {:en "The person is misbehaving."}))
(def access-denied (make-statement :text {:en "The person is denied access to the library."}))

(def A1 (make-argument :weight 0.5 :conclusion misbehaves :premises [(pm snores)]))
(def A2 (make-argument :weight 0.7 :conclusion access-denied :premises [(pm misbehaves)]))
(def A3 (make-argument :weight 0.6 :conclusion (neg access-denied) :premises [(pm professor)]))

(def library-graph 
  (-> (make-argument-graph)
      (enter-arguments [A1, A2, A3])
      (assume [misbehaves])
      (accept [snores, professor])))

; Carneades applies the "last link" principle to order arguments, as can
; be seen below.

(deftest test-library-graph
   (is (in? (evaluate carneades-evaluator library-graph) 
            (literal-atom access-denied))))

; Serial self defeat example, ibid., page 18

(def P  (make-statement :text {:en "Witness John says that he is unreliable."}))
(def Q  (make-statement :text {:en "Witness John is unreliable."}))

; The next argument is manually assigned an id, which can be used as 
; a constant term to refer to the argument in the undercutter, A3, below.

(def A7 (make-argument :id 'A7 :conclusion Q :premises [(pm P)]))

; The next argument illustrates how undercutters are now explicity 
; represented in Carneades.  

(def A8 (make-argument
          :id 'A8
          :conclusion '(undercut A7)
          :premises [(pm Q)]))

(def self-defeat-graph 
  (-> (make-argument-graph)
      (enter-arguments [A7,A8])
      (assume [Q])
      (accept [P])))
 
(deftest test-self-defeat
         (is (undecided? (evaluate caes2-evaluator self-defeat-graph) 
                         (literal-atom Q))))

;; TO DO: remaining examples in Henry's article, starting with the example
;; or parallel self-defeat on page 18.

;; The next example is from "Relating Carneades with abstract argumentation via the ASPIC+ framework for
;; structured argumentation", by Bas Gijzel and Henry Prakken.  It is the example they use to illustrate
;; the inability of Carneades to handle cycles

(def Italy (make-statement :text {:en "Let's go to Italy."}))
(def Greece (make-statement :text {:en "Let's go to Greece."}))

(def greece-arg
  (make-argument
   :id 'greece-arg
   :conclusion Greece))

;(def greece-undercutter
;  (make-argument
;   :id 'greece-undercutter
;   :conclusion '(undercut greece-arg)
;   :premises [(pm Italy)]))

(def greece-rebuttal
  (make-argument
    :id 'greece-rebuttal
    :strict true
    :conclusion (neg Greece)
    :premises [(pm Italy)]))

(def italy-arg
  (make-argument
   :id 'italy-arg
   :conclusion Italy))

;(def italy-undercutter
;  (make-argument
;   :id 'italy-undercutter
;   :conclusion '(undercut italy-arg)
;   :premises [(pm Greece)]))

(def italy-rebuttal
  (make-argument
    :id 'italy-rebuttal
    :strict true
    :conclusion (neg Italy)
    :premises [(pm Greece)]))

(def vacation-graph 
  (-> (make-argument-graph)
      (assume [Italy, Greece])
      (enter-arguments [greece-arg, greece-rebuttal, 
                        italy-arg, italy-rebuttal])))

(deftest test-vacation
  (let [g  (evaluate caes2-evaluator vacation-graph)]
    (and (is (undecided? g Italy))
         (is (undecided? g Greece)))))

;; Now let's see what happens after a decision has been made
;; to go to Italy.

(def vacation-graph2 
  (accept vacation-graph [Italy]))

(deftest test-vacation-2
  (let [g  (evaluate caes2-evaluator vacation-graph2)]
    (and (is (in? g Italy))
         (is (out? g Greece)))))

; (argument-graph-to-framework vacation-graph2)



