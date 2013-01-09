(ns graphics.basic
  (:import (javax.swing JFrame JPanel)))

(defonce draw-fn (atom nil))

(defn draw [g]
  (.drawLine g 10 10 20 20))

(reset! draw-fn draw)

(defn make-panel []
  (proxy [JPanel] []
    (paintComponent [g]
      (proxy-super paintComponent g)
      (@draw-fn g))))

(defn make-frame []
  (doto (JFrame. "Basic graphics example")
    (.setSize 500 500)
    (.setContentPane (make-panel))))

(defn test []
  (doto (make-frame) (.pack) (.setVisible true)))
