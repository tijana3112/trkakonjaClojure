(ns trkakonja.layout
    (:use trkakonja.core
        ring.server.standalone
        [ring.middleware file-info file]))

(defonce server (atom nil))

(defn get-handler []

  (-> #'app
    (wrap-file "resources")
    (wrap-file-info)))

(defn start-server
  [& [port]]
  (let [port (if port (Integer/parseInt port) 8080)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :init init
                    :auto-reload? true
                    :destroy destroy
                    :join true}))
    (println (str "Adresa je http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))
