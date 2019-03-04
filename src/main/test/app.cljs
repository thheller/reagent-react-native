(ns test.app
  (:require
    ["react-native" :as rn]
    ["react" :as react]
    ["create-react-class" :as crc]
    [reagent.core :as r]
    ))

;; must use defonce and must refresh full app so metro can fill these in
;; at live-reload time `require` does not exist and will cause errors
;; must use path relative to :output-dir

(defonce splash-img (js/require "../assets/shadow-cljs.png"))

(def styles
  ^js (-> {:container
           {:flex 1
            :backgroundColor "#fff"
            :alignItems "center"
            :justifyContent "center"}
           :title
           {:fontWeight "bold"
            :fontSize 24
            :color "blue"}}
          (clj->js)
          (rn/StyleSheet.create)))

(defn root []
  [:> rn/View {:style (.-container styles)}
   [:> rn/Text {:style (.-title styles)} "Hello!"]
   [:> rn/Image {:source splash-img :style {:width 200 :height 200}}]])

(defonce root-ref (atom nil))
(defonce root-component-ref (atom nil))

(defn render-root [root]
  (let [first-call? (nil? @root-ref)]
    (reset! root-ref root)

    (if-not first-call?
      (when-let [root @root-component-ref]
        (.forceUpdate ^js root))
      (let [Root
            (crc
              #js {:componentDidMount
                   (fn []
                     (this-as this
                       (reset! root-component-ref this)))
                   :componentWillUnmount
                   (fn []
                     (reset! root-component-ref nil))
                   :render
                   (fn []
                     (let [body @root-ref]
                       (if (fn? body)
                         (body)
                         body)))})]

        (rn/AppRegistry.registerComponent "AwesomeProject" (fn [] Root))))))

(defn start
  {:dev/after-load true}
  []
  (render-root (r/as-element [root])))

(defn init []
  (start))

