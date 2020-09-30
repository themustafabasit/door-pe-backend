(ns doorpe.backend.my-bookings
  (:require [ring.util.response :as response]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [doorpe.backend.util :refer [extract-token-from-request docs-object-id->str docs-custom-object-id->str]]
            [monger.util :refer [object-id]]
            [doorpe.backend.db.query :as query]
            [monger.operators :refer [$or]]))

(defn transform-booking-data
  [{:keys [service-provider-id service-id booking-on service-on service-time status]}]
  (let [service-provider-res (query/retreive-one-by-custom-key-value "serviceProviders" :_id (object-id service-provider-id))

        service-provider-name (:name service-provider-res)
        service-provider-contact (:contact service-provider-res)
        service-provider-address (:address service-provider-res)

        services-res (query/retreive-one-by-custom-key-value "services" :_id (object-id service-id))
        service-name (:name services-res)
        service-charge-type (:charge-type services-res)]
    {:service-provider-name service-provider-name
     :service-provider-contact service-provider-contact
     :service-provider-address service-provider-address
     :service-name service-name
     :service-charge-type service-charge-type
     :booking-on booking-on
     :service-on service-on
     :service-time service-time
     :status status}))


  (defn show-customer-my-bookings
    [customer-id]
    (let [coll "bookings"
          pending {:status "pending"}
          accepted {:status "accepted"}
          ref {:customer-id (object-id customer-id)
               $or  [pending
                     accepted]}
          booking-res (-> (query/retreive-all-by-custom-ref coll ref)
                          docs-object-id->str
                          (docs-custom-object-id->str :customer-id)
                          (docs-custom-object-id->str :service-provider-id)
                          (docs-custom-object-id->str :service-id)
                          (docs-custom-object-id->str :review-id))]
      (if (> (count booking-res) 0)
        (response/response (pmap transform-booking-data
                                 booking-res))
        (response/response nil))))

(defn show-service-provider-my-bookings
  [service-provider-id]
  (response/response "sp"))

(defn my-bookings
  [req]
  (if-not (authenticated? req)
    throw-unauthorized
    (let [token (extract-token-from-request req)
          coll "authTokens"
          res (-> (query/retreive-one-by-custom-key-value coll :token token))
          user-id (:user-id res)
          user-type (:user-type res)]
      (cond
        (= "customer" user-type) (show-customer-my-bookings user-id)
        (= "service-provider" user-type) (show-service-provider-my-bookings user-id)))))

; curl  POST -H "authorization: Token ZVJdb1lzhAeQcmSl1D1fPeWKAeoq+NVLqENkoWmyTnc=" -i  "http://localhost:7000/my-bookings"
