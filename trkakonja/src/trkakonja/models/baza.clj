(ns trkakonja.models.baza
(:require [clojure.java.jdbc :as sql]
            [korma.core :as k]
            [korma.db :refer [defdb mysql]]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.edn :as edn]
            )
  (:import java.sql.DriverManager))

(def db-config(edn/read-string (slurp "config.edn")))

(defdb db (mysql db-config))
  
(k/defentity organizator
  (k/table :organizator))

(k/defentity trkac
  (k/table :trkac))
  
 (defn add-organizator [params]
  (k/insert organizator
  (k/values params)))

(defn delete-organizator [id]
  (k/delete organizator
  (k/where {:organizatorID id})))

(defn find-organizator [params]
  (k/select organizator
            (k/where params)))

(defn find-organizator-by-id [params]
  (k/select organizator
            (k/where params)))

(defn get-organizator []
  (k/select organizator))

(defn update-organizator [params]
  (k/update organizator
            (k/set-fields params)
            (k/where {:id (:id params)})))
(defn search-organizator [text]
  (k/select organizator
            (k/where (or
                       {:organizatorID text}
                       {:imePrezime text}
                       {:username text}
                       {:password text}))
            (k/order :organizatorID :ASC)))


(k/defentity konj
  (k/table :konj))

(defn get-konj []
  (k/select konj
          (k/fields :* [:trkac.imePrezime :time])
          (k/join trkac (= :konj.trkacID :trkac.trkacID))))

(defn get-text-search [text]
  (str "%" text "%"))

(defn search-konj [text]
  (k/select konj
            (k/fields :* [:trkac.imePrezime :time])
            (k/join trkac (= :konj.trkacID :trkac.trkacID) )
            (k/where (or
                       {:konjID text}
                       {:ime [like (get-text-search text)]}
                       {:boja [like (get-text-search text)]}
                       {:rasa [like (get-text-search text)]}
                       {:sampion [like (get-text-search text)]}
                       {:slika text}
                       {:trkac.imePrezime [like (get-text-search text)]}
                       {:trkac.trkacID text}))
            (k/order :konjID :ASC)))

(defn add-konj [params]
  (k/insert konj
  (k/values params)))

(defn delete-konj [id]
  (k/delete konj
  (k/where {:konjID id})))

(defn delete-konj-trkac [id]
  (k/delete konj
  (k/where {:trkacID id})))

(defn find-konj [params]
  (k/select konj
          (k/fields :* [:trkac.imePrezime :time])
          (k/join trkac (= :konj.trkacID :trkac.trkacID))
          (k/where params)))


(defn find-konj-by-id [id]
  (k/select konj
          (k/fields :* [:trkac.imePrezime :time])
          (k/join trkac (= :konj.trkacID :trkac.trkacID))
          (k/where {:konjID id})))


(defn find-konj-by-trkac [id]
  (k/select konj
          (k/fields :* [:trkac.imePrezime :time])
          (k/join trkac (= :konj.trkacID :trkac.trkacID))
          (k/where {:trkacID id})))


(defn update-konj [params]
  (k/update konj
            (k/set-fields params)
            (k/where {:konjID (:konjID params)})))

(k/defentity trka
  (k/table :trka))

(defn get-trka[]
  (k/select trka
          (k/fields :* [:organizator.imePrezime :oime])
          (k/join organizator (= :trka.organizatorID :organizator.organizatorID))))

(defn add-trka[params]
  (k/insert trka
  (k/values params)))

(defn delete-trka [trkaID]  
  (k/delete trka
  (k/where {:trkaID trkaID})))

(defn delete-trka-organizator [organizator]
  (k/delete organizator
  (k/where {:organizatorID organizator})))

(defn find-trka [params]
  (k/select trka
          (k/fields :* [:organizator.imePrezime :oime])
          (k/join organizator (= :trka.organizatorID :organizator.organizatorID))
          (k/where params)))


