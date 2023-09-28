(ns musick.app
  (:require
   [goog.dom :as gdom]
   [goog.string :as gstr]
   [goog.string.format]
   [reagent.core :as r]
   ["react-dom/client" :refer [createRoot]]))

(def styles
  {:selected {:font-weight :bold}})

(defn style
  [k]
  {:style (get styles k {})})

(def notes
  [:A :A#-Bb :B :C :C#-Db :D :D#-Eb :E :F :F#-Gb :G :G#-Ab])

(def notes-seq (cycle notes))

(def scales
  {:major         [0 2 2 1 2 2 2]
   :minor         [0 2 1 2 2 1 2]
   #_#_:diminshed []
   #_#_:augmented []
   #_#_:dominant  []
   })

(defn tap [tag x]
  (println {tag x})
  x)

(defn notes-in-scale
  [root scale]
  (let [intervals        (get scales scale)
        rooted-scale-seq (drop-while #(not= %1 root) notes-seq)]
    (as->
        {:scale     []
         :scale-seq rooted-scale-seq} $
      (reduce
        (fn [{:keys [scale-seq] :as acc} interval]
          (let [next-seq  (drop interval scale-seq)
                next-note (first next-seq)
                #_#_next-seq  (drop 1 next-seq)]
            (-> acc
              (update :scale concat [next-note])
              (assoc :scale-seq next-seq))))
       $ intervals)
      (get $ :scale))))

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
  (r/with-let [note      (r/atom :C)
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
            (map-indexed (partial render-note @note set-note))))]]]
     [:div#scales.picker
      [:nav
       [:h3 "scales"]
       [:ul
        (doall
          (->> scales
            (keys)
            (map-indexed (partial render-scale @scale set-scale))))]]]
     [:div#scales.picker
      [:nav
       [:h3 (gstr/format "Notes in %s %s" (name @note) (name @scale))]
       [:ul
        (doall
          (->>
            (notes-in-scale @note @scale)
            (map-indexed (fn [idx n] [:li {:key idx} n]))))]]]]))

(defonce root (createRoot (gdom/getElement "app")))

(defn init
  "initialize root application"
  []
  (.render root (r/as-element [main])))

(defn ^:dev/after-load re-render
  []
  (init))
