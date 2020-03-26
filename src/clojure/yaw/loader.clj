(ns yaw.loader
(:require [clojure.java.io :as io]
          [clojure.string :as s]))

;---------------------------------------
;-------------- STRUCTURE --------------
;---------------------------------------
(def ^:private model
{:texture-name ""
 :rbg []
 :vertices {}
 :normals {}
 :faces {}
 :text_coord {}})


;---------------------------------------
;-------------- FUNCTIONS --------------
;---------------------------------------
(defn- add-to-model
"Add a value in the model"
([model key val] ;;For :texture-name, rbg and weight doesn't change
 (case key
   :texture-name (assoc model key val)
   :rbg (assoc model key val)
   :else model))
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

(declare split-line)
(defn- handle-mtllib
"Acces to mtllib in order to get rbg"
[model mtllib]
(let [lines (with-open [r (io/reader mtllib)] (vec (line-seq r)))
      color (split-line (first (filter #(re-matches #"Kd.*" %) lines)))]
  (add-to-model model :rbg (into [] (map (fn [x] (Float/parseFloat x)) (rest color))))))

;---------------- UPDATE MODEL -------------------

(def ^:private handlers
{:v handle-v
 :vn handle-vn
 :vt handle-vt
 :f handle-f
 :usemtl handle-usemtl
 :mtllib handle-mtllib})

(defn- update-model
"Update the model using the handlers"
([model] model)
([model [kw & data]]
 (let [which-handler (kw handlers)]
   (apply which-handler (conj data model)))))

;---------------------------------------
;------- NORMALIZATION OBJ -------------
;---------------------------------------
(defn- delete-comment
"Delete an OBJ comment in the string"
[string]
(s/replace string #"#.*" ""))

(defn- split-line
"Change a line into a vector using whitespace as a delimiter"
[string]
(let [v (s/split string #"\s+")]
  (assoc v 0 (keyword (first v))))) ;The 1st element (keyword) define the vector (use in handlers)

(defn- isValid?
"Check if the line is what we want"
[[kw & data]]
(contains? handlers kw))

;---------------------------------------
;------------- TRANSFORM ---------------
;---------------------------------------
(defn- transNormal
"Transform :normals of the model into :normals of a mesh"
[normals n]
(loop [ks (keys normals), res {}, nb n]
  (if (and (seq ks) (seq nb))
    (recur (rest ks) (assoc res (first ks) (into [] (reduce concat (repeat (first nb) (get normals (first ks)))))) (rest nb))
    res)))

(defn- addVt
"Add coordinates texttures to the structure (maps)"
[tc res vt keyw acc]
(if (seq vt)
(if (contains? acc (first vt))
  (addVt tc res (rest vt) keyw acc)
  (addVt tc (assoc res keyw (into [] (concat (get res keyw) (get tc (first vt)))))
         (rest vt) keyw (conj acc (first vt))))
[res acc]))

(defn- getVn
"Get normals of a face"
[f]
(into [] (map #(first (rest (rest %))) f)))

(defn- transFnT
"Transform :faces and :text_coord of the model into a mesh"
([mapFaces textures] (transFnT textures (vals mapFaces) {} {} {}))
([tc list res coord acc]
(if (seq list)
 (let [face (first list)
       v (map #(dec %) (map #(first %) face)) ;Get nodes of a face
       f (first (getVn face))
       vt (map #(first (rest %)) face)
       [coord2 acc2] (addVt tc coord v f (get acc f #{}))]
   (transFnT tc (rest list) (assoc res f (into [] (concat v (get res f)))) ;Add indices of a face
             coord2 (assoc acc f acc2)))
 [res coord])))

(defn transformModel
"Transform model of loader into a correct mesh"
[model]
(let [[f t] (transFnT (get model :faces) (get model :text_coord))
      n (transNormal (get model :normals) (map #(count %) (vals (into (sorted-map) (get model :faces)))))]
  (-> model
      (assoc :faces f)
      (assoc :normals n)
      (assoc :text_coord t))))

;---------------------------------------
;------------- LOADER OBJ --------------
;---------------------------------------
(defn load-model
"Load an OBJ file to a model"
[file]
(let [item (with-open [r (io/reader file)]
             (transduce (comp (map delete-comment)
                              (map s/trim)
                              (remove empty?)
                              (map split-line)
                              (filter isValid?))
                        update-model
                        model
                        (line-seq r)))]
  (transformModel item)))

