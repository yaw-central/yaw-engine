(ns yaw.ex02-gameloop
    "This is part 2 of the tutorial series demonstrating
  the basic functionalities of the Yaw engine.

  This second example introduces the gameloop, and show how
  to animate the static scene constructed in [[ex01-intro]]."

  (:require
    ;; The main user-facing namespace is `yaw.world`
   [yaw.world :as w]
   ;; We will build on the scene of the previous example
   [yaw.ex01-intro :refer [world cube-1 cube-2 cube-3]]
   )
  )

;;{
;; # The Game loop
;;
;; Starting with the static scene built in the first part of the
;; tutorial, our goal is now to animate the colored cubes by
;; applying a rotation at regular interval for each
;;
;; At the lowest level, the way to do this is to installe
;; a *callback* function that will be called upon each frame
;; refresh.
;;
;; For this, we use the function `register-update-callback!`,
;; e.g. as follows:
;;

(w/register-update-callback!
 world
 (fn [delta-time]
   (w/rotate! cube-1 :x (* 140 delta-time))
   (w/rotate! cube-2 :y (* 300 delta-time))
   (w/rotate! cube-3 :z (* -60 delta-time))))

;; {
;; To stop the animation, we just have to unregister
;; the update-callback
;;}

(comment
  (w/unregister-update-callback! world)
  )
  
;; {
;; We could now add some interactivity, such as making the
;; animation slow/faster using the -/+ keys.
;; For this, we will use the function `register-key-callback!`, but
;; first we will save the current-speed in an atom.
;;}

(def +speed-atom+ (atom {:speed 1.0}))

;; {
;; And let's use the atom's value upon frame update
;; }

(w/register-update-callback!
 world
 (fn [delta-time]
   (let [current-speed (:speed @+speed-atom+)]
     (w/rotate! cube-1 :x (* current-speed 140 delta-time))
     (w/rotate! cube-2 :y (* current-speed 300 delta-time))
     (w/rotate! cube-3 :z (* current-speed -60 delta-time)))))

;; {
;; We can "play" directly
;; }

(defn change-speed!
  [delta] (swap! +speed-atom+ #(update % :speed + delta)))


(defn faster!
  ([] (faster! 0.25))
  ([incr] (change-speed! incr)))

(defn slower!
  ([] (slower! 0.25))
  ([decr] (change-speed! (- decr))))

(comment
  ;; can evaluate multiple times
  (faster!)
  (slower!)
)

;;{
;; And now we can register our low-level key callback
;; For now we will just print the data received.
;;}

(w/register-key-callback!
 world
 (fn [keyval scancode action mode]
   (println {:keyval keyval, :scancode scancode, :action action, :mode mode})))

;;{
;; We obtain quite low-level key events.
;; On my keyboard, the '+' `keyval` is 61 and `-` has value 54
;; (unrelated to the shift status or things like that, we see the key
;; and not the character).
;;
;; We also remark that a *keypress* event is with `action` = 0 
;;
;; This is enough for our illustration.
;;}

(w/register-key-callback!
 world
 (fn [keyval _ action _]
   (when (zero? action)
     (case keyval
       ;; + key
       61 (faster!)
       ;; - key
       54 (slower!)
       ;; default
       nil))))









                             


