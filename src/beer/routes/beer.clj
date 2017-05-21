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
   :style [st/required st/number]
   :alcohol [st/required st/number]
   :manufacturer [st/required st/string]
   :country [st/required st/string]
   :info [st/required st/string]
   :picture [st/required st/string]})

(def comment-schema
  {:user [st/required st/number]
   :beer [st/required st/number]
   :text [st/required st/string]})

(defn authenticated-admin? [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn get-logged-user-id [session]
  (:id (:identity session)))

(defn upload-picture [{:keys [filename tempfile] picture :picture}]
  (io/copy tempfile (io/file "resources" "public" "images" "beer" filename)))

(defn get-add-beer [session &[message]]
  (if-not (authenticated? session)
    (redirect "/login")
    (render-file "templates/beer-add.html" {:title "Add beer"
                                            :logged (:identity session)
                                            :message message
                                            :styles (db/get-styles)})))

(defn get-beer [{:keys [params session]} &[message]]
  (cond
    (not (authenticated? session))
    (redirect "/login")
    (check-authenticated-admin? session)
    (render-file "templates/beer-admin.html" {:title (str "Beer " (:id params))
                                        :logged (:identity session)
                                        :message message
                                        :beer (first (db/find-beer-by-id (:id params)))
                                        :styles (db/get-styles)
                                        :likes (db/get-likes (:id params))
                                        :liked (count (db/find-user-like-for-beer (:id params) (:id (:identity session))))
                                        :comments (db/get-comments (:id params))})
    :else
    (render-file "templates/beer-user.html" {:title (str "Beer " (:id params))
                                        :logged (:identity session)
                                        :message message
                                        :beer (first (db/find-beer-by-id (:id params)))
                                        :styles (db/get-styles)
                                        :likes (db/get-likes (:id params))
                                        :liked (count (db/find-user-like-for-beer (:id params) (:id (:identity session))))
                                        :comments (db/get-comments (:id params))})))

(defn add-beer [{:keys [params session]}]
  (let [beer-name (:name params)
        origin (read-string (:origin params))
        price (read-string (:price params))
        style (read-string (:style params))
        alcohol (read-string (:alcohol params))
        manufacturer (:manufacturer params)
        country (:country params)
        info (:info params)
        picture-name (if (not (nil? (:picture params))) (str "/images/beer/" (:filename (:picture params))) (:picture_url params))]
    (cond
    (not= (authenticated? session))
     (redirect "/login")
    (and (st/valid? {:name beer-name
                     :origin origin
                     :price price
                     :style style
                     :alcohol alcohol
                     :manufacturer manufacturer
                     :country country
                     :info info
                     :picture picture-name} beer-schema))
      (do
        (if (not(nil? (:picture params)))
        (upload-picture (:picture params)))
        (redirect (str "/beer/" (:generated_key (db/add-beer beer-name origin price style alcohol manufacturer country info picture-name)))))
    :else
      (-> (get-add-beer session {:text "All fields are required" :type "error"})))))

(defn get-beers [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-beers)
    (db/search-beers (str "%" text "%"))))

(defn get-search-beers [params session]
  (render-file "templates/beer-search.html" {:title "Search beers"
                                             :logged (:identity session)
                                             :beers (get-beers nil)}))

(defn add-comment [{:keys [params session]}]
  (let [user (get-logged-user-id session)
        beer (:id params)
        text (:comment params)]
    (cond
    (not= (authenticated? session))
     (redirect "/login")
    (and (st/valid? {:user user
                     :beer (read-string beer)
                     :text text} comment-schema))
      (do (db/add-comment user beer text)
        (redirect (str "/beer/" beer)))
    :else
      (-> (get-beer (zipmap [:params :session] [params session])
                    {:text "All fields are required"
                     :type "error"})))))

(defn update-beer-data [params session]
  (println params)
  (let [id (:id params)
        beer-name (:name params)
        origin (read-string (:origin params))
        price (read-string (:price params))
        style (read-string (:style params))
        alcohol (read-string (:alcohol params))
        manufacturer (:manufacturer params)
        country (:country params)
        info (:info params)
        picture-name (if (not (nil? (:picture params))) (str "/images/beer/" (:filename (:picture params))) (:picture_url params))]
    (cond
    (not= (authenticated? session))
     (redirect "/login")
    (and (st/valid? {:name beer-name
                     :origin origin
                     :price price
                     :style style
                     :alcohol alcohol
                     :manufacturer manufacturer
                     :country country
                     :info info
                     :picture picture-name} beer-schema))
      (do
        (if (not(nil? (:picture params)))
        (upload-picture (:picture params)))
        (db/update-beer id beer-name origin price style alcohol manufacturer country info picture-name))
    :else
      {:text "All fields are required" :type "error"} )))


(defresource search-beers [{:keys [params session]}]
  :allowed-methods [:post]
  :authenticated? (authenticated? session)
  :handle-created (json/write-str (get-beers (:text params)))
  :available-media-types ["application/json"])

(defresource search-beers [{:keys [params session]}]
  :allowed-methods [:get]
  :available-media-types ["text/html" "application/json"]
  :authorized? (fn [_] (authenticated-admin? session))
  :handle-ok #(let [media-type (get-in % [:representation :media-type])]
                    (condp = media-type
                      "text/html" (get-search-beers params session)
                      "application/json" (json/write-str (get-beers (:text params))))))

(defresource update-beer [{:keys [params session]}]
  :allowed-methods [:put]
  :handle-malformed "beer id cannot be empty"
  :authenticated? (authenticated? session)
  :new? false
  :respond-with-entity? true
  :put! (fn [_] (update-beer-data params session))
  :handle-ok (fn [_] (json/write-str "Beer successfully edited"))
  :available-media-types ["application/json"])

(defresource delete-beer [{:keys [params session]}]
  :allowed-methods [:delete]
  :handle-malformed "beer id cannot be empty"
  :authorized? (authenticated? session)
  :new? false
  :respond-with-entity? false
  :delete! (fn [_] (db/delete-beer (:id params)))
  :handle-created (fn [_] (json/write-str "Beer successfully deleted"))
  :available-media-types ["application/json"])

(defresource handle-like [{:keys [params session]}]
  :allowed-methods [:post :delete]
  :handle-malformed "beer id cannot be empty"
  :authenticated? (authenticated? session)
  :new? false
  :respond-with-entity? true
  :post! (fn [_] (db/add-like (get-logged-user-id session) (:id params)))
  :delete! (fn [_] (db/delete-like (get-logged-user-id session) (:id params)))
  :handle-ok (fn [_] (json/write-str (count (db/get-likes (:id params)))))
  :available-media-types ["application/json"])

(defresource delete-comment [{:keys [params session]}]
  :allowed-methods [:delete]
  :handle-malformed "comment id cannot be empty"
  :authenticated? (authenticated? session)
  :new? false
  :respond-with-entity? true
  :delete! (fn [_] (db/delete-comment (:id params)))
  :handle-ok (fn [_] (json/write-str (count (db/get-comments (:beer params)))))
  :available-media-types ["application/json"])

(defn admin [{session :session}]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defroutes beer-routes
  (GET "/beer" request (get-add-beer (:session request)))
  (POST "/beer" request (add-beer request))
  (PUT "/beer" request (update-beer request))
  (DELETE "/beer" request (delete-beer request))
  (GET "/beers" request (search-beers request))
  (GET "/beer/:id" request (get-beer request))
  (POST "/comment/:id" request (add-comment request))
  (DELETE "/comment/:id" request (delete-comment request))
  (POST "/like" request (handle-like request))
  (DELETE "/like" request (handle-like request)))

