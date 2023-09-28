(ns musick.app
  (:require
   [goog.dom :as gdom]
   ;; [goog.string :as gstr]
   ;; [goog.string.format]
   [reagent.core :as r]
   ["react-dom/client" :refer [createRoot]]))

(def styles
  {:selected {:font-weight :bold}})

(defn style
  [k]
  {:style (get styles k {})})

(def notes
  {:A []
   :B []
   :C []
   :D []
   :E []
   :F []
   :G []})

(def scales
  {:major         [0 4 2 1 2 2 2 1]
   :minor         [0 2 1 2 2 1 2 1]
   #_#_:diminshed []
   #_#_:augmented []
   #_#_:dominant  []
   })

(defn render-note
  [selected-note set-note index note]
  [:li (merge
         (when (= note selected-note)
           (style :selected))
         {:key (str index "-" note)})
   [:a {:href     "#"
        :on-click #(set-note note)}
    (name note)]])

(defn render-scale
  [selected-scale set-scale index scale]
  [:li (merge
         (style {:selected #(= scale selected-scale)})
         {:key (str index "-" scale)})
   [:a {:href     "#"
        :on-click #(set-scale scale) }
    (name scale)]])

(defn main
  []
  (r/with-let [note      (r/atom :A)
               scale     (r/atom :major)
               set-note  (fn [new-note] (reset! note new-note))
               set-scale (fn [new-scale] (reset! scale new-scale))]
    (js/console.log (clj->js {:note @note :scale @scale}))
    [:main (style :test)
     [:h1 ":musick/scales"]
     [:hr]
     [:div#tones.picker
      [:nav
       [:h3 "tones"]
       [:ul
        (doall
          (->> notes
            (keys)
            (map-indexed (partial render-note @note set-note))))]]]
     [:div#scales.picker
      [:nav
       [:h3 "scales"]
       [:ul
        (doall
          (->> scales
            (keys)
            (map-indexed (partial render-scale @scale set-scale))))]]]]))

(defonce root (createRoot (gdom/getElement "app")))

(defn init
  "initialize root application"
  []
  (.render root (r/as-element [main])))

(defn ^:dev/after-load re-render
  []
  (init))
