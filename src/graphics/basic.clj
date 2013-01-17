(ns ^{:doc "A simple app for displaying a generated image in swing."
      :author "Mark Feeney"}
  graphics.basic
  (:import (javax.swing ImageIcon JFrame JLabel JPanel)
           (java.awt Color RenderingHints)
           (java.awt.image BufferedImage)))

(set! *warn-on-reflection* true)

;; "A global frame that we can put images on."
(defonce frame
  (doto (JFrame. "Basic graphics")
    (.setDefaultCloseOperation(JFrame/DISPOSE_ON_CLOSE))))

;; Useful in colour generating functions
(defonce prng (java.util.Random.))
(defn rnd [] (.nextFloat ^java.util.Random prng))

(defn display-image
  "Display img on frame, replacing whatever is currently there."
  [^JFrame frame ^BufferedImage img]
  (.. frame (getContentPane) (removeAll))
  (.add frame (JLabel. (ImageIcon. img)))
  (if (not (.isVisible frame))
    (.setVisible frame true)
    (.validate frame)))

(defn resize-image
  "Return img resized to width w and height h.  Favours image quality."
  [img w h]
  (let [resized (BufferedImage. w h BufferedImage/TYPE_INT_RGB)
        g2 (.createGraphics resized)]
    (.setRenderingHint g2
                       RenderingHints/KEY_INTERPOLATION
                       RenderingHints/VALUE_INTERPOLATION_BILINEAR)
    (.drawImage g2 img 0 0 w h nil)
    (.dispose g2)
    resized))

(defn make-image
  "f is a function of x, y that returns [r g b].  All of x, y, r, g, b are
  expected to be in the interval [0, 1].  Width and height specify the size in
  pixels of the output image."
  [width height aafactor f]
  (let [w (* aafactor width)
        h (* aafactor height)
        xscale 1
        yscale 1
        img (BufferedImage. w h BufferedImage/TYPE_INT_RGB)]
    (doseq [x (range w)
            y (range h)]
      (let [scaledx (* xscale (/ (float x) w))
            scaledy (* yscale (/ (float y) h))
            [r g b] (f scaledx scaledy)
            col (Color. (float r) (float g) (float b))]
        (.setRGB img x y (.getRGB col))))
    (if (= 1 aafactor)
      img
      (resize-image img width height))))

(defn mandel
  "Mandelbrot set. http://en.wikipedia.org/wiki/Mandelbrot_set#For_programmers"
  [x y]
  (let [max-iters 1000
        x (float x) ;; 20x speedup from forcing these to primitives
        y (float y)
        iters (loop [curx x cury y iter 0]
                (if (and (< (+ (* curx curx) (* cury cury)) 4)
                         (< iter max-iters))
                  (recur (+ (- (* curx curx) (* cury cury)) x)
                         (+ y (* 2 (* x y)))
                         (inc iter))
                  iter))]
    ;; If it took max-iters, it's a member of the set, else colour the pixel
    ;; based on the number of iters before escape.
    (if (= iters max-iters)
      [0 0 0]
      [0 0 (min 1 (/ iters 15))])))

(comment

  (defn show [f] (display-image frame (make-image 300 300 2 f)))

  ;; Simple gradient
  (defn f1 [x y] [x y 0])
  (show f1)

  ;; Random noise, tinted
  (defn f2 [x y] [(* 0.3 (rnd)) (rnd) (rnd)])
  (show f2)

  ;; sin(x)
  (defn sin [x y]
    (let [sinx (* 0.5 (+ 1.0 (Math/sin x)))
          delta (Math/abs (- sinx y))]
      (if (< delta 0.01)
        [0 0 0]
        [1 1 1])))
  (show sin)

  (defn f3 [x y] [x y 0.5])
  (show f3)

  (defn f4 [x y] [(Math/sin x) (Math/sin y) (Math/sin (+ x y))])
  (show f4)

)

