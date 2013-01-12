(ns ^{:doc "A simple example app of displaying a generated image in swing"
      :author "Mark Feeney"}
  graphics.basic
  (:import (javax.swing ImageIcon JFrame JLabel JPanel)
           (java.awt Color)
           (java.awt.image BufferedImage)))

(set! *warn-on-reflection* true)

;; "A global frame that we can put images on."
(defonce frame
  (doto (JFrame. "Basic graphics")
    (.setDefaultCloseOperation(JFrame/DISPOSE_ON_CLOSE))))

(defn display-image 
  "Display img on frame, replacing whatever is currently there."
  [^JFrame frame ^BufferedImage img]
  (.. frame (getContentPane) (removeAll))
  (.add frame (JLabel. (ImageIcon. img)))
  (if (not (.isVisible frame))
    (.setVisible frame true)
    (.validate frame)))


(comment

(defn make-test-image [^Color col]
  (let [img (BufferedImage. 300 200 BufferedImage/TYPE_INT_ARGB)]
    (doseq [x (range 300)
            y (range 10)]
      (.setRGB img x (+ y 30) (.getRGB col)))
    img))

(defn make-red [] (make-test-image Color/RED))
(defn make-blue [] (make-test-image Color/BLUE))
(display-image frame (make-red))
(display-image frame (make-blue))

)

