(ns my-app.core
  (:require [datascript.core :as d]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [dk.ative.docjure.spreadsheet :as spr]
            ))


;;; Create a DataScript "connection" (an atom with the current DB value)
(def conn (d/create-conn))
;; Define datoms to transact
(def datoms [{:db/id -1 :name "Bob" :age 30}
             {:db/id -2 :name "Sally" :age 15}])
;;; Add the datoms via transaction
(d/transact! conn datoms)
;; => #datascript.db.TxReport{:db-before #datascript/DB {:schema nil, :datoms [[1 :age 30 536870913] [1 :name "Bob" 536870913] [2 :age 15 536870913] [2 :name "Sally" 536870913]]}, :db-after #datascript/DB {:schema nil, :datoms [[1 :age 30 536870913] [1 :name "Bob" 536870913] [2 :age 15 536870913] [2 :name "Sally" 536870913] [3 :age 30 536870914] [3 :name "Bob" 536870914] [4 :age 15 536870914] [4 :name "Sally" 536870914]]}, :tx-data [#datascript/Datom [3 :name "Bob" 536870914 true] #datascript/Datom [3 :age 30 536870914 true] #datascript/Datom [4 :name "Sally" 536870914 true] #datascript/Datom [4 :age 15 536870914 true]], :tempids {-1 3, -2 4, :db/current-tx 536870914}, :tx-meta nil}
;;; Query to find names for entities (people) whose age is less than 18
(def q-young '[:find ?n
               :in $ ?min-age
               :where
               [?e :name ?n]
               [?e :age ?a]
               [(< ?a ?min-age)]])
;; execute query: q-young, passing 18 as min-age
(d/q q-young @conn 18)

;;create samples from far dataset

(def conn-far (d/create-conn))


;; get workbook data from spreadsheet
(defn get-workbook [file-path] (spr/load-workbook file-path))

(def workbook (get-workbook "resources/sample.xlsx"))


(def sheet
      (->> workbook
     (spr/select-sheet "Matrix")
     ))

(spr/read-cell (first (nth (spr/row-seq sheet) 4)))

;; combine col letter with name
(defn to-col [num]
  (loop [n num s ()]
    (if (> n 25)
      (let [r (mod n 26)]
        (recur (dec (/ (- n r) 26)) (cons (char (+ 65 r)) s)))
      (keyword (apply str (cons (char (+ 65 n)) s))))))


;; use clojure built-in csv reader
(def matrix-data (with-open [reader (io/reader "resources/sample - Matrix.csv")]
(doall
 (csv/read-csv reader))))

(nth matrix-data 4)

;; parse data into maps
(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map #(clojure.string/replace % #"\n" ""))
            (map #(clojure.string/replace % #":" ""))
            (map #(clojure.string/replace % #" " "_"))
            (map keyword) ;; Drop if you want string keys instead
            repeat)
	     (rest csv-data)))

(def row-1 (first (csv-data->maps (drop 4 matrix-data))))

(def matrix-datoms  [(assoc row-1 :db/id 1)])


(d/transact! conn matrix-datoms)

;;build a sample query
(def q-ibr '[
:find [?f ?n ?h]
:where
             [?e :CR ?n]
             [?e :T&MLH ?h]
[?e :FP ?f]])

(d/q q-ibr @conn)

(def q-attr '[
             :find ?e
             :where
             [?e :FP]
             ])

;;return all attrs associated with query
(def eid (d/q q-attr @conn))

(def ent (d/entity @conn (ffirst eid)))

(seq ent)
