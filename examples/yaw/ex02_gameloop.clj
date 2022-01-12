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
  
                             


