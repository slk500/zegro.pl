(ns guestbook.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(defn send-message! [fields]
  (POST "/api/offers"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :params @fields
         :handler #(.log js/console (str "response:" %))
         :error-handler #(.log js/console (str "error:" %))}))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.alert.alert-danger (clojure.string/join error)]))

(defn get-messages [messages]
  (GET "/api/offers"
       {:headers {"Accept" "application/transit+json"}
        :handler #(reset! messages (vec %))}))

(defn message-list [messages]
  [:ul.content
   (for [{:keys [name description price timestamp]} @messages]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p description]
      [:p " - " name]])])

(defn message-form []
  (let [fields (atom {})
        errors (atom nil)]
    (fn []
      [:div.content
       [:div.form-group
        [:p "Name:"
         [:input.form-control
          {:type :text
           :name :name
           :on-change #(swap! fields assoc :name (-> % .-target .-value))
           :value (:name @fields)}]]]
       [errors-component errors  :name]
       [:p "Description:"
        [:textarea.form-control
         {:rows 4
          :cols 50
          :name :description
          :on-change #(swap! fields assoc :description (-> % .-target .-value))}
         (:description @fields)]]
       [:p "Price:"
        [:input.form-control
         {:type :number
          :name :price
          :on-change  #(swap! fields assoc :price (-> % .-target .-value))
          :value (:price @fields)}]]
       [:input.btn.btn-primary
        {:type :submit
         :value "Wyślij"
         :on-click #(send-message! fields errors)}]])))

(defn home []
  (let [messages (atom nil)]
    (get-messages messages)
    (fn []
      [:div
       [:div.row
        [:div.span12
         [message-list messages]]]
       [:div.row
        [:div.span12
         [message-form]]]])))


(reagent/render
 [home]
 (.getElementById js/document "offer-form"))
