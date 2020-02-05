(ns trkakonja.routes.ishod
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
            [ring.util.response :refer [redirect]]))

(def ishod-schema
  {:konjID [st/required st/number]
   :trkaID [st/required st/number]
   :mesto [st/required st/number]})

(defn ishod-validation? [params]
  (st/valid? {:konjID (read-string (:konjID params))
              :trkaID (read-string (:trkaID params))
              :mesto (:mesto params)} ishod-schema))
  
(defn get-trke []
    (db/get-trka))

(defn get-ishodi [text]
  (if (or (nil? text)
          (= "" text))
       (db/get-ishod)
       (db/search-ishod text)))

(defn get-ishod-page [page params]
  (render-file page
               {:title "Ishod trke"
                :trke (get-trke)
                :ishod (get-ishodi params)}))

(defn ishod [params]
    (get-ishod-page "views/ishod.html" params))

(defn ishod-tr [params]
    (get-ishod-page "views/ishod-tr.html" params))

(defn ishod-or [params]
    (get-ishod-page "views/ishod-or.html" params))

(defn add-ishod-to-db [ishod]
  (-> (db/add-ishod ishod)))

(defn get-konji [text]
    (db/search-konj text))


(defn get-add-ishod-page [session &[message]]
  (if-not (authenticated? session)
    (redirect "/tlogin")
    (render-file "views/ishod-add.html" {:title "Prijava za trku"
                                         :logged (:identity session)
                                         :trke (get-trke)
                                         :konji (get-konji (:trkacID :logged.trkacID))})))

(defn add-ishod [{:keys [params]}]
    (db/add-ishod params)
    (redirect "/addishod"))

(defn ishod-page-submit [{:keys [params session]}]
    (add-ishod params))

(defn get-ishod-edit-page [page params session]
  (println (get params :trkaID))
  (render-file page {:title "Unos ishoda trke"
                     :logged (:identity session)
                     :trkaID (get params :trkaID)
                     :ishodi (db/find-ishod-by-trka params)}))

(defn get-ishod [{:keys [params session]}]
  (if-not (authenticated? session)
    (redirect "/login")
    (get-ishod-edit-page "views/edit-ishod.html" params session)))


(defresource update-ishod [{:keys [params session]}]
  :allowed-methods [:put]  
  :available-media-types ["application/json"]
  (println params)
  (db/update-ishod params))

(defroutes ishod-routes
  (GET "/ishodi" request (ishod request))
  (GET "/ishodtr" request (ishod-tr request))
  (GET "/ishodor" request (ishod-or request))
  (GET "/getishod" request (get-ishodi request))
  (GET "/addishod" request (get-add-ishod-page (:session request)))
  (POST "/addishod" request (add-ishod request))
  (GET "/editishod" request (get-ishod request))
  (PUT "/ishod" request (update-ishod request)))
  
