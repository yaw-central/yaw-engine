(ns yaw.world
  "This is the main Clojure wrapper for the 3D engine.

  Most of the functions in this namespace have side-effects,
 providing only a think wrapper for the corresponding functionalities
implemented in Java/LLWJGL.

  As such, using this namespace directly provides a very imperative,
 and non-idiomatic, way of doing things.  It is better to use more
functional (e.g. FRP) approaches, such as using the companion
framework: Yaw-reactive.
"
  (:import (yaw.engine World
                       SceneRenderer
                       InputCallback)
           (yaw.engine.light AmbientLight DirectionalLight PointLight SpotLight LightModel)
           (yaw.engine.camera Camera))
  
  (:require [yaw.util :as u]
            [yaw.geom :as geom]
            [yaw.loader]))

;;; ==========================================================================
;;; World management
;;; ==========================================================================

(defn create-world!
  "Create a 3D world view.
  
  Options include:
  
  :width <size>    => the width of the view window (in pixels)
  :height <size>   => the height of the view window (in pixels)
  :x <pos>         => the x position of the window (in pixels, 0 is leftmost)
  :y <pos>         => the y position of the window (in pixels, 0 is topmost)
  :vsync <bool>    => wether vertical synchronization should be enabled (true by default)
  
  The world is the direct connection with the OpenGL state-machine,
  and should be interacted with with great care.

  If successful, the world instance is returned.
  "
  [& {:keys [width height x y vsync]
      :or   {x      0
             y      0
             width  800
             height 600
             vsync true}}]
  (let [world (World. x y width height vsync)
        lights (LightModel.)
        scene (SceneRenderer. lights)]
    (.installScene world scene)
    (.launchAsync world)
    world))

(defn destroy-world!
  "Terminates the specified [world] and claim all the related resources."
  [world]
  (.terminate world))

;;; ==========================================================================
;;; Mesh management
;;; ==========================================================================

(defn create-mesh!
  [world geom material]
  (let [cgeom (if (= (get geom :format) :yaw.geom/compact)
                geom
                (geom/compact-geom geom))]
    (.createMesh world
                 (float-array (:verts cgeom))
                 nil ;; XXX: for now, no texture support
                 (float-array (:norms cgeom))
                 (int-array (:tris cgeom))
                 1 ;; XXX: weight should probably disappear
                 (float-array (:color material))
                 "" ;; XXX : for now, no texture support
                 )))

;;; ==========================================================================
;;; Items and groups
;;; ==========================================================================

(defn create-item!
  "Create an item in the `world` with the
  specified `id` (optional),  `mesh` and `properties` (
   a map with e.g. `:position` vector, etc.}"
  ([world mesh props] (create-item! world (gensym "item-") mesh props))
  ([world id mesh props]
   (let [[px py pz] (get props :position [0 0 0])
         scale (get props :scale 1)]
     (.createItemObject world (str id) px py pz scale mesh))))

(defn remove-item!
  "Remove the specified `item` from the `world`"
  [world item]
  (.removeItem world item))

(defn rotate!
  "Applies a rotation to the specified `item`, with
  rotation angles expressed through keyword arguments
  `:x`, `:y` and `:z`"
  [item & {:keys [x y z]
           :or   {x 0
                  y 0
                  z 0}}]
  (.rotateXYZ item x y z))

(defn translate!
  "Applies a translation to the specified `item`, with
  translation vector expressed through keyword arguments
  `:x`, `:y` and `:z`"
  [item & {:keys [x y z]
           :or   {x 0
                  y 0
                  z 0}}]
  (.translate item x y z))

;;; ==========================================================================
;;; Camera management
;;; ==========================================================================

(defn active-camera
  "Get the currently active camera of the specified `world`."
  [world]
  (.getCamera world))

(defn camera-look-at!
  "Orient the camera towards the specified coordinates."
  ([camera [x y z] [ux uy uz]]
   (.lookAt camera x y z ux uy uz))
  ([camera [x y z]]
   (.lookAt camera x y z 0 1.0 0)))


