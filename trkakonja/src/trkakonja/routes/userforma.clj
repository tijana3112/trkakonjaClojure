(ns trkakonja.routes.userforma
  (:require [compojure.core :refer :all]
            [selmer.parser :refer [render-file]]
            [compojure.response :refer [render]]
            [buddy.auth :refer [authenticated?]]
            [trkakonja.models.baza :as db]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]
            [compojure.response :refer [render]]
            [buddy.auth :refer [authenticated?]]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [liberator.representation :refer [ring-response as-response]]
            [clojure.set :refer [rename-keys]]
            [clojure.string :as str]))

(def trkac-schema
  {:imePrezime [st/required st/string]
   :grad [st/required st/string]
   :username [st/required st/string]
   :password [st/required st/string]})

(defn trkac-validation? [params]
  (st/valid? {:imePrezime (:imePrezime params)
              :grad (:grad params)
              :username (:username params)
              :password (:password params)} trkac-schema))


(defn get-userforma-page [page session]
  (render-file page
               {:title "Glavna forma organizatora"
                :logged (:identity session)}))

(defn userforma [session]
  (cond
    (not (authenticated? session))
    (redirect "/login")
    :else
    (get-userforma-page "views/userForma.html" session)))


(defn get-trkacforma-page [page session]
  (render-file page
               {:title "Glavna forma trkaca"
                :logged (:identity session)}))

(defn trkacforma [session]
  (cond
    (not (authenticated? session))
    (redirect "/tlogin")
    :else
    (get-trkacforma-page "views/trkacForma.html" session)))

(defn get-trkaci [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-trkac)
    (db/search-trkac text)))

(defn get-search-trkaci [params session]
  (render-file "views/trkaci.html" {:title "Pretraga trkaca"
                                              :logged (:identity session)
                                              :trkaci (get-trkaci nil)}))

(defresource search-trkac [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                (condp = media-type
                  "text/html" (get-search-trkaci params session)
                  "application/json" (->(:text params)
                                        (get-trkaci)
                                        (json/write-str)))))

(defresource delete-trkac [{:keys [params session]}]
  :allowed-methods [:delete] 
  (println params)
  (db/delete-trkac (:trkacID params))
  :available-media-types ["application/json"])

(defroutes forme-routes
  (GET "/userForma" request (userforma (:session request)))
  (POST "/userForma" request (userforma request))
  (GET "/trkacForma" request (trkacforma (:session request)))
  (POST "/trkacForma" request (trkacforma request))
  (GET "/trkaci" request (search-trkac request))
  (DELETE "/trkac/:trkacID" request (delete-trkac request)))