(ns trkakonja.routes.home  
  (:require [compojure.core :refer :all]
            [trkakonja.models.baza :as db]
            [selmer.parser :refer [render-file]]
            [compojure.response :refer [render]]
            [selmer.parser :refer [render-file]]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [struct.core :as st]
            [clojure.java.io :as io]
            [liberator.representation :refer [ring-response as-response]]
            [clojure.set :refer [rename-keys]]
            [clojure.string :as str]
            [ring.util.response :refer [redirect]]))

(def konj-schema
  {:ime [st/required st/string]
   :boja [st/required st/string]
   :rasa [st/required st/string]
   :sampion [st/required st/string]
	 :slika [st/required st/string]
   :trkacID [st/required st/number]
})

(defn konj-validation? [params]
  (st/valid? {:ime (:ime params)
              :boja (:boja params)
              :rasa (:rasa params)
              :sampion (:sampion params)
              :slika (:slika params)
              :trkacID (read-string (:trkacID params))} konj-schema))

(defn get-home-page [page session]
  (render-file page
               {:title "Home"
                :trkaci (count (db/get-trkac))
                :konji (count (db/get-konj))
                :trke (count (db/get-trka))}))

(defn home-page [session]
  (get-home-page "views/home.html" session))

(defn send-submit [{:keys [params session]}]
  
  (get-home-page "views/home.html" session))

(defn get-konji [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-konj)
    (db/search-konj text)))

(defn get-search-konji [params session]
    (render-file "views/search-konj-home.html" {:title "Home page"
	                                               :konji (get-konji nil)}))

(defresource search-konj-home [{:keys [params session]}]
  :allowed-methods [:post]
  :handle-created (json/write-str (get-konji (:text params)))
  :available-media-types ["application/json"])

(defresource search-konj-home [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                (condp = media-type
                  "text/html" (get-search-konji params session)
                  "application/json" (->(:text params)
                                        (get-konji)
                                        (json/write-str)))))
(defn get-trke [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-trka)
    (db/search-trka text)))

(defn get-search-trke [params session]
  (render-file "views/search-trke-home.html" {:title "Home page"
                                               :trke (get-trke nil)}))

(defresource search-trke-home [{:keys [params session]}]
  :allowed-methods [:post]
  :handle-created (json/write-str (get-trke (:text params)))
  :available-media-types ["application/json"])

(defresource search-trke-home [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                (condp = media-type
                  "text/html" (get-search-trke params session)
                  "application/json" (->(:text params)
                                        (get-trke)
                                        (json/write-str)))))


(defroutes home-routes
           (GET "/" request (home-page (:session request)))
           (POST "/" request (send-submit request))
           (GET "/pretragahome" request (search-konj-home request))
           (GET "/pretragatrkahome" request (search-trke-home request)))





