(ns doorpe.backend.server.routes
  (:require [compojure.core :refer [defroutes context GET POST PUT PATCH DELETE]]
            [compojure.route :as route]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [muuntaja.middleware :refer [wrap-format]]
            [doorpe.backend.server.authentication :refer [auth-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]

            [doorpe.backend.home-page :refer [home-page]]
            [doorpe.backend.register :refer [register]]
            [doorpe.backend.server.login :refer [login]]
            [doorpe.backend.all-categories :refer [all-categories]]
            [doorpe.backend.all-services :refer [all-services]]
            [doorpe.backend.all-services-by-category-id :refer [all-services-by-category-id]]
            [doorpe.backend.all-service-providers-by-service-id :refer [all-service-providers-by-service-id]]

            [doorpe.backend.dashboard :refer [dashboard]]
            [doorpe.backend.my-bookings :refer [my-bookings]]
            [doorpe.backend.book-service :refer [book-service]]
            [doorpe.backend.provide-service :refer [provide-service]]
            [doorpe.backend.provide-service-by-category-id :refer [provide-service-by-category-id]]
            [doorpe.backend.accept-booking :refer [accept-booking]]
            [doorpe.backend.cancel-booking :refer [cancel-booking]]
            [doorpe.backend.reject-booking :refer [reject-booking]]
            [doorpe.backend.my-profile :refer [my-profile]]
            [doorpe.backend.update-my-profile :refer [update-my-profile]]
            [doorpe.backend.server.send-otp :refer [send-otp]]
            [doorpe.backend.book-complaint :refer [book-complaint]]
            [doorpe.backend.pay-dues :refer [pay-dues]]
            [doorpe.backend.pending-dues :refer [pending-dues]]
            [doorpe.backend.server.logout :refer [logout]]

            [doorpe.backend.admin-add :refer [admin-add]]
            [doorpe.backend.admin-edit :refer [admin-edit]]
            [doorpe.backend.all-service-requests :refer [all-service-requests]]
            [doorpe.backend.approve-service-request :refer [approve-service-request]]
            [doorpe.backend.reject-service-request :refer [reject-service-request]]
            [doorpe.backend.check-complaints :refer [check-complaints]]
            [doorpe.backend.complaint-reply :refer [complaint-reply]]
            [doorpe.backend.revenue-generated :refer [revenue-generated]]))

(defroutes app-routes
  (context "/" []
    ;; public
    (GET "/" [] home-page)
    (GET "/all-categories" [] all-categories)
    (GET "/all-services" [] all-services)
    (GET "/all-services-by-category-id/:category-id" [] all-services-by-category-id)
    (GET "/all-service-providers-by-service-id/:service-id" [] all-service-providers-by-service-id)
    (GET "/send-otp/:contact/:otp-method" [] send-otp)
    (POST "/register" [] register)
    (POST "/login" [] login)

    ;; customer
    (POST "/book-service" [] book-service)
    (POST "/cancel-booking/:booking-id" [] cancel-booking)

    ;; service-provider
    (GET "/pending-dues" [] pending-dues)
    (POST "/accept-booking/:booking-id" [] accept-booking)
    (POST "/reject-booking/:booking-id" [] reject-booking)
    (POST "/provide-service" [] provide-service)
    (POST "/pay-dues" [] pay-dues)
    (GET "/provide-service-by-category-id/:category-id" [] provide-service-by-category-id)

    ;; shared
    (GET "/dashboard" [] dashboard)
    (GET "/my-bookings" [] my-bookings)
    (GET "/my-profile" [] my-profile)
    (POST "/update-my-profile" [] update-my-profile)
    (POST "/book-complaint" [] book-complaint)
    (POST "/logout" [] logout)

    ;; admin
    (GET "/all-service-requests" [] all-service-requests)
    (GET "/approve-service-request" [] approve-service-request)
    (GET "/reject-service-request" [] reject-service-request)
    (GET "/check-complaints" [] check-complaints)
    (GET "/revenue-generated" [] revenue-generated))
    (POST "/admin-edit/:edit-what" [] admin-edit)
    (POST "/admin-add/:add-what" [] admin-add)
    (POST "/complaint-reply" [] complaint-reply)
  (route/not-found "page not found"))

(def app
  (-> app-routes
      (wrap-authentication auth-backend)
      (wrap-authorization auth-backend)
      (wrap-cors :access-control-allow-origin [#"http://localhost:8000" #"http://localhost:7000" #"http://."]
                 :access-control-allow-methods [:get :put :post :delete])
      wrap-format
      wrap-keyword-params
      wrap-params
      wrap-multipart-params))