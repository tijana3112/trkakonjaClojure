(defproject trkakonja "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.5.2"]
                 [conman "0.8.3"]
                 [funcool/struct "1.3.0"]
                 [metosin/ring-http-response "0.9.1"]
                 [selmer "1.10.7"]
                 [ring-server "0.4.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/clojurescript "0.0-3165"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [liberator "0.10.0"]
                 [ring/ring-json "0.4.0"]
                 [migratus "0.8.28"]
                 [korma/korma "0.4.3"]
                 [com.cerner/clara-rules "0.14.0"]
                 [buddy/buddy-auth "1.4.1"]
                 [bcrypt-clj "0.3.3"]
                 [funcool/struct "1.0.0"]
                 [ring-webjars "0.2.0"]
                 [mount "0.1.15"]
                 [org.webjars/bootstrap "4.2.1"]
                 [org.webjars/font-awesome "5.6.3"]
                 [org.webjars/jquery "3.3.1-1"]
                 [lib-noir "0.9.9"]
                 [yesql "0.5.3"]
                 [com.fzakaria/slf4j-timbre "0.3.12"]
                 [yogthos/config "1.1.1"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.1"]
                 [http-kit "2.2.0"]] 
  :require [config.core :refer [env]]  
  :jvm-opts ["-Dconf=dev-config.edn"]  
  :plugins [[lein-ring "0.8.12"]
            [migratus-lein "0.4.1"]]  
  :migratus {:store         :database
             :migration-dir "migrations"
             :db            (clojure.edn/read-string (slurp "migratus-config.edn"))}

  :ring {:handler trkakonja.core/app
         :init trkakonja.core/init
         :destroy trkakonja.core/destroy}  
  :profiles
  {:production
			  {:ring
			   {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]
                        [figwheel "0.2.5"]
												[com.cemerick/piggieback "0.2.0"]
												[org.clojure/tools.nrepl "0.2.10"]
												[weasel "0.6.0"]
                       ]}})
