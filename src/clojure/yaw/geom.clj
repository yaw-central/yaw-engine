(ns yaw.geom
  "Some basic geometries for Yaw.")

(defn compact-geom
  "Return a compact version of the `geom` geometry description."
  [geom]
  (let [[cverts vmap]
        (loop [verts (:vertices geom), idx 0, cverts [], vmap {}]
          (if (seq verts)
            (let [[k [x y z]] (first verts)]
              (recur (rest verts) (inc idx) (conj cverts x y z) (assoc vmap k idx)))
            [cverts vmap]))
        [ctris cnorms]
        (loop [tris (:triangles geom), ctris [], cnorms []]
          (if (seq tris)
            (let [[k [v1 v2 v3]] (first tris)]
              (recur (rest tris)
                     (conj ctris (vmap v1) (vmap v2) (vmap v3))
                     (apply (partial conj cnorms) (get (:normals geom) k))))
            [ctris cnorms]))]
    {:format ::compact,
     :verts cverts
     :tris ctris
     :norms cnorms}))

(def cube-geom
  {:vertices  {:a [1 1 1]
               :b [1 -1 1]
               :c [-1 -1 1]
               :d [-1 1 1]
               :e [1 1 -1]
               :f [1 -1 -1]
               :g [-1 1 -1]
               :h [-1 1 -1]}
   :triangles {:t1  [:a :c :b]
               :t2  [:a :d :c]
               :t3  [:e :f :h]
               :t4  [:f :g :h]
               :t5  [:a :e :h]
               :t6  [:a :h :d]
               :t7  [:b :c :f]
               :t8  [:f :c :g]
               :t9  [:a :f :e]
               :t10 [:a :b :f]
               :t11 [:d :h :c]
               :t12 [:c :h :g]}
   :normals   {:t1  [0 0 1]
               :t2  [0 0 1]
               :t3  [0 0 -1]
               :t4  [0 0 -1]
               :t5  [0 1 0]
               :t6  [0 1 0]
               :t7  [0 -1 0]
               :t8  [0 -1 0]
               :t9  [1 0 0]
               :t10 [1 0 0]
               :t11 [-1 0 0]
               :t12 [-1 0 0]}})

