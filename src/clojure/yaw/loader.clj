(ns yaw.loader
  "A loader for (simple) OBJ/MTL 3D mesh files"
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

;---------------------------------------
;-------------- STRUCTURE --------------
;---------------------------------------
(def ^:private model
  {:filename ""
   :texture-name ""
   :rgb []
   :vertices {}
   :normals {}
   :faces {}
   :text_coord {}})

;---------------------------------------
;-------------- FUNCTIONS --------------
;---------------------------------------
(defn- add-to-model
  "Add a value in the model"
  ([model key val]
   (case key
     :rgb (assoc model key (conj (get model key) val))
     (assoc model key val)))
  ([model k1 k2 val]
   (assoc model k1 (assoc (get model k1) k2 val)))) ;;For the maps of vertices, normals, faces and text_coord

;------------- HANDLE OBJ --------------
(defn- handle-v
  "Add to the model the node in vertices"
  [model x y z]
  (let [nb (count (get model :vertices))]
    (add-to-model model :vertices nb [(Float/parseFloat x)
                                      (Float/parseFloat y)
                                      (Float/parseFloat z)])))

(defn- handle-vn
  "Add to the model the normal in normals"
  [model x y z]
  (let [nb (count (get model :normals))]
    (add-to-model model :normals nb [(Float/parseFloat x)
                                     (Float/parseFloat y)
                                     (Float/parseFloat z)])))

(defn- handle-vt
  "Add to the model the texture coordinates in text_coord"
  [model u v]
  (let [nb (count (get model :text_coord))]
    (add-to-model model :text_coord nb [(Float/parseFloat u)
                                        (Float/parseFloat v)])))

(defn- create-face
  "Create a list of (vertice text_cood normal)"
  [args]
  (into [] (map (fn [arg] (into [] (map #(Integer/parseInt %) (into [] (s/split arg #"/"))))) args)))

(defn- handle-f
  "Add to the model the face definitions in faces"
  [model & args]
  (let [nb (count (get model :faces))]
    (add-to-model model :faces nb (create-face args))))

(defn- handle-usemtl
  "Add to the model the Material use in texture-name"
  [model mtl]
  (add-to-model model :texture-name mtl))



;---------------- HANDLE MTL -------------------

; In the obj file only the name of the mtl is
; written but not the full path 
(defn- get-mtl-path
  "Get the right mtl path file according to the file .obj"
  [model file]
  (let [path (s/split (get model :filename) #"/")]
    (if (> (count path) 1)
      (s/join "/" (conj (pop path) file))
      file)))

(declare split-line)
(defn- get-colors
  "Get informations about colors of a mtl file"
  [model filter]
  (loop [l filter, mod model]
    (if (seq l)
      (recur (rest l) (add-to-model mod :rgb (into [] (map (fn [x] (Float/parseFloat x)) (rest (split-line (first l)))))))
      mod)))


(defn- handle-mtllib
  "Acces to mtllib in order to get rgb"
  [model mtllib]
  (let [mtl (get-mtl-path model mtllib)
        lines (with-open [r (io/reader mtl)] (vec (line-seq r)))
        colors (filter #(re-matches #"Kd.*" %) lines)
        textures (filter #(re-matches #"map_Kd.*" %) lines)]
    (if (seq textures)
      (handle-usemtl (get-colors model colors) (second (split-line (first textures))))
      (get-colors model colors))))



;---------------- UPDATE MODEL -------------------

; The list of informations in obj file
; that we need to handle 
; fo example, :usemtl handle-usemtl
(def ^:private handlers
  {:v handle-v
   :vn handle-vn
   :vt handle-vt
   :f handle-f
   :mtllib handle-mtllib})

; Gather informations in our model
(defn- update-model
  "Update the model using the handlers"
  ([model] model)
  ([model [kw & data]]
   (let [which-handler (kw handlers)]
     (apply which-handler (conj data model)))))

;---------------------------------------
;------- NORMALIZATION OBJ -------------
;---------------------------------------

; In order to get informations of the obj file
; without the comments 
(defn- delete-comment
  "Delete an OBJ comment in the string"
  [string]
  (s/replace string #"#.*" ""))

(defn- split-line
  "Change a line into a vector using whitespace as a delimiter"
  [string]
  (let [v (s/split string #"\s+")]
    (assoc v 0 (keyword (first v))))) ;The 1st element (keyword) define the vector (use in handlers)

; Check if there is a information that can be 
; handle by our functions 
(defn- is-valid?
  "Check if the line is what we want"
  [[kw & data]]
  (contains? handlers kw))

;---------------------------------------
;------------- TRANSFORM ---------------
;---------------------------------------


; Each face have a number of vertices that have the same normal 
; (so we use repeat) 
;  * normals is the normals of the model that will be change
;  * n is the sequence 
(defn- trans-normals
  "Transform :normals of the model into :normals of a mesh"
  [normals n]
  (loop [ks (keys normals), res {}, nb n]
    (if (and (seq ks) (seq nb))
      (recur (rest ks) (assoc res (first ks) (into [] (reduce concat (repeat (first nb) (get normals (first ks)))))) (rest nb))
      res)))

(defn- add-vt
  "Add coordinates texttures to the structure (maps)"
  [tc res vt keyw acc]
  (if (seq vt)
    (if (contains? acc (first vt))
      (add-vt tc res (rest vt) keyw acc)
      (add-vt tc (assoc res keyw (into [] (concat (get res keyw) (get tc (first vt)))))
             (rest vt) keyw (conj acc (first vt))))
    [res acc]))


(defn- get-vn
  "Get normals of a face"
  [f]
  (into [] (map #(first (rest (rest %))) f)))

; Transform Faces and Textures
;  * mapFaces : information about faces
;  * textures : information about textures
; Return couple of the new faces and textures
(defn- trans-faces-and-textures
  "Transform :faces and :text_coord of the model into a mesh"
  ([mapFaces textures] (trans-faces-and-textures textures (vals mapFaces) {} {} {}))
  ([tc list res coord acc]
   (if (seq list)
     (let [face (first list)
           v (map #(dec %) (map #(first %) face)) ;Get nodes of a face
           f (first (get-vn face)) ;Get number of the face
           vt (map #(first (rest %)) face)
           [coord2 acc2] (add-vt tc coord v f (get acc f #{}))]
       (trans-faces-and-textures tc (rest list) (assoc res f (into [] (concat v (get res f)))) ;Add indices of a face
                 coord2 (assoc acc f acc2)))
     [res coord])))

; Change a vertice according to it's face update
;  * f : vector of vertice for one face
;  * vs : list of all vertices 
;  * n : number of vertices without duplicate
; Return couple of the new vertices and faces
(defn- modif-verticies
  [f vs n]
  (loop [face f, resV [], resF [], acc {}, cpt n]
    (if (seq face)
      (let [ind (first face)]
        (if (get acc (first face))
          (recur (rest face) resV (conj resF (get acc ind)) acc cpt)
          (recur (rest face) (vec (concat resV (get vs ind))) (conj resF cpt) (assoc acc ind cpt) (inc cpt))))
      [resV, resF])))

; Change a vertice according to it's face update
;  * f : vector of vertice of all faces
;  * vs : list of all vertices 
; Return couple of the new vertices and faces
(defn- trans-verticies
  "Transforme Vertices and Face according to the update"
  ([f vs]
   (loop [size (map #(count (set %)) (vals (into (sorted-map) f))), k (keys (into (sorted-map) f)), resV [], resF {}, n 0]
     (if (and (seq k) (seq size))
       (let [[v face] (modif-verticies (get f (first k)) vs n)]
         (recur (rest size) (rest k) (conj resV v) (assoc resF (first k) face) (+ n (first size))))
       [resV, resF]))))

(defn trans-model
  "Transform model of loader into a correct mesh"
  [model]
  (let [[f t] (trans-faces-and-textures (get model :faces) (get model :text_coord))
        n (trans-normals (get model :normals) (map #(count (set %)) (vals (into (sorted-map) f))))
        [v f2] (trans-verticies f (get model :vertices))]
    (-> model
        (assoc :faces f2)
        (assoc :vertices v)
        (assoc :normals n)
        (assoc :text_coord t)
        (dissoc :filename))))

;---------------------------------------
;------------- LOADER OBJ --------------
;---------------------------------------
(defn load-model
  "Load an OBJ file to a model"
  [file]
  (let [mymodel (add-to-model model :filename file)
        item (with-open [r (io/reader file)]
               (transduce (comp (map delete-comment)
                                (map s/trim)
                                (remove empty?)
                                (map split-line)
                                (filter is-valid?))
                          update-model
                          mymodel
                          (line-seq r)))]
    (trans-model item)))


