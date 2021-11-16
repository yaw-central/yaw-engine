(ns yaw.multicolors
    (:require [yaw.world :as w]))

(def univ (w/start-universe!))
(def world (:world @univ))




(def mtop (w/create-mesh! world
                        :vertices     {:v0 [-1 1 -1] :v1 [-1 1 1] :v2 [1 1 1] :v3 [1 1 -1]}
                        :text-coord   {
                                          :top    [0 0 0 1 1 1 1 0]
                                         }
                        :normals      {
                                         :top    [0 1 0 0 1 0 0 1 0 0 1 0]
                                         }
                        :faces        {
                                         :top    [0 1 3 3 1 2]
                                        }
                        :weight 1
                        :rgb [0 1 0]
                        :texture-name "/resources/dice6.png"))

(def mback (w/create-mesh! world
                        :vertices     {:v0 [-1 1 -1] :v1 [-1 -1 -1] :v2 [1 -1 -1] :v3 [1 1 -1]}
                        :text-coord   { :back  [0 0 0 1 1 1 1 0]  }

                        :normals      {
                                         :back   [0 0 -1 0 0 -1 0 0 -1 0 0 -1]
                                        }
                        :faces        {
                                         :back   [3 2 0 0 2 1]
                                         }
                        :weight 1
                        :rgb [1 0 0]
                        :texture-name "/resources/dice2.png"))

(def mbottom (w/create-mesh! world
                        :vertices    {:v0 [-1 -1 -1] :v1 [-1 -1 1] :v2 [1 -1 1] :v3 [1 -1 -1]}
                        :text-coord   {:bottom  [0 0 0 1 1 1 1 0]
                                       }
                        :normals      {
                                         :bottom [0 -1 0 0 -1 0 0 -1 0 0 -1 0]
                                        }
                        :faces        {
                                         :bottom [1 0 2 2 0 3]
                                         }
                        :weight 1
                        :rgb [1 0 1]
                        :texture-name "/resources/dice1.png"))



(def mright (w/create-mesh! world
                        :vertices     {:v0 [1 1 -1] :v1 [1 -1 -1] :v2 [1 -1 1] :v3 [1 1 1]}
                        :text-coord   {:right  [0 0 0 1 1 1 1 0]       }
                        :normals      {
                                         :right  [1 0 0 1 0 0 1 0 0 1 0 0]}
                        :faces        {
                                         :right  [3 2 0 0 2 1]}
                        :weight 1
                        :rgb [1 1 1]
                        :texture-name "/resources/dice4.png"))


(def ftop (w/create-item! world "cube" :position [0 0 -2] :scale 0.3 :mesh mtop))
(def fback (w/create-item! world "cube" :position [0 0 -2] :scale 0.3 :mesh mback))
(def fbottom (w/create-item! world "cube" :position [0 0 -2] :scale 0.3 :mesh mbottom))

(def fright (w/create-item! world "cube" :position [0 0 -2] :scale 0.3 :mesh mright))

(def cube (w/new-group! world "cube"))

(w/group-add! cube "2" ftop)
(w/group-add! cube "3" fback)
(w/group-add! cube "4" fbottom)

(w/group-add! cube "6" fright)

(w/rotate! cube :x 20 :y 120 :z 0)