;;; ==========================================================================
;;; Light management
;;; ==========================================================================

(defn ambient-light
  "Get the ambient light of the specified `world`."
  [world]
  (.getAmbientLight (.getSceneLight world)))

(defn intensity!
  "Change intensity of specified `light` to value `i`
  (a float between 0 and 1.0"
  [light i]
  (.setIntensity light i))

(defn directional-light
  "Get the directional light of the specified `world`."
  [world]
  (.getSun (.getSceneLight world)))

(defn direction!
  "Change the direction of the specified (directional `light`)
according to the specified vector `dir` [dx dy dz]"
  [light dir]
  (let [[dx dy dz] dir]
    (.setDirection light dx dy dz)))

(defn point-light!
  "Configure the `ref`-th point light in the `world` according
  to the specified `props` map.
  <TODO>: explain the point-light configuration
  "
  [world ref props]
  (let [color (get props :color [1 1 1])
        [r g b] color
        position (get props :position [0 0 0])
        [px py pz] position
        intensity (get props :intensity 1.0)
        attenuation (get props :attenuation [0.3 0.5 0.9])
        [const lin quad] attenuation]
    (let [plight (PointLight. r g b px py pz intensity const lin quad)]
      (.setPointLight (.getSceneLight world) plight ref)
      plight)))


;;; ==========================================================================
;;; Callbacks
;;; ==========================================================================

(defn register-update-callback!
  "Registers the function `cb` as a frame update callback for the
  specified `world`.
  
  The `cb` function takes a single parameter, the `delta-time` in
  millisecond (a double floating point number) the time difference
  between the current and the previous update.  

  Note that there is at most one frame update callback registered,
   more complex mechanism (e.g. event systems) must be built on the
  clojure side using this single entry point."
  [world cb]
  (.registerUpdateCallback world
   (proxy [yaw.engine.UpdateCallback] []
       (update [delta-time]
               (cb delta-time)))))

(defn unregister-update-callback!
  "Unregister the frame update callback for the specified `world`."
  [world]
  (.registerUpdateCallback world nil))

;;{
;; Register a callback to a given world
;; When a keyboard inputs is detected, the callback is called
;; The callback takes a key, a scancode, an action and a mode in its parameters
;;}
;; To remove since the way the keyboard is handled has changed
(defn register-key-callback! 
  "Register the input callback for low-level keyboard management."
  [world callback]
  (let [cb (reify InputCallback
             (sendKey [this key scancode action mode]
               ;; (println "key event! key=" key " scancode=" scancode "action=" action "mode=" mode)
               (callback key scancode action mode)))]
    ;; (println "[input callback] cb=" cb "world=" world)
    (.registerInputCallback world cb)))


;; TODO
;; (defn unregister-input-callback!
;;   "Unregister the current input callback (if any)"
;;   [world]
;;   )

;;; =========================
;;; Old API below


(comment
(defn create-mesh!
  "Create an item in the `world` with the  specified id, position, mesh"
  [world & {:keys [vertices text-coord normals faces weight rgb texture-name]
            :or   {texture-name ""
                   rgb          [0 0 1]
                   weight       1
                   vertices     {:v0 [-1 1 1] :v1 [-1 -1 1] :v2 [1 -1 1] :v3 [1 1 1]
                                 :v4 [-1 1 -1] :v5 [1 1 -1] :v6 [-1 -1 -1] :v7 [1 -1 -1]}
                   normals      {:front  [0 0 1 0 0 1 0 0 1 0 0 1]
                                 :top    [0 1 0 0 1 0 0 1 0 0 1 0]
                                 :back   [0 0 -1 0 0 -1 0 0 -1 0 0 -1]
                                 :bottom [0 -1 0 0 -1 0 0 -1 0 0 -1 0]
                                 :left   [-1 0 0 -1 0 0 -1 0 0 -1 0 0]
                                 :right  [1 0 0 1 0 0 1 0 0 1 0 0]}
                   faces        {:front  [0 1 3 3 1 2]
                                 :top    [4 0 3 5 4 3]
                                 :back   [7 6 4 7 4 5]
                                 :bottom [2 1 6 2 6 7]
                                 :left   [6 1 0 6 0 4]
                                 :right  [3 2 7 5 3 7]}
                   text-coord   {:front  [0 0 0 0.5 0.5 0.5 0.5 0]
                                 :back   [0 0 0.5 0 0 0.5 0.5 0.5]
                                 :top    [0 0.5 0.5 0.5 0 1 0.5 1]
                                 :right  [0 0 0 0.5]
                                 :left   [0.5 0 0.5 0.5]
                                 :bottom [0.5 0 1 0 0.5 0.5 1 0.5]}}}]

  (.createMesh world
               (float-array (u/flat-map vertices))
               (float-array (u/flat-map text-coord))
               (float-array (u/flat-map normals))
               (int-array (u/flat-map faces))
               (int weight) (float-array rgb) texture-name))


;;CALLBACKS---------------------------------------------------------------



;;Since we completely destroy the old architecture we will migrate the basic method to this module
;;README ONLY USE WORLD IT is A FACADE, no DIRECT USE OF MANAGEMENT/BUILDER TOOLS

;; MeshOld Functions------------------------------------------------
;; TODO: delete this when `create-simple-mesh` is stable (and we can handle texture)




(defn create-mesh!
  "Create an item in the `world` with the  specified id, position, mesh"
  [world & {:keys [vertices text-coord normals faces weight rgb texture-name]
            :or   {texture-name ""
                   rgb          [0 0 1]
                   weight       1
                   vertices     {:v0 [-1 1 1] :v1 [-1 -1 1] :v2 [1 -1 1] :v3 [1 1 1]
                                 :v4 [-1 1 -1] :v5 [1 1 -1] :v6 [-1 -1 -1] :v7 [1 -1 -1]}
                   normals      {:front  [0 0 1 0 0 1 0 0 1 0 0 1]
                                 :top    [0 1 0 0 1 0 0 1 0 0 1 0]
                                 :back   [0 0 -1 0 0 -1 0 0 -1 0 0 -1]
                                 :bottom [0 -1 0 0 -1 0 0 -1 0 0 -1 0]
                                 :left   [-1 0 0 -1 0 0 -1 0 0 -1 0 0]
                                 :right  [1 0 0 1 0 0 1 0 0 1 0 0]}
                   faces        {:front  [0 1 3 3 1 2]
                                 :top    [4 0 3 5 4 3]
                                 :back   [7 6 4 7 4 5]
                                 :bottom [2 1 6 2 6 7]
                                 :left   [6 1 0 6 0 4]
                                 :right  [3 2 7 5 3 7]}
                   text-coord   {:front  [0 0 0 0.5 0.5 0.5 0.5 0]
                                 :back   [0 0 0.5 0 0 0.5 0.5 0.5]
                                 :top    [0 0.5 0.5 0.5 0 1 0.5 1]
                                 :right  [0 0 0 0.5]
                                 :left   [0.5 0 0.5 0.5]
                                 :bottom [0.5 0 1 0 0.5 0.5 1 0.5]}}}]

  (.createMesh world
               (float-array (u/flat-map vertices))
               (float-array (u/flat-map text-coord))
               (float-array (u/flat-map normals))
               (int-array (u/flat-map faces))
               (int weight) (float-array rgb) texture-name))

  (defn createObjMesh
      [world objFilename]
      (let [model (yaw.loader/load-model objFilename)
            texture-name (if (clojure.string/blank? (:texture-name model))
                            ""
                            (str "/resources/" (:texture-name model)))]
          (.createMesh world
                     (float-array (flatten (:vertices model)))
                     (float-array (u/flat-map (:text_coord model)))
                     (float-array (u/flat-map (:normals model)))
                     (int-array (u/flat-map (:faces model)))
                     (int 0) (float-array (first (:rgb model))) texture-name)))


;;this function should not be used in yaw-react if you create meshes via the OBJ loader
;; (because these meshes do not contain a geometry with tris and vertices)
(defn create-simple-mesh!
  "Create an item in the `world` from the specified mesh object"
  [world & {:keys [geometry rgb]
            :or {geometry (yaw.mesh/box-geometry)
                 rgb [0 0 1]}}]
  (let [{:keys [vertices tris]} geometry
        vidx (zipmap (keys vertices) (range (count vertices)))
        coords (float-array (mapcat second vertices))
        normals (float-array (mapcat (fn [{n :n _ :v}] (concat n n n)) tris))
        indices (int-array (mapcat (fn [{_ :n v :v}] (map #(% vidx) v)) tris))]
    (.createMesh world coords normals indices (float-array rgb))))

;; Items Functions------------------------------------------------
(defn create-item!
  "Create an item in the `world` with the
  specified id, position, mesh"
  [world id & {:keys [position scale mesh]
               :or   {position [0 0 2]
                      scale    1
                      mesh     nil}}]         ;;error here
  (.createItemObject world id (position 0) (position 1) (position 2) scale (or mesh
                                                                               (create-mesh! world))))

(defn load-item!
  "Load an item (in a .obj file) in the `world` 
   with the position, weight ans scale of the mesh"
  [world file & {:keys [position weight scale]
                 :or {position [0 0 -5]
                      weight 1
                      scale 1}}]
  (let [model (yaw.loader/load-model file)
        mesh (.createMesh world
                          (float-array (u/flat-map (into (sorted-map) (get model :vertices))))
                          (float-array (u/flat-map (into (sorted-map) (get model :text_coord))))
                          (float-array (u/flat-map (into (sorted-map) (get model :normals))))
                          (int-array (u/flat-map (into (sorted-map) (get model :faces))))
                          (int weight)
                          (float-array (get model :rbg))
                          (get model :texture-name))]
    (.createItemObject world (str (gensym "item-")) (position 0) (position 1) (position 2) scale mesh)))
                  

;(create-mesh! world (get model :vertices) (get model :text_coord) (get model :normals)
;                               (get model :faces) weight (get model :rbg) (cast String (get model :texture-name))))))
;=> ERROR: Execution error (IllegalArgumentException) at yaw.world/create-mesh! (world.clj:75). No value supplied for key: Material




(defn set-item-color!
  "Replaces the material of the item with the specified color"
  [item [r g b]]
  (.setColor item r g b))

(defn create-block!
  "Create a rectangular block in the `world` with the
  specified id, position, color"
  [world & {:keys [id position scale color texture]
            :or   {texture  ""
                   color    [0 0 1]
                   scale    1
                   position [0 0 -2]
                   id (str (gensym "block-"))}}]
  (create-item! world id
                :position position
                :scale scale
                :mesh (create-simple-mesh! world :rgb color)))

(defn create-pyramid!
  "Create a rectangular block in the `world` with the
  specified id, position, color"
  [world & {:keys [id position scale color texture]
            :or   {texture  ""
                   color    [0 0 1]
                   scale    1
                   position [0 0 -2]
                   id (str (gensym "pyra-"))}}]
  (create-item! world id
                :position position
                :scale scale
                :mesh (create-simple-mesh! world :rgb color :geometry (yaw.mesh/pyramid-geometry))))

;;CAMERA MANAGEMENT------------------------------------------------
(defn cameras "Retrieve the list of cameras of the world" [world] (.getCamerasList world))

(defn camera "Retrieve the main camera of the world" [world] (.getCamera world))

(defn clear-cameras! "Remove all the cameras from the `world`" [world] (.clearCameras world))

(defn add-camera!
  "Add a camera to the `world`"
  ([world idx camera]
   (.addCamera world idx camera))
  ([world camera]
   (.add (cameras world) camera)))

(defn set-camera!
  "Set the current camera of the `world`"
  [world camera]
  (.setCamera world camera))

(defn create-camera!
  "Create a camera with the given parameters"
  [{:keys [fov near far pos target]
    :or {fov 60 near 0.1 far 1000.0 pos [5 5 5] target [0 0 0]}}]
  (println fov near far pos target)
  (let [[px py pz] pos
        [ox oy oz] target]
    (Camera. (float (Math/toRadians fov)) (float near) (float far)
             (float px) (float py) (float pz)
             (float ox) (float oy) (float oz))))

(defn set-camera-target!
  "Sets the target of a camera"
  [camera [x y z]]
  (.setOrientation camera x y z))

(defn set-camera-fov!
  "Sets the fov of a camera"
  [camera fov]
  (.setFieldOfView camera (Math/toRadians fov)))

;;LIGHT------------------------------------------------------------
(defn lights "Retrieve the lighting settings of the world scene" [world] (.getSceneLight world))


(defn create-sun-light!
  [{:keys [color i dir]
    :or {color [1 1 1]
         i 0.6
         dir [-1 -1 -1]}}]
  (let [[r g b] color
        [dx dy dz] dir]
    (DirectionalLight. r g b i dx dy dz)))



(defn create-spot-light!
  [ {{:keys [const lin quad] :or {const 0.3 lin 0.5 quad 0.9}} :att
     :keys [color i position direction angle]
     :or {color [1 1 1]
          i 1
          position [0 0 0]
          direction [0 0 -1]
          angle 30}}]
  (let [[r g b] color [px py pz] position [dx dy dz] direction]
    (SpotLight. r g b px py pz i const lin quad dx dy dz angle)))

(defn set-ambient-light!
  "Set the ambient light of the world"
  [world l]
    (.setAmbient (lights world) l))

(defn set-sun!
  "Set the sun of the world"
  [world l]
    (.setSun (lights world) l))

(defn set-point-light!
  "Set the `n`th pointlight with the given `color`, `position`, `itensity`, and attenuation factors"
  [world n l]
    (.setPointLight (lights world) l n))

(defn set-spot-light!
  "Set the `n`th spotlight with the given `color`, `intensity`, `position`, `direction` and attenuation factors"
  [world n l]
    (.setSpotLight (lights world) l n))

;;COLLISIONS------------------------------------------------------

(defn create-hitbox!
  "Create a hitbox in the `world` with the
  specified id, position, length, scale"
  [world id & {:keys [position length scale is-visible]
               :or   {position [0 0 -2]
                      length   [1 1 1]
                      scale 1
                      is-visible true}}]
  (.createHitBox world
                 (str id)
                 (get position 0) (get position 1) (get position 2)
                 scale
                 (get length 0) (get length 1) (get length 2)
                 is-visible))

(defn check-collision!
  "Check if 2 hitboxes are in collision in the `world`"
  [world hitbox1 hitbox2]
  (.isInCollision world hitbox1 hitbox2))

(defn fetch-hitbox!
  "Fetch and return the hitbox of the given a `group` and its `id`"
  [group id]
  (.fetchHitBox group (str id)))

;;SKYBOX MANAGEMENT---------------------------------------------------
(defn skybox "Retrieve the skybox of the world" [world] (.getSkybox world))

(defn set-skybox!
  "Create a flat-colored skybox for the `world` with the
  specified scale and color"
  [world & {[w l h] :scale
            [r g b] :color
            :or {w 1000 l 1000 h 1000
                 r 0 g 0 b 0}}]
  (.setSkybox world w l h r g b))

(defn clear-skybox!
  "Remove the current skybox from the `world`"
  [world]
  (.removeSkybox world))

;;GROUP MANAGEMENT---------------------------------------------------------
(defn groups "Retrieve the groups of the `world`"
  [world]
  (into [] (.getItemGroupArrayList world)))

(defn new-group!
  "Create and return a new group in the `world` with the given `id`"
  [world id]
  (.createGroup world (str id)))

(defn group-add!
  "Add the specified item or hitbox with the given the `id` to the `group`."
  [group id item]
  (.add group (str id) item))

(defn remove-group!
  "Remove the specified group from the `world`"
  [world group]
  (.removeGroup world group))

;; Item/camera Manipulation ------------------------------------------------


)
