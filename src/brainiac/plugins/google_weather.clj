(ns brainiac.plugins.google-weather
  (:require [brainiac.plugin :as brainiac]
            [brainiac.xml-utils :as xml]
            [clojure.contrib.zip-filter.xml :as zf]))

(defn parse-conditions [node]
  (assoc {}
    :temp (zf/xml1-> node :temp_f (zf/attr :data))
    :icon (str "http://www.google.com" (zf/xml1-> node :icon (zf/attr :data)))
    :current-conditions (zf/xml1-> node :condition (zf/attr :data))))

(defn transform [stream]
  (let [xml-zipper (xml/parse-xml stream)
        current (zf/xml1-> xml-zipper :weather :current_conditions parse-conditions)]
    (assoc {}
      :name "google-weather"
      :type "weather"
      :title "Right now, outside..."
      :data current)))

(defn html []
  [:script#weather-template {:type "text/mustache"}
   "<h3>{{title}}</h3> <h2 class='temperature'>{{data.temp}}&deg;</h2>
		<h4 class='conditions'><img src={{data.icon}}> {{data.current-conditions}}</h4>"])

(defn url [city]
  (format "http://www.google.com/ig/api?weather=%s" city))

(defn configure [{:keys [city program-name]}]
  (brainiac/schedule
    60000
    (brainiac/simple-http-plugin
       {:url (url city)}
       transform program-name)))
