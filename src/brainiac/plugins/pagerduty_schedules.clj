(ns brainiac.plugins.pagerduty-schedules
  (:import [java.util Calendar TimeZone]
           [java.text SimpleDateFormat])
  (:use [clojure.contrib.json :only (read-json)]
        [clojure.java.io :only (reader)])
  (:require [brainiac.plugin :as brainiac]))

(defn now []
  (.getTime (Calendar/getInstance (TimeZone/getTimeZone "America/Chicago"))))

(defn date-formatter [date]
  (let [date-formatter (SimpleDateFormat. "yyyy-MM-dd'T'HH:mmZ")]
    (.format date-formatter date)))

(defn schedule-url [schedule]
  (let [current-time (date-formatter (now))]
    (format "https://braintree.pagerduty.com/api/v1/schedules/%s/entries?since=%s&until=%s&overflow=true" schedule current-time current-time)))

(defn html []
  [:script#schedule-template {:type "text/mustache"}
   "<h3>On Call Now</h3>{{#data}}<p>{{ user.name }}</p>{{/data}}"])

(defn transform [stream]
  (let [json (read-json (reader stream))]
    (assoc {}
      :name "pagerduty-schedule"
      :type "schedule"
      :data (:entries json))))

(defn configure [{:keys [program-name username password schedule_ids]}]
  (brainiac/schedule
    5000
    (brainiac/simple-http-plugin
      {:method :get :url (schedule-url schedule_ids) :basic-auth [username password]}
      transform program-name)))
