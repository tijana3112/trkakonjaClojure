(ns trkakonja.routes.trka
  (:require [compojure.core :refer :all]
            [selmer.parser :refer [render-file]]
            [trkakonja.models.baza :as db]
            [compojure.response :refer [render]]
            [buddy.auth :refer [authenticated?]]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [struct.core :as st]
            [clojure.java.io :as io]
            [liberator.representation :refer [ring-response as-response]]
            [clojure.set :refer [rename-keys]]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [ring.util.response :refer [redirect]]))

(def file-config (edn/read-string (slurp "file-config.edn")))

(defn create-file-name [{:keys [fname content-type]}]
  (str (:short-img-location file-config) fname "." (last (str/split content-type #"/"))))

(defn get-picture-url [params]
  (if (contains? params :url)
    (:url params)
    (->(assoc (:file params) :fname (:name params))
       (create-file-name))))

(def trka-schema
  {:tip [st/required st/string]
   :naziv [st/required st/string]
   :grad [st/required st/string]
   :organizatorID [st/required st/number]})

(defn trka-validation? [params]
  (st/valid? {:tip (:tip params)
              :naziv (:naziv params)
              :grad (:grad params)
              :organizatorID (:organizatorID params)} trka-schema))

(defn get-trke-page [page session]
  (render-file page
               {:title "Pregled trka"
                :logged (:identity session)
                :izlozbe (db/get-trka)}))


(defn get-add-trka-page [session &[message]]
  (if-not (authenticated? session)
    (redirect "/login")
    (render-file "views/trka-add.html" {:title "Dodaj trku"
                                            :logged (:identity session)})))

(defn add-trka [{:keys [params session]}]
    (println params)
    (trka-validation? params)
    (db/add-trka params)
    (redirect "/userForma"))

(defn get-trke [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-trka)
    (db/search-trka text)))

(defn get-search-trke [params session]
  (if-not (authenticated? session)
    (redirect "/tlogin")
  (render-file "views/trka-search.html" {:title "Pretraga trka"
                                      :logged (:identity session)
                                      :trke (get-trke nil)})))
(defn get-search-trke-or [params session]
  (if-not (authenticated? session)
    (redirect "/login")
  (render-file "views/trka-search-or.html" {:title "Pretraga trka"
                                      :logged (:identity session)
                                      :trke (get-trke nil)})))

(defresource search-trka [{:keys [params session]}]
  :allowed-methods [:post]
  :authenticated? (authenticated? session)
  :handle-created (json/write-str (get-trke (:text params)))
  :available-media-types ["application/json"])

(defresource search-trka [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                (condp = media-type
                  "text/html" (get-search-trke params session)
                  "application/json" (->(:text params)
                                        (get-trke)
                                        (json/write-str)))))
(defresource search-trka-or [{:keys [params session]}]
  :allowed-methods [:post]
  :authenticated? (authenticated? session)
  :handle-created (json/write-str (get-trke (:text params)))
  :available-media-types ["application/json"])

(defresource search-trka-or [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                (condp = media-type
                  "text/html" (get-search-trke-or params session)
                  "application/json" (->(:text params)
                                        (get-trke)
                                        (json/write-str)))))

(defn get-trka-edit-page [page params session]
  (render-file page {:title "Trka"
                     :logged (:identity session)
                     :trka (first (db/find-trka params))}))

(defn get-trka [{:keys [params session]}]
   (if-not (authenticated? session)
    (redirect "/login")
    (get-trka-edit-page "views/edit-trka.html" params session)))

(defresource update-trka [{:keys [params session]}]
  :allowed-methods [:put]  
  :available-media-types ["application/json"]
  (println params)
  (db/update-trka params))

(defresource delete-trka [{:keys [params session]}]
  :allowed-methods [:delete]  
  :available-media-types ["application/json"])

(defroutes trka-routes
  (GET "/addtrka" request (get-add-trka-page (:session request)))
  (POST "/addtrka" request (add-trka request))
  (GET "/pretragatrka" request (search-trka request))
  (GET "/pretragatrkaor" request (search-trka-or request))
  (GET "/trka/:trkaID" request (get-trka request))
  (PUT "/trka" request (update-trka request))
  (DELETE "/trka" request (delete-trka request)))