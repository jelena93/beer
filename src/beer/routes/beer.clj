(ns beer.routes.beer
  (:require [compojure.core :refer :all]
            [selmer.parser :refer [render-file]]
            [beer.models.db :as db]
            [compojure.response :refer [render]]
            [buddy.auth :refer [authenticated?]]
            [liberator.core :refer [defresource]]
            [clojure.data.json :as json]
            [struct.core :as st]
            [clojure.java.io :as io]
            [ring.util.response :refer [redirect]]))

(def file-config (clojure.edn/read-string (slurp "conf/file-config.edn")))

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

(defn authenticated-admin? [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn get-picture-url [params]
  (if-not (contains? params :url)
    (str (:short-img-location file-config) (:filename (:picture params)))
    (:url params)))

(defn beer-validation? [params]
  (st/valid? {:name (:name params)
              :origin (read-string (:origin params))
              :price (read-string (:price params))
              :style (read-string (:style params))
              :alcohol (read-string (:alcohol params))
              :manufacturer (:manufacturer params)
              :country (:country params)
              :info (:info params)
              :picture (get-picture-url params)} beer-schema))

(defn upload-picture [{:keys [filename tempfile] picture :picture}]
  (io/copy tempfile (io/file (:full-img-location file-config) filename)))

(defn get-add-beer [session &[message]]
  (if-not (authenticated? session)
    (redirect "/login")
    (render-file "templates/beer-add.html" {:title "Add beer"
                                            :logged (:identity session)
                                            :message message
                                            :styles (db/get-styles)})))

(defn add-beer->db [params]
  (println params)
  (if-not (contains? params :url)
    (upload-picture params))
  (-> (assoc (select-keys params [:name :origin :price :style :alcohol :manufacturer :country :info]) :picture (get-picture-url params))
    (db/add-beer)
    (:generated_key)))

(defn add-beer [{:keys [params session]}]
  (cond
    (not= (authenticated-admin? session))
    (redirect "/login")
    (beer-validation? params)
    (redirect (str "/beer/" (add-beer->db params)))
    :else
    (-> (get-add-beer session {:text "All fields are required" :type "error"}))))

(defn get-beer [{:keys [params session]} &[message]]
  (cond
    (not (authenticated? session))
    (redirect "/login")
    (authenticated-admin? session)
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

(defn get-beers [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-beers)
    (db/search-beers (str "%" text "%"))))

(defn get-search-beers [params session]
  (render-file "templates/beer-search.html" {:title "Search beers"
                                             :logged (:identity session)
                                             :beers (get-beers nil)}))

(defn update-beer-data [params session]
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
  :malformed? (fn [_] (empty? (:id params)))
  :handle-malformed (fn [_] "Please provide an id")
  :exists? (fn [_] (not (empty? (db/find-beer-by-id (:id params)))))
  :handle-not-found (fn [_] (str "There is no beer with id " (:id params)))
  :authorized? (authenticated? session)
  :new? false
  :respond-with-entity? true
  :delete! (fn [_] (db/delete-beer (:id params)))
  :handle-ok (fn [_] (json/write-str "Beer successfully deleted"))
  :available-media-types ["application/json"])

(defroutes beer-routes
  (GET "/beer" request (get-add-beer (:session request)))
  (POST "/beer" request (add-beer request))
  (PUT "/beer" request (update-beer request))
  (DELETE "/beer" request (delete-beer request))
  (GET "/beers" request (search-beers request))
  (GET "/beer/:id" request (get-beer request)))
