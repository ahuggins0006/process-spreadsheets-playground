(ns spreadsheet-ripper.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :as pprint]
            [table.core :as t]
            [dk.ative.docjure.spreadsheet :as spr])
  (:gen-class))

;; TODO model data as contracts with attributes
;; TODO user wants to be able form queries about contracts based on their attributes
;; Import data into a database such as dataomic that uses a query language like datalog
;; What is datalog? https://www.youtube.com/watch?v=aI0zVzzoK_E


(defn get-workbook [file-path] (spr/load-workbook file-path))

(def workbook (get-workbook "resources/sample.xlsx"))

(def workbook2 (get-workbook "resources/agu_messages.xlsx"))

(filter #(str/includes? % "write" ) (->> workbook2
     spr/sheet-seq
     (map spr/sheet-name)
     ))


(def sheet-names (->> workbook
                      spr/sheet-seq
                      (map spr/sheet-name)
                      ))

sheet-names;; => ("Matrix" "COVID-19" "Acronym Key" "About" "Change Summary" "CSI-UCF")

(def matrix-sheet-row-count (->> workbook
                    (spr/select-sheet "Matrix")
                    spr/row-seq
                    (remove nil?)
                    count))

matrix-sheet-row-count;; => 8761


(defn get-row-count [workbook sheet-name]
  (->> workbook
       (spr/select-sheet sheet-name)
       spr/row-seq
       (remove nil?)
       count)
  )


;; get data out of Matrix sheet

(defn combine-ref-with-value [cell]
  [(spr/cell-reference cell) (spr/read-cell cell)])

(def matrix-sheet-cell-data
   (->> workbook
     (spr/select-sheet "Matrix")
     spr/row-seq
     (remove nil?)
     (map spr/cell-seq)
     (map #(map combine-ref-with-value  %))))

(nth matrix-sheet-cell-data 6)

(def col-FP (spr/select-columns {:C :title} (spr/select-sheet "Matrix" workbook)))

(def indexed-col-values (map-indexed vector col-FP))

(def find-target (filter #(= {"title" "Covenant Against Contingent Fees"} (second  %)) indexed-col-values))

(def only-A (filter #(= {:FP "A"} (second  %)) indexed-col-values))

(nth indexed-col-values 9)
;; => [9 {"title" "Covenant Against Contingent Fees"}]

(first indexed-col-values)


(nth matrix-sheet-cell-data 8)

;; User requests that certain columns be hidden/removed

;; select columns

(def sample-data (spr/select-columns {:K :FP :L :CR} (spr/select-sheet "Matrix" workbook)))
(def sample-3 (take 3 sample-data))
;; => ({:FP nil, :CR nil} {:FP nil, :CR nil} {:FP "CONTRACT \nTYPE", :CR nil})

;; remove empty rows
(def sample-3-no-nil (map #(into {} (filter (comp some? val) %)) sample-3))

sample-3-no-nil
;; remove empty maps
(def no-empty (remove empty? sample-3-no-nil))


(pprint/print-table no-empty)

(t/table no-empty :stype :unicode)








(keys (ns-publics 'dk.ative.docjure.spreadsheet))
;; => (select-columns
;;     load-workbook-from-resource
;;     apply-date-format!
;;     border
;;     sheet-seq
;;     assert-type
;;     create-xls-workbook
;;     load-workbook-from-stream
;;     remove-row!
;;     horiz-align
;;     load-workbook-from-file
;;     select-cell
;;     get-font
;;     IFontable
;;     add-rows!
;;     cell-reference
;;     create-cell-style!
;;     add-name!
;;     row-seq
;;     set-cell-comment!
;;     set-row-styles!
;;     cell-seq
;;     save-workbook-into-file!
;;     set-font
;;     color-index
;;     save-workbook-into-stream!
;;     load-workbook
;;     save-workbook!
;;     set-row-style!
;;     as-font
;;     create-workbook
;;     string-cell?
;;     get-row-styles
;;     row-vec
;;     sheet-name
;;     whens
;;     create-sparse-workbook
;;     set-cell!
;;     into-seq
;;     escape-cell
;;     read-cell
;;     remove-all-rows!
;;     select-sheet
;;     select-name
;;     read-cell-value
;;     set-cell-style!
;;     add-row!
;;     cell-fn
;;     create-font!
;;     add-sheet!
;;     vert-align)

#_(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
