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

(def matrix-sheet-cell-data
   (->> workbook
     (spr/select-sheet "Matrix")
     spr/row-seq
     (remove nil?)
     (map spr/cell-seq)
     (map #(map spr/read-cell %))))

(take 6  matrix-sheet-cell-data)

(def matrix-sheet-row-data
   (->> workbook
     (spr/select-sheet "Matrix")
     spr/row-seq
     (map spr/row-vec)))

(first matrix-sheet-row-data)

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
