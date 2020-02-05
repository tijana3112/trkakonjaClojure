(ns trkakonja.routes.loginregistration  
  (:require [compojure.core :refer :all]
            [bcrypt-clj.auth :as bcrypt]
            [struct.core :as st]
            [ring.util.response :refer [redirect]]
            [selmer.parser :refer [render-file]]
            [trkakonja.models.baza :as db]
            [compojure.response :refer [render]]
            [buddy.auth :refer [authenticated?]]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [liberator.representation :refer [ring-response as-response]]
            [clojure.set :refer [rename-keys]]
            [clojure.string :as str]))

(def login-schema
  {:username [st/required st/string]
   :password [st/required st/string]})

(defn login-validation? [params]
  (st/valid? {:username (:username params)
              :password (:password params)} login-schema))

(defn get-login-page [&[error]]
  (render-file "views/login.html" {:title "Prijava organizatora"
                                   :error error}))

(defn get-trkac-login-page [&[error]]
  (render-file "views/tlogin.html" {:title "Prijava trkaca"
                                 :error error}))

(defn get-organizator-by-username-from-db [params]
  (-> (select-keys params [:username])
      (db/find-organizator)
      (first)))

(defn get-trkac-by-username-from-db [params]
  (-> (select-keys params [:username])
      (db/find-trkac)
      (first)))


(defn login-page-submit [{:keys [params session]}]
  (let [organizator (get-organizator-by-username-from-db params)]
    (cond
      (not (login-validation? params))
      (get-login-page "Unesite svoj username i password")
      (empty? organizator)
      (get-login-page "Unesite kredencijale")
      :else
      (assoc (redirect "/userForma"):session (assoc session :identity organizator)))))

(defn trkac-login-page-submit [{:keys [params session]}]
  (let [trkac (get-trkac-by-username-from-db params)]
    (cond
      (not (login-validation? params))
      (get-trkac-login-page "Unesite svoj username i password")
      :else
      (assoc (redirect "/trkacForma"):session (assoc session :identity trkac)))))

(defn logout [request]
  (-> (redirect "/")
      (assoc :session {})))
(defn trkac-logout [request]
  (-> (redirect "/")
      (assoc :session {})))

(def register-schema
  [[:imePrezime st/required st/string]
   [:username st/required st/string]
   [:password st/required st/string]])

(defn register-validaton? [params]
  (st/valid? {:imePrezime (:imePrezime params)
              :username (:username params)
              :password (:password params)
              } register-schema))

(defn get-registration-page [&[error]]
  (render-file "views/registration.html" {:title "Registrovanje"
                                          :error error}))
(defn add-organizator-to-db [params]
  (-> (db/add-organizator params)))

(defn registration-page-submit [{:keys [params session]}]	
  (let [organizator (get-organizator-by-username-from-db params)]
    (cond
      (not (register-validaton? params))
      (get-registration-page "Potrebno je popuniti sva polja!")
      (not-empty organizator)
      (get-registration-page (str "Uneti kredencijali vec postoje!"))
      :else
      (assoc (redirect "/login"):session (assoc session :identity (add-organizator-to-db params))))))

(def tregister-schema
  [[:imePrezime st/required st/string]
   [:grad st/required st/string]
   [:username st/required st/string]
   [:password st/required st/string]])

(defn tregister-validaton? [params]
  (st/valid? {:imePrezime (:imePrezime params)
              :grad (:grad params)
              :username (:username params)
              :password (:password params)} tregister-schema))

(defn get-tregistration-page [&[error]]
  (render-file "views/tregistration.html" {:title "Registrovanje trkaca"
                                         :error error}))
(defn add-trkac-to-db [params]
  (-> (db/add-trkac params)))

(defn tregistration-page-submit [{:keys [params session]}]
     (assoc (redirect "/tlogin"):session (assoc session :identity (add-trkac-to-db params))))

(defn get-organizatori [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-organizator)
    (db/search-organizator text)))
(defn get-search-organizatori [params session]
  (render-file "views/organizatori.html" {:title "Pretraga organizatora"
                                                    :logged (:identity session)
                                                    :organizatori (get-organizatori nil)}))

(defresource search-organizator [{:keys [params session]}]
  :allowed-methods [:post]
  :handle-created (json/write-str (get-organizatori (:text params)))
  :available-media-types ["application/json"])

(defresource search-organizator [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                (condp = media-type
                  "text/html" (get-search-organizatori params session)
                  "application/json" (->(:text params)
                                        (get-organizatori)
                                        (json/write-str)))))


(defroutes log-routes
           (GET "/login" [] (get-login-page))
           (POST "/login" request (login-page-submit request))
           (GET "/logout" request (logout request))
           (GET "/registration" [] (get-registration-page))
           (POST "/registration" request (registration-page-submit request))
           (GET "/tlogin" [] (get-trkac-login-page))
           (POST "/tlogin" request (trkac-login-page-submit request))
           (GET "/tlogout" request (trkac-logout request))
           (GET "/tregistration" [] (get-tregistration-page))
           (POST "/tregistration" request (tregistration-page-submit request))
           (GET "/organizatori" request (search-organizator request)))
           