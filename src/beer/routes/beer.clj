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
            [liberator.representation :refer [ring-response as-response]]
            [clojure.set :refer [rename-keys]]
            [clojure.string :as str]
            [ring.util.response :refer [redirect]]))

(def file-config (clojure.edn/read-string (slurp "conf/file-config.edn")))

(defn create-file-name [{:keys [fname content-type]}]
  (str (:short-img-location file-config) fname "." (last (str/split content-type #"/"))))

(defn get-picture-url [params]
  (if (contains? params :url)
    (:url params)
    (->(assoc (:file params) :fname (:name params))
       (create-file-name))))

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

(defn authenticated-admin? [session]
  (and (authenticated? session)
       (="admin" (:role (:identity session)))))

(defn upload-picture [{:keys [fname tempfile]}]
  (io/copy tempfile (io/file (:resources-folder file-config) fname)))

(defn get-add-beer-page [session &[message]]
  (if-not (authenticated? session)
    (redirect "/login")
    (render-file "templates/beer-add.html" {:title "Add beer"
                                            :logged (:identity session)
                                            :message message
                                            :styles (db/get-styles)})))

(defn add-beer->db [params]
  (let [file (get-picture-url params)]
    (if-not (contains? params :url)
      (->(assoc (:file params) :fname file))
      (upload-picture))
    (-> (dissoc (assoc params :picture file) :file :url)
        (db/add-beer)
        (:generated_key))))

(defn add-beer [{:keys [params session]}]
  (cond
    (not= (authenticated-admin? session))
    (redirect "/login")
    (beer-validation? params)
    (redirect (str "/beer/" (add-beer->db params)))
    :else
    (-> (get-add-beer-page session {:text "All fields are required"
                                    :type "error"}))))

(defn get-beer-page [page params session &[message]]
  (render-file page {:title (str "Beer " (:id params))
                     :logged (:identity session)
                     :message message
                     :beer (first (db/find-beer params))
                     :styles (db/get-styles)
                     :likes (db/find-like (hash-map :beer (:id params)))
                     :liked (count (db/find-like (hash-map :beer (:id params) :user (:id (:identity session)))))
                     :comments (db/find-comment (select-keys (rename-keys params {:id :beer}) [:beer]))}))

(defn get-beer [{:keys [params session]} &[message]]
  (cond
    (not (authenticated? session))
    (redirect "/login")
    (authenticated-admin? session)
    (get-beer-page "templates/beer-admin.html" params session message)
    :else
    (get-beer-page "templates/beer-user.html" params session message)))

(defn get-beers [text]
  (if (or (nil? text)
          (= "" text))
    (db/get-beers)
    (db/search-beers text)))

(defn get-search-beers [params session]
  (render-file "templates/beer-search.html" {:title "Search beers"
                                             :logged (:identity session)
                                             :beers (get-beers nil)}))
(defn get-beer-picture-from-db [params]
  (:picture (first (db/find-beer (select-keys params [:id])))))

(defn file-exists? [params]
  (.exists (clojure.java.io/as-file (str (:resources-folder file-config) (get-beer-picture-from-db params)))))

(defn update-beer-data [params]
  (let [file (get-picture-url params)]
    (if-not (contains? params :url)
      (->(assoc (:file params) :fname file)
         (upload-picture)))
    (->(dissoc (assoc params :picture (get-picture-url params)) :file :url)
      (db/update-beer))))

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
                  "application/json" (->(:text params)
                                        (get-beers)
                                        (json/write-str)))))

(defresource update-beer [{:keys [params session]}]
  :allowed-methods [:put]
  :available-media-types ["application/json"]
  :malformed? (fn [_] (not (beer-validation? params)))
  :handle-malformed "All fields are required"
  :exists? (fn [_] (not (empty? (db/find-beer (select-keys params [:id])))))
  :can-put-to-missing? false
  :authorized? (authenticated-admin? session)
  :new? false
  :respond-with-entity? true
  :put! (fn [_] (update-beer-data params))
  :handle-ok (fn [_] (json/write-str {:message "Beer successfully edited"
                                      :beer (->(select-keys params [:id])
                                               (db/find-beer)
                                               (first))}))
  :handle-not-implemented (fn [_] (str "There is no beer with id " (:id params))))

(defresource delete-beer [{:keys [params session]}]
  :allowed-methods [:delete]
  :malformed? (fn [_] (empty? (:id params)))
  :handle-malformed (fn [_] "Please provide an id")
  :exists? (fn [_] (not (empty? (db/find-beer params))))
  :handle-not-found (fn [_] (str "There is no beer with id " (:id params)))
  :authorized? (authenticated-admin? session)
  :new? false
  :respond-with-entity? true
  :delete! (fn [_] (db/delete-beer (:id params)))
  :handle-ok (fn [_] (json/write-str "Beer successfully deleted"))
  :available-media-types ["application/json"])

(defroutes beer-routes
  (GET "/beer" request (get-add-beer-page (:session request)))
  (POST "/beer" request (add-beer request))
  (PUT "/beer" request (update-beer request))
  (DELETE "/beer" request (delete-beer request))
  (GET "/beers" request (search-beers request))
  (GET "/beer/:id" request (get-beer request)))
