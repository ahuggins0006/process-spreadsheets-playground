(defproject spreadsheet-ripper "0.1.0-SNAPSHOT"
  :description "processing a spreadsheet for a project"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [dk.ative/docjure "1.14.0"]
                 [table "0.5.0"]
                 ]
  :main ^:skip-aot spreadsheet-ripper.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
