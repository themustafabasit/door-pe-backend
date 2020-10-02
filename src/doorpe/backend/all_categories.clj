(ns doorpe.backend.all-categories
  (:require [ring.util.response :as response]
            [doorpe.backend.db.query :as query]))

(defn all-categories
  [req]
  (let [coll "categories"
        categories (query/retreive-all coll)]
    (response/response categories)))