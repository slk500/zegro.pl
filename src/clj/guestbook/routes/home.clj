(ns guestbook.routes.home
  (:require
    [guestbook.layout :as layout]
    [guestbook.db.core :as db]
    [clojure.java.io :as io]
    [guestbook.middleware :as middleware]
    [ring.util.http-response :as response]
    [struct.core :as st]))

(defn home-page [{:keys [flash] :as request}]
  (layout/render
   request
   "home.html"
   (merge {:items (db/get-messages)}
          (select-keys flash [:name :item :errors]))))

(defn about-page [request]
  (layout/render request "about.html"))

(defn offer-form [request]
  (layout/render request "offer-form.html"))

(def message-schema
  [[:name
    st/required
    st/string]

   [:description
    st/required
    st/string
    {:description "message must contain at least 10 characters"
     :validate #(> (count %) 9)}]])

(defn validate-message [params]
  (first (st/validate params message-schema)))

(defn save [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/save-message!
       (assoc params :timestamp (java.util.Date.)))
      (response/found "/"))))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/oferty/dodaj" {:get offer-form
                    :post save}]
   ["/about" {:get about-page}]])