(comment
(defn box-geometry
  "Generate a box mesh.

  Create a low-level geometry object from a high-level simple keyword map."
  [& {:keys [anchor scale]
      :or {anchor [0 0 0]
           scale [1 1 1]}}]
  (let [v (anchor-fun anchor scale)]
    {:vertices {:a (v [1 1 1])
                :b (v [1 -1 1])
                :c (v [-1 -1 1])
                :d (v [-1 1 1])
                :e (v [1 1 -1])
                :f (v [1 -1 -1])
                :g (v [-1 -1 -1])
                :h (v [-1 1 -1])}
     :tris [{:n [0 0 1]
             :v [:a :c :b]}
            {:n [0 0 1]
             :v [:a :d :c]}
            {:n [0 0 -1]
             :v [:e :f :h]}
            {:n [0 0 -1]
             :v [:f :g :h]}
            {:n [0 1 0]
             :v [:a :e :h]}
            {:n [0 1 0]
             :v [:a :h :d]}
            {:n [0 -1 0]
             :v [:b :c :f]}
            {:n [0 -1 0]
             :v [:f :c :g]}
            {:n [1 0 0]
             :v [:a :f :e]}
            {:n [1 0 0]
             :v [:a :b :f]}
            {:n [-1 0 0]
             :v [:d :h :c]}
            {:n [-1 0 0]
             :v [:c :h :g]}]}))

(defn cuboid-geometry
  "Generate a box mesh.

  Create a low-level geometry object from a high-level simple keyword map."
  [& {:keys [anchor scale]
      :or {anchor [0 0 0]
           scale [1 1 1]}}]
  (let [v (anchor-fun anchor scale)]

    {:vertices {:a (v [1 3 1])
                :b (v [1 -3 1])
                :c (v [-1 -3 1])
                :d (v [-1 3 1])
                :e (v [1 3 -1])
                :f (v [1 -3 -1])
                :g (v [-1 -3 -1])
                :h (v [-1 3 -1])}
     :tris [{:n [0 0 1]
             :v [:a :c :b]}
            {:n [0 0 1]
             :v [:a :d :c]}
            {:n [0 0 -1]
             :v [:e :f :h]}
            {:n [0 0 -1]
             :v [:f :g :h]}
            {:n [0 1 0]
             :v [:a :e :h]}
            {:n [0 1 0]
             :v [:a :h :d]}
            {:n [0 -1 0]
             :v [:b :c :f]}
            {:n [0 -1 0]
             :v [:f :c :g]}
            {:n [1 0 0]
             :v [:a :f :e]}
            {:n [1 0 0]
             :v [:a :b :f]}
            {:n [-1 0 0]
             :v [:d :h :c]}
            {:n [-1 0 0]
             :v [:c :h :g]}]}))

(defn pyramid-geometry
  "Generate a squared-based pyramid mesh

  Create a low-level geometry object from a high-level simple keyword map."
  [& {:keys [anchor scale]
      :or {anchor [0 0 0]
           scale [1 1 1]}}]
  (let [v (anchor-fun anchor scale)
        n (Math/cos (/ Math/PI 4))]

    {:vertices {:o (v [0 0 0])
                :s (v [0 0 1])
                :a (v [1 1 0])
                :b (v [1 -1 0])
                :c (v [-1 -1 0])
                :d (v [-1 1 0])}
     :tris [{:n [0 0 -1]
             :v [:o :a :b]}
            {:n [0 0 -1]
             :v [:o :b :c]}
            {:n [0 0 -1]
             :v [:o :c :d]}
            {:n [0 0 -1]
             :v [:o :d :a]}
            {:n (v [n 0 n])
             :v [:s :b :a]}
            {:n (v [0 (- n) n])
             :v [:s :c :b]}
            {:n (v [(- n) 0 n])
             :v [:s :d :c]}
            {:n (v [0 n n])
             :v [:s :a :d]}]}))

(defn cone-geometry
  "Generate a 12-point based cone mesh

  Create a low-level geometry object from a high-level simple keyword map."
  [& {:keys [anchor scale]
      :or {anchor [0 0 0]
           scale [1 1 1]}}]
  (let [v (anchor-fun anchor scale)]

    {:vertices {:s (v [0 0 0.5])
                :o (v [0 0 -0.5])
                :a (v [0.5 0 -0.5])
                :b (v [0.46194 0.19134 -0.5])
                :c (v [0.35355 0.35355 -0.5])
                :d (v [0.19134 0.46194 -0.5])
                :e (v [0 0.5 -0.5])
                :f (v [-0.19134 0.46194 -0.5])
                :g (v [-0.35355 0.35355 -0.5])
                :h (v [-0.46194 0.19134 -0.5])
                :i (v [-0.5 0 -0.5])
                :j (v [-0.46194 -0.19134 -0.5])
                :k (v [-0.35355 -0.35355 -0.5])
                :l (v [-0.19134 -0.46194 -0.5])
                :m (v [0 -0.5 -0.5])
                :n (v [0.19134 -0.46194 -0.5])
                :p (v [0.35355 -0.35355 -0.5])
                :q (v [0.46194 -0.19134 -0.5])}
     :tris [{:n (v [0.881 0.175 0.440])
             :v [:a :b :s]}
            {:n (v [0.747 0.499 0.440])
             :v [:b :c :s]}
            {:n (v [0.499 0.747 0.440])
             :v [:c :d :s]}
            {:n (v [0.175 0.881 0.440])
             :v [:d :e :s]}
            {:n (v [-0.175 0.881 0.440])
             :v [:e :f :s]}
            {:n (v [-0.499 0.747 0.440])
             :v [:f :g :s]}
            {:n (v [-0.747 0.499 0.440])
             :v [:g :h :s]}
            {:n (v [-0.881 0.175 0.440])
             :v [:h :i :s]}
            {:n (v [-0.881 -0.175 0.440])
             :v [:i :j :s]}
            {:n (v [-0.747 -0.499 0.440])
             :v [:j :k :s]}
            {:n (v [-0.499 -0.747 0.440])
             :v [:k :l :s]}
            {:n (v [-0.175 -0.881 0.440])
             :v [:l :m :s]}
            {:n (v [0.175 -0.881 0.440])
             :v [:m :n :s]}
            {:n (v [0.499 -0.747 0.440])
             :v [:n :p :s]}
            {:n (v [0.747 -0.499 0.440])
             :v [:p :q :s]}
            {:n (v [0.881 -0.175 0.440])
             :v [:q :a :s]}
            {:n (v [0 0 -1])
             :v [:a :q :o]}
            {:n (v [0 0 -1])
             :v [:q :p :o]}
            {:n (v [0 0 -1])
             :v [:p :n :o]}
            {:n (v [0 0 -1])
             :v [:n :m :o]}
            {:n (v [0 0 -1])
             :v [:m :l :o]}
            {:n (v [0 0 -1])
             :v [:l :k :o]}
            {:n (v [0 0 -1])
             :v [:k :j :o]}
            {:n (v [0 0 -1])
             :v [:j :i :o]}
            {:n (v [0 0 -1])
             :v [:i :h :o]}
            {:n (v [0 0 -1])
             :v [:h :g :o]}
            {:n (v [0 0 -1])
             :v [:g :f :o]}
            {:n (v [0 0 -1])
             :v [:f :e :o]}
            {:n (v [0 0 -1])
             :v [:e :d :o]}
            {:n (v [0 0 -1])
             :v [:d :c :o]}
            {:n (v [0 0 -1])
             :v [:c :b :o]}
            {:n (v [0 0 -1])
             :v [:b :a :o]}]}))

)
