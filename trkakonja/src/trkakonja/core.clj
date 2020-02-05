(ns trkakonja.core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes routes]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.middleware :refer [wrap-base-url]]
            [liberator.dev :refer :all]
            [selmer.parser :refer :all]
            [trkakonja.routes.home :refer [home-routes]]
            [trkakonja.routes.loginregistration :refer [log-routes]]
            [trkakonja.routes.userforma :refer [forme-routes]]
            [trkakonja.routes.konj :refer [konj-routes]]
            [trkakonja.routes.trka :refer [trka-routes]]
            [trkakonja.routes.ishod :refer [ishod-routes]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.middleware.flash :refer [wrap-flash]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [wrap-access-rules success error]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.accessrules :refer [restrict]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defn start [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello Clojure!"})

(defn on-error [request response]
  {:status  403
   :headers {"Content-Type" "text/plain"}
   :body    (str "Access to " (:uri request) " is not authorized")})

(def backend (session-backend))

(defn destroy [])

(defn init []
  (System/setProperties
  (doto (java.util.Properties. (System/getProperties))
    (.put "com.mchange.v2.log.MLog" "com.mchange.v2.log.FallbackMLog")
    (.put "com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL" "OFF")))
  (selmer.parser/cache-off!))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes  home-routes log-routes forme-routes trka-routes konj-routes ishod-routes app-routes)
      (wrap-json-response)
      (handler/site)
      (wrap-authentication backend)
      (wrap-authorization backend)
      (wrap-base-url)
      (wrap-trace :header :ui)
      (wrap-resource "images")))
