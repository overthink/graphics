(ns ^{:doc "A simple app for displaying a generated image in swing."
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

;; Useful in colour generating functions
(defonce prng (java.util.Random.))
(defn rnd [] (.nextFloat prng))

(defn display-image
  "Display img on frame, replacing whatever is currently there."
  [^JFrame frame ^BufferedImage img]
  (.. frame (getContentPane) (removeAll))
  (.add frame (JLabel. (ImageIcon. img)))
  (if (not (.isVisible frame))
    (.setVisible frame true)
    (.validate frame)))

(defn make-image
  "f is a function of x, y that returns [r g b].  All of x, y, r, g, b are
  expected to be in the interval [0, 1].  Width and height specify the size in
  pixels of the output image."
  [width height f]
  (let [img (BufferedImage. width height BufferedImage/TYPE_INT_RGB)]
    (doseq [x (range width)
            y (range height)]
      (let [scaledx (/ (float x) width)
            scaledy (/ (float y) height)
            [r g b] (f scaledx scaledy)
            col (Color. (float r) (float g) (float b))]
        (.setRGB img x y (.getRGB col))))
    img))

(comment

  (defn show [f] (display-image frame (make-image 400 400 f)))

  ;; Simple gradient
  (defn f1 [x y] [x y 0])
  (show f1)

  ;; Random noise, tinted
  (defn f2 [x y] [(* 0.3 (rnd)) (rnd) (rnd)])
  (show f2)

  ;; sin(x)
  (defn sin [x y]
    (let [sinx (Math/sin x)
          delta (Math/abs (- sinx y))]
      (if (< delta 0.005)
        [0 0 0]
        [1 1 1])))
  (show sin)

)

