(ns yaw.texmap
    (:require [yaw.world :as w]))

(def univ (w/start-universe!))
(def world (:world @univ))
(def sorted_vertices (into (sorted-map) {0 [-1 1 1] 1 [-1 -1 1] 2 [1 -1 1] 3 [1 1 1]        ;; front
                                         4 [1 1 -1]  5 [1 -1 -1] 6 [-1 -1 -1] 7  [-1 1 -1]   ;; back
                                         8 [-1 1 -1] 9 [-1 -1 -1] 10 [-1 -1 1] 11 [-1 1 1]   ;; left
                                         12 [1 1 1] 13 [1 -1 1] 14 [1 -1 -1] 15 [1 1 -1]     ;; right
                                         16 [-1 1 -1] 17 [-1 1 1] 18 [1 1 1] 19 [1 1 -1]     ;; top
                                         20 [-1 -1 -1] 21 [-1 -1 1] 22 [1 -1 1] 23 [1 -1 -1] ;; bottom
                                          }))
(def mcube (w/create-mesh! world
                        :vertices sorted_vertices
                        :text-coord   {:front  [0 0 0 1 1 1 1 0]
                                        :back   [0 0 0 1 1 1 1 0]
                                        :left   [0 0 0 1 1 1 1 0]
                                        :right  [0 0 0 1 1 1 1 0]
                                        :top    [0 0 0 1 1 1 1 0]
                                         :bottom [0 0 0 1 1 1 1 0]
                                         }

                        :normals      {:front  [0 0 1 0 0 1 0 0 1 0 0 1]
                                        :back   [0 0 -1 0 0 -1 0 0 -1 0 0 -1]
                                        :left   [-1 0 0 -1 0 0 -1 0 0 -1 0 0]
                                        :right  [1 0 0 1 0 0 1 0 0 1 0 0]
                                        :top    [0 1 0 0 1 0 0 1 0 0 1 0]
                                        :bottom [0 -1 0 0 -1 0 0 -1 0 0 -1 0]
                                         }


                        :faces        {:front  [0 1 3 1 3 2]
                                                          :back   [4 5 7 5 7 6]
                                                     :left   [8 9 11 9 11 10]
                                                     :right  [12 13 15 13 15 14]
                                                      :top    [16 17 19 17 19 18]
                                                           :bottom [20 21 23 21 23 22]
                                                             }
                        :weight 24
                        :rgb [0 1 1]
                        :texture-name "./src/java/resources/colorgrid.png"
                        ))

(def cube (w/create-item! world "cube" :position [0 0 -2] :scale 0.5 :mesh mcube))

(w/rotate! cube :x 30 :y 40 :z 0)
;;(w/rotate! cube :x 0 :y 60 :z 0)





