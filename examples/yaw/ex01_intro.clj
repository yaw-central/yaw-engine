(ns yaw.ex01-intro
  "This is part 1 of the tutorial series demonstrating
  the basic functionalities of the Yaw engine.

  This first example gives a concise introduction to most basic
   features of the engine, and produces a simple, static 3D scene."

  (:require
    ;;; The main user-facing namespace is `yaw.world`
    [yaw.world :as w])
  )

;;{
;; **Disclaimer**:
;;
;; Yaw is a simple 3D engine based on OpenGL (through LWJGL) developed
;; in Java but for Clojure programmers. It is important to understand
;; that the OpenGL machinery is very *imperative* in essence, and so is
;; the Yaw engine.
;;
;; The Clojure wrapper we discuss in this document is very thin, hence
;; it provides a very non-idiomatic (imperative) way of interacting
;; with the graphic card.
;; The companion project [yaw-reactive](https://github.com/yaw-central/yaw-reactive)
;; aims at providing a functional/reactive approach.

;;{
;; #  World creation
;;
;; The **world** is the main connection between the OpenGL machine (the graphic card)
;; and the user. Technically it is an instance of class [yaw.engine.World] but it
;; is better to use the top-level functions from the [yaw.world] namespace rather
;; than relying on direct Java interop.
;;
;; To create a new world, we use [create-world!] from the [yaw.world] namespace.
;;
;;}

(def world (w/create-world! :width 1024
                            :height 768
                            :vsync true))

;;{
;; Here we create a 3D world view in a window of the specified size,
;; and we activate the vertical synchronization, which is a good way
;; to program in 3D without consuming too much (GPU) resources.
;;
;; This should open a window with the specified size and
;; a black backgound and with title `"Yet Another World"`.
;;
;; While interacting with Yaw, it is best to arrange the desktop so that
;; the World window is always visible.
;;
;; Remarks:
;;   - while is seems possible to open more than one windows, and thus create multiple worlds
;;     simultaneously, this does not seem to be portable and may make things unstable.
;;     Thus, for now, multiple windows remains largely unsupported.
;;
;;   - more options will be provided in the future, such as allowing to switch to fullscreen,
;;     change the window title, etc.  (although we will *not* support all the GLFW features)
;;
;;
;; It is also possible to destroy a previously created world, abruptly.
;;}

(comment
  (w/destroy-world! world)
  )


;;{
;; # A first 3D object
;;
;; The informal notion of a *3D object* is (most often) the conflation of
;; related but distinct notions, namely:
;;   - a **geometry** describing the object form in terms of (local) 3D coordinates
;;     and related properties
;;   - a **material** describing the way the object should look
;;   - a **position** (in global coordinates) and **state** (e.g. rotation angle) in
;;     the world.
;;
;; In Yaw (as in various other engines), the precise concepts are the following ones.
;;
;; ## Geometries
;;
;; First, the geometry in Yaw is based on triangles described by
;; a set of **vertices** and a set of **triangles** with their **normals** (an
;; information required for lightning purpose)
;;
;; The namespace  [yaw.geom] contains a library of basic geometries, and it is possible
;; to import geometries (and also materials) using the functionalities of the namespace
;; [yaw.loader].
;;
;; We now describe the geometry of a Cube, as a simple Clojure map.
;;
;;    d.+------+ a
;;   .' |    .'|
;; h+---+--+'e |
;;  |   |  |   |
;;  | c,+--+---+ b
;;  |.'    | .'
;; g+------+'f
;;}

;; extracted from the [yaw.geom] namespace
(def cube-geom {:vertices {:a [ 1  1  1]
                           :b [ 1 -1  1]
                           :c [-1 -1  1]
                           :d [-1  1  1]
                           :e [ 1  1 -1]
                           :f [ 1 -1 -1]
                           :g [-1 -1 -1]
                           :h [-1  1 -1]
                           }
                :triangles {:t1 [:a :c :b]
                            :t2 [:a :d :c]
                            :t3 [:e :f :h]
                            :t4 [:f :g :h]
                            :t5 [:a :e :h]
                            :t6 [:a :h :d]
                            :t7 [:b :c :f]
                            :t8 [:f :c :g]
                            :t9 [:a :f :e]
                            :t10 [:a :b :f]
                            :t11 [:d :h :c]
                            :t12 [:c :h :g]}
                :normals {:t1 [ 0  0  1]
                          :t2 [ 0  0  1]
                          :t3 [ 0  0 -1]
                          :t4 [ 0  0 -1]
                          :t5 [ 0  1  0]
                          :t6 [ 0  1  0]
                          :t7 [ 0 -1  0]
                          :t8 [ 0 -1  0]
                          :t9 [ 1  0  0]
                          :t10 [ 1  0  0]
                          :t11 [-1  0  0]
                          :t12 [-1  0  0]}})

;;{
;; There exists a more compact representation of such geometries,
;; which would be e.g. as follows:
;;}

(def cube-geom-compact
  {:format :yaw.geom/compact
   :verts [1 1 1 1 -1 1 -1 -1 1 -1 1 1 1 1 -1 1 -1 -1 -1 1 -1 -1 1 -1]
   :tris [5 2 6 5 6 7 0 7 3 0 3 2 0 5 4 2 7 6 0 1 5 4 5 7 1 2 5 0 2 1 3 7 2 0 4 7]
   :norms [0 -1 0 0 0 -1 0 1 0 0 0 1 1 0 0 -1 0 0 1 0 0 0 0 -1 0 -1 0 0 0 1 -1 0 0 0 1 0]})

;;{
;; We remark that these ways of describing geometries is *not* user-friendly
;; (especially the compact form).
;; Thus, most geometries should be described by generating programs, or
;; imported from an external representation.
;;
;; Predefined geometries from [yaw.geom] are demonstrated in another
;; example program (cf. the [[geometry]] example).
;; Also, other geometric informations (e.g. texture coordinates)
;; are discussed in yet another example program (cf. the [[texture]] example).
;;}

;;{
;; ## Meshes
;;
;; A **mesh** is the combination of a geometry and a **material**.
;; An object in the real world is made of some material with observable and physical
;; properties: woord, metal, liquid, etc.  In a simulated world things are
;; much simpler. For the Yaw engine, a material is composed out of:
;;
;; - a solid color together with properties related to lighting
;; - or, alternatively, a more complex material defined with textures and 2D images
;;
;; More material options might be proposed in the future: procedural textures,
;; animated materials, bump mapping, etc.
;;
;; For this introduction we will use a simple color,
;; described as a [red, green, blue] vector.
;;
;; Here we define materials for the three basic colors.
;;}

(def solid-red {:color [1 0 0]})
(def solid-green {:color [0 1 0]})
(def solid-blue {:color [0 0 1]})

;;{
;;
;; The [[create-mesh]] function is used to create ... a mesh ... from a geometry
;; and some material options.
;;
;;}

(def blue-cube (w/create-mesh! world cube-geom solid-blue))

;;{
;;
;; as you can see on the World view, nothing happens... we just
;; created a mesh and put on the graphic card the required data (vertices, etc.)
;;
;;}

;;{
;; # Items
;;
;; To put a mesh, e.g. our blue cube, in the real world, we must explain
;; where to place it, i.e. we must give its *position*.
;; For this, Yaw uses the concept of an *item* which is, as a first approximation,
;; the association of a mesh together with global coordinates.
;;
;;}

(def cube-1 (w/create-item! world blue-cube {:position [0 0 -5]}))

;;{
;; The call to this function is of the form:
;;
;;     (create-item! <world> <mesh> <props>)
;;
;; with `<props>` a map of properties, in particular the property
;; `:location` taking a 3-dimensional vector. In the example,
;; the blue cube is put at the origin of the `world` and translated
;; in the z coordinate (the depth) at 5 units.
;; (if we do not do that, then the world window will be entirely blue,
;; because the default camera is already at the origin).
;;
;;}

;;{
;; It is possible to apply transformations to items, using
;; e.g. the following functions;
;;}

(w/rotate! cube-1 :y 30)

;;{
;;
;; for rotation we can specify the rotation angle (in degrees) for
;; the three axes (`:x`, `:y`, `:z`), defaulting to 0
;;
;;}

(w/translate! cube-1 :z -1)

;;{
;;
;; for translation what we specify the translation along each axis
;; (also defaulting to 0)
;;
;; Do not hesitate to call these functions multiple times, changing the
;; specified values to see what happens.
;;
;;}

;;{
;;
;; Of course, it is possible to remove an item from the world.
;;
;;}

(comment
  (w/remove-item! world cube-1)
  )

;;{
;; # Cameras
;;
;; What we see in the world (for the moment, a cube) is through the lens
;; of a **camera**.
;; The Yaw engine supports multiple cameras but in this introduction
;; we will only discuss the *default* camera.
;;
;; The default Camera is put a the origin of the world, i.e. [0 0 0]
;; We can fetch the activate camera as follows:
;;
;;}

(def cam (w/active-camera world))

;;{
;; As it is the case for items, we can translate or rotate the camera. 
;;}

(w/translate! cam :z 1)

;;{
;;
;; The camera has been displaced in one unit in the z direction,
;; which makes the whole scene appear smaller.
;; To observe this more finally, let's add another cube in the scene.
;;
;;}

(def red-cube (w/create-mesh! world cube-geom solid-red))

(def cube-2 (w/create-item! world red-cube {:position [-2.5 0 -5]}))

(def green-cube (w/create-mesh! world cube-geom solid-green))

(def cube-3 (w/create-item! world green-cube {:position [3 0 -6]}))


;;{
;;
;; Now let's move the camera further back one unit
;;
;;}

(w/translate! cam :z 1)

;;{
;;
;; We see that the two cubes "move" (although only the camera actually
;; moves here.
;; Rotations are also possible of course.
;;
;;}

(w/rotate! cam :y 30 :z 20)

;;{
;;
;; We know that the red cube is at `[-2.5 0 -5]`, we can
;; orient the camera towards given coordinates using the
;; following function.
;;
;;}

(w/camera-orient! cam [-2.5 0 -5])
;; XXX: shouldn't it be pointing to the red cube ?

;;{
;; # Lights
;;
;; The cubes in our current scene do not really appear
;; as 3d objects. The main reason is that all their pixel
;; have exactly the same color (either "full" blue or red).
;;
;; To obtain a (slightly) more realistic view, we must introduce
;; some (more) light into the scene.
;;
;; The Yaw engine supports a variety of different lights:
;;   - an **ambient light** that correspond to a kind of basis for lighting
;;   - (at most) a **directional light** that emit a kind of "inifinite" light,
;;     simulating a kind of a sun (or moon)
;;   - **point lights** that emit in all directions
;;   - **spot lights** that emit light directionally, according to a specified cone
;;
;; Note: because lighting is compute intensive, the number of point lights and spot lights
;; is limited to a low number, by default 5 of each  (although this can be changed
;; after recompiling the engine).
;;
;; In this introduction, we will only discuss the first three kinds of lights,
;; spot lights, more complex,  are explaind in a separate tutorial example.
;;}

;;{
;; ## Ambient light
;;
;; The ambient light is what we see in our current scene.
;; We can see that by changing its components.
;;}

(def amb (w/ambient-light world))

(w/intensity! amb 0.5)

;; A value of 0 makes the scene all dark

(w/intensity! amb 0.0)

;; And of course a value of 1.0 restore the default
;; (maximal) level.

(w/intensity! amb 1.0)

;;{
;; ## Directional light
;;
;; Each world also possesses a single directional light, simulating
;; the presence of a sun or moon. This light source is located
;; at an "infinite" distance and all its rays are considered parallel,
;; according to a given direction.
;;
;;}

(def sun (w/directional-light world))

(w/intensity! sun 1.0)

;;{
;; To see something, somehow, we must reduce the
;; intensity of the ambient light.
;;}

(w/intensity! amb 0.7)

;;{
;; We can also change the direction of the light/sun
;; as a vectory [dx dy dz]
;;}

(w/direction! sun [-0.5 0.5 -2])

;;{
;; ## Point lights
;;
;; A point light, as its name implies, is located at a given coordinates
;; and emits in all directions.
;; Let's put a point light a light bit in front of the cubes, and slightly
;; on the right and above.
;;
;;}

(def plight (w/point-light! world 0 {:position [0.5 -1.5 -6]}))

;; Intensity can be beyond 1.0   (XXX: ?)
(w/intensity! plight 3.0)

;; To see something it is better to reduce the direction light
(w/intensity! sun 0.5)

;; Let's finalize our 3D scene...
(w/rotate! cube-3 :z 30)
(w/rotate! cube-2 :y 30)
(w/rotate! cube-1 :x 30)

;;{
;;
;; We have now the basic components of a 3D scene, and this is enough for
;; an introduction. In the next tutorial we will discover other basic
;; geometries, and install an *interactive loop* to play with the Yaw engine.
;;}

