(ns my-app.core
  (:require [datascript.core :as d]
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
