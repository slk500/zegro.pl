(ns guestbook.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(defn message-form []
  (let [fields (atom {})]
    (fn []
      [:div.content
       [:div.form-group
        [:p "Name:"
         [:input.form-control
          {:type :text
           :name :name
           :on-change #(swap! fields assoc :name (-> % .-target .-value))
           :value (:name @fields)}]]]
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
         :value "comment"
         :on-click #(send-message! fields)}]])))

<input id="number" type="number" value="42">

(defn send-message! [fields]
  (POST "/oferty/dodaj"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :params @fields
         :handler #(.log js/console (str "response:" %))
         :error-handler #(.log js/console (str "error:" %))}))

(defn home []
  [:div.row
   [:div.span12
    [message-form]]])

(reagent/render
 [home]
 (.getElementById js/document "content"))
