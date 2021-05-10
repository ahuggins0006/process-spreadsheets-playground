(ns spreadsheet-ripper.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [dk.ative.docjure.spreadsheet :as spr])
  (:gen-class))

(defn get-workbook [file-path] (spr/load-workbook file-path))

(def workbook (get-workbook "resources/sample.xlsx"))

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

(def col-FP (spr/select-columns {:K :FP} (spr/select-sheet "Matrix" workbook)))

(def indexed-col-values (map-indexed vector col-FP))

(def only-A (filter #(= {:FP "A"} (second  %)) indexed-col-values))

(nth matrix-sheet-cell-data 8)

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
