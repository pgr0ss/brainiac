(ns brainiac.pages.layout
  (:require [brainiac.plugin :as plugin])
  (:use [noir.core :only (defpartial)]
        hiccup.core
        hiccup.form-helpers
        hiccup.page-helpers
        brainiac.loader))

(defpartial plugins []
  (map plugin/render @loaded))

(defpartial main-layout [& content]
  (html5
    [:head
     [:title "Brainiac"]
     (include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
     (include-js "/js/vendor/jquery.mustache.js")
     (include-js "/js/vendor/jquery.vticker.js")
     (include-js "/js/widgets.js")
     (include-js "/js/updater.js")
     (include-js "/js/debug.js")
     (include-css "/css/main.css")]
    [:body.nocursor
     content
     [:div.templates (map plugin/render-template @loaded)]]))

