(ns trkakonja.routes.konj
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

(defn get-trkac [trkac]
  (db/find-trkac-by-id (:trkacID trkac)))

(defn get-konj-by-trkac [params]
    (db/find-konj-by-trkac params))

(defn upload-picture [{:keys [fname tempfile]}]
  (io/copy tempfile (io/file (:resources-folder file-config) fname)))

(defn get-konj-slika-from-db [params]
  (:slika (first (db/find-konj (select-keys params [:konjID])))))

(defn file-exists? [params]
  (.exists (clojure.java.io/as-file (str (:resources-folder file-config) (get-konj-slika-from-db params)))))

(defn get-konji [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-konj)
    (db/search-konj text)))



(defn get-search-konji [params session]
   (cond
    (not (authenticated? session))
    (redirect "/login")
    :else
    (render-file "views/konj-search.html" {:title "Prikaz konja"
                                            :logged (:identity session)
                                            :konji (get-konji nil)})))

(defn get-search-konj-tr [params session]
   (cond
    (not (authenticated? session))
    (redirect "/tlogin")
    :else
    (render-file "views/konj-search-tr.html" {:title "Pretraga konja po trkacu"
                                                :logged (:identity session)
                                                :konji (get-konji (:trkacID :logged.trkacID))})))

(defresource search-konji [{:keys [params session]}]
  :allowed-methods [:post]
  :handle-created (json/write-str (get-konji (:text params)))
  :available-media-types ["application/json"])


(defresource search-konj [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                (condp = media-type
                  "text/html" (get-search-konji params session)
                  "application/json" (->(:text params)
                                        (get-konji)
                                        (json/write-str)))))

(defresource search-konj-tr [{:keys [params session]}]
  :allowed-methods [:post]
  :authenticated? (authenticated? session)
  :handle-created (json/write-str (get-konji (:text params)))
  :available-media-types ["application/json"])

(defresource search-konj-tr [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                (condp = media-type
                  "text/html" (get-search-konj-tr params session)
                  "application/json" (->(:text params)
                                        (get-konji)
                                        (json/write-str)))))

(defn get-add-konj-page [session &[message]]
  (if-not (authenticated? session)
    (redirect "/tlogin")
    (render-file "views/konj-add.html" {:title "Dodavanje novog konja"
                                         :logged (:identity session)})))

(defn add-konj [{:keys [params session]}]
    (konj-validation? params)
    (println params)
    (db/add-konj params)
    (redirect "/trkacForma"))

(defn get-konj-edit-page [page params session]
  (render-file page {:title "Izmena konja"
                     :logged (:identity session)
                     :konj (first (db/find-konj params))}))

(defn get-konj [{:keys [params session]}]
   (if-not (authenticated? session)
    (redirect "/tlogin")
    (get-konj-edit-page "views/edit-konj.html" params session)))

(defresource update-konj [{:keys [params session]}]
  :allowed-methods [:put]  
  :available-media-types ["application/json"]
  (println params)
  (db/update-konj params))


(defresource delete-konj [{:keys [params session]}]
  :allowed-methods [:delete] 
  (db/delete-konj (:konjID params))
  (db/delete-konj-trkac (:konjID params))
  :available-media-types ["application/json"])


(defroutes konj-routes  
  (GET "/pretraga" request (search-konj request))
  (GET "/pretragatr" request (search-konj-tr request))
  (GET "/addkonj" request (get-add-konj-page (:session request)))
  (POST "/addkonj" request (add-konj request))
  (GET "/konj/:konjID" request (get-konj request))
  (PUT "/konj" request (update-konj request))  
  (DELETE "/konj" request (delete-konj request)))



