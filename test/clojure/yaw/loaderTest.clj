(ns yaw.loaderTest
    (:require [yaw.world :as w]))

(def univ (w/start-universe!))
(def world (:world @univ))

(def mcube (w/createObjMesh world "./src/clojure/resources/models/ball.obj"))

(def cube (w/create-item! world "cube" :position [0 0 -2] :scale 0.5 :mesh mcube))

(w/rotate! cube :x 30 :y 40 :z 0)