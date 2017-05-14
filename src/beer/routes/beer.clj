(ns beer.routes.beer
  (:require [compojure.core :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            [struct.core :as st]
            [clojure.java.io :as io]
            [ring.util.response :refer [response redirect content-type]]))

(def beer-schema
  {:name [st/required st/string]
   :origin [st/required st/number]
   :price [st/required st/number]
   :beer_style [st/required st/number]
   :alcohol [st/required st/number]
   :manufacturer [st/required st/string]
   :country [st/required st/string]
   :info [st/required st/string]})

(def beer-comment-schema
  {:user-id [st/required st/number]
   :beer-id [st/required st/number]
   :text [st/required st/string]})

(defn authenticated [session]
  (authenticated? session))

(defn authenticated-admin [session]
  (if (and (not (authenticated? session))
       (not="admin" (:role (:identity session))))
    (throw-unauthorized {:message "Not authorized"})))

(defn check-authenticated-admin [session]
  (and (not (authenticated? session))
       (not="admin" (:role (:identity session)))))

(defn get-logged-user-id [session]
  (:id (:identity session)))

(defn get-add-beer [session &[message]]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/beer-add.html" {:title "Add beer"
                                            :logged (:identity session)
                                            :message message
                                            :bs (db/get-beer-styles) })))
(defn get-beer [{:keys [params session] request :request} &[message]]
    (println message)
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/beer.html" {:title (str "Beer " (:id params))
                                            :logged (:identity session)
                                            :message message
                                            :beer (first (db/find-beer-by-id (:id params)))
                                            :likes (db/get-beer-likes (:id params))
                                            :liked (count (db/find-user-like-for-beer (:id params) (:id (:identity session))))
                                            :comments (db/get-beer-comments (:id params))})))

(defn add-beer [{:keys [params session] request :request}]
  (let [beer-name (:name params)
        origin (read-string (:origin params))
        price (read-string (:price params))
        beer-style (read-string (:beer_style params))
        alcohol (read-string (:alcohol params))
        manufacturer (:manufacturer params)
        country (:country params)
        info (:info params)]
    (cond
    (not= (authenticated session))
     (redirect "/login")
    (and (st/valid? {:name beer-name
                     :origin origin
                     :price price
                     :beer_style beer-style
                     :alcohol alcohol
                     :manufacturer manufacturer
                     :country country
                     :info info} beer-schema))
;;       (-> (get-add-beer session {:text (str "Beer successfully added, id: " (db/add-beer params)) :type "success"}))
      (redirect (str "/beer/" (:generated_key (db/add-beer params))))
    :else
      (-> (get-add-beer session {:text "All fields are required" :type "error"})))))

(defn update-beer [{:keys [params session] request :request}]
  (if-not (authenticated session)
    (redirect "/login")
    (render-file "templates/beer-edit.html" {:title "Edit beer" :logged (:identity session) :message (str "Beer successfully edited, id: " (db/update-beer params))})))

(defn get-beers [text]
  (if (or (nil? text) (= "" text))
    (db/get-beers)
    (db/search-beers (str "%" text "%"))))

(defn get-search-beers [{:keys [params session] request :request}]
  (render-file "templates/beer-search.html" {:title "Search beers" :logged (:identity session) :beers (get-beers nil)}))

(defn add-beer-comment [{:keys [params session] request :request}]
  (let [user-id (get-logged-user-id session)
        beer-id (:id params)
        text (:comment params)]
    (cond
    (not= (authenticated session))
     (redirect "/login")
    (and (st/valid? {:user-id user-id
                     :beer-id (read-string beer-id)
                     :text text} beer-comment-schema))
      (do (db/add-beer-comment user-id beer-id text)
        (redirect (str "/beer/" beer-id)))
    :else
      (-> (get-beer (zipmap [:params :session] [params session])
                    {:text "All fields are required" :type "error"})))))


(defresource search-beers [{:keys [params session] request :request}]
  :allowed-methods [:post]
  :authenticated? (authenticated session)
  :handle-created (json/write-str (get-beers (:text params)))
  :available-media-types ["application/json"])

(defresource delete-beer [{:keys [params session] request :request}]
  :allowed-methods [:delete]
  :handle-malformed "beer id cannot be empty"
  :authenticated? (authenticated session)
  :delete! (db/delete-beer (:id params))
  :handle-created (json/write-str "Beer successfully deleted")
  :available-media-types ["application/json"])

(defresource handle-beer-like [{:keys [params session] request :request}]
  :allowed-methods [:post :delete]
  :handle-malformed "beer id cannot be empty"
  :authenticated? (authenticated session)
  :new? false
  :respond-with-entity? true
  :post! (fn [_] (db/add-beer-like (get-logged-user-id session) (:id params)))
  :delete! (fn [_] (db/delete-beer-like (get-logged-user-id session) (:id params)))
  :handle-ok (fn [_] (json/write-str (count (db/get-beer-likes (:id params)))))
  :available-media-types ["application/json"])

(defresource delete-beer-comment [{:keys [params session] request :request}]
  :allowed-methods [:delete]
  :handle-malformed "comment id cannot be empty"
  :authenticated? (authenticated session)
  :new? false
  :respond-with-entity? true
  :delete! (fn [_] (db/delete-beer-comment (:id params)))
  :handle-ok (fn [_] (json/write-str (count (db/get-beer-comments (:beer params)))))
  :available-media-types ["application/json"])

(defresource tests [{:keys [params session] request :request}]
  :allowed-methods [:get]
  :handle-malformed "id cannot be empty"
  :authenticated? (authenticated session)
  :new? false
  :respond-with-entity? true
  :get (println (io/input-stream  (:picture (first (db/find-beer-by-id (:id params))))))
;;   :handle-ok (fn [_] (slurp (:picture (first (db/find-beer-by-id (:id params))))))
  :handle-ok (fn [_] (io/input-stream  (:picture (first (db/find-beer-by-id (:id params))))))
  :available-media-types ["image/jpeg"]
  )
;; (defn tests [{:keys [params session] request :request}]
;; (let [beer (first (db/find-beer-by-id (:id params)))]
;;   ))

(defn admin-view []
  (response "ADMINS ONLY"))

(defn admin [{session :session}]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defroutes beer-routes
  (GET "/beer" request (get-add-beer (:session request)))
  (GET "/image/:id" request (tests request))
  (POST "/beer" request (add-beer request))
  (PUT "/beer" request (update-beer request))
  (DELETE "/beer" request (delete-beer request))
  (GET "/beers" request (get-search-beers request))
  (POST "/beers" request (search-beers request))
  (GET "/beer/:id" request (get-beer request))
  (POST "/beer/comment/:id" request (add-beer-comment request))
  (DELETE "/beer/comment/:id" request (delete-beer-comment request))
  (POST "/beer/like:/id" request (handle-beer-like request))
  (DELETE "/beer/like/:id" request (handle-beer-like request)))