(defn search-trka [text]
  (k/select trka
            (k/fields :* [:organizator.imePrezime :oime])
            (k/join organizator (= :trka.organizatorID :organizator.organizatorID))
            (k/where (or
                       {:trkaID [like (get-text-search text)]}
                       {:tip [like (get-text-search text)]}
                       {:naziv [like (get-text-search text)]}
                       {:grad [like (get-text-search text)]}                       
                       {:organizator.imePrezime [like (get-text-search text)]}))
            (k/order :trkaID :ASC)))

(defn update-trka [params]
  (k/update trka
            (k/set-fields params)
            (k/where {:trkaID (:trkaID params)})))


(k/defentity ishod
  (k/table :ishod))
(defn get-ishod []
   (k/select ishod
          (k/fields :* [:trka.naziv :tnaziv])
          (k/fields :* [:trka.tip :tip])
          (k/fields :* [:trka.grad :tgrad])
          (k/fields :* [:trka.trkaID :trkaID])
          (k/join trka (= :ishod.trkaID :trka.trkaID))
          (k/fields :* [:konj.ime :kime])
          (k/join konj (= :ishod.konjID :konj.konjID))
          (k/fields [:ishod.mesto :mesto]) ))

(defn search-ishod [text]
   (k/select ishod
          (k/fields [:trka.naziv :tnaziv])
          (k/fields [:trka.tip :tip])
          (k/fields [:trka.grad :tgrad])
          (k/fields [:trka.trkaID :trkaID])
          (k/join trka (= :ishod.trkaID :trka.trkaID))
          (k/fields [:konj.ime :kime])
          (k/join konj (= :ishod.konjID :konj.konjID))
          (k/fields [:ishod.mesto :mesto])   
          (k/fields [:trkac.imePrezime :time])
          (k/join trkac (= :konj.trkacID :trkac.trkacID))
          (k/order :mesto :ASC)))

(defn find-ishod-by-trka [id]
  (k/select ishod
          (k/fields [:trka.naziv :tnaziv])
          (k/fields [:trka.tip :tip])
          (k/fields [:trka.grad :tgrad])
          (k/fields [:trka.trkaID :trka])
          (k/join trka (= :ishod.trkaID :trka.trkaID))
          (k/fields [:konj.ime :kime])
          (k/fields [:konj.konjID :konjID])
          (k/join konj (= :ishod.konjID :konj.konjID))
          (k/fields [:ishod.mesto :mesto]) 
          (k/where id)
          (k/order :mesto)))

(defn find-ishod [params]
  (k/select ishod          (k/fields :* [:trka.naziv :tnaziv])
          (k/join trka (= :ishod.trkaID :trka.trkaID))
          (k/fields :* [:konj.ime :kime])
          (k/join konj (= :ishod.konjID :konj.konjID))
          (k/where params)))

(defn add-ishod [params]
  (k/insert ishod
  (k/values params)))
(defn delete-ishod-trka [trka]
  (k/delete ishod
  (k/where {:trkaID trka})))
(defn update-ishod [params]
  (k/update ishod
            (k/set-fields params)
            (k/where {:trkaID (:trkaID params) :konjID (:konjID params)})))

(defn get-trkac []
  (k/select trkac))

(defn search-trkac [text]
  (k/select trkac
            (k/where (or
                       {:trkacID [like (get-text-search text)]}
                       {:imePrezime [like (get-text-search text)]}
                       {:grad [like (get-text-search text)]}
                       {:username [like (get-text-search text)]}
                       {:password [like (get-text-search text)]}))
            (k/order :trkacID :ASC)))

(defn add-trkac [params]
  (k/insert trkac
  (k/values params)))

(defn delete-trkac [id]
  (k/delete trkac
  (k/where {:trkacID id})))

(defn find-trkac [params]
  (k/select trkac
            (k/where params)))

(defn find-trkac-by-id [params]
  (k/select trkac
            (k/where params)))

