(ns yaw.ex01-intro
  "A short introduction to the Yaw 3D engine and
  its basic functionalities.
  (see the comments)"

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
                           :g [-1  1 -1]
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



