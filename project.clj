(require 'leiningen.core.eval)

;; per-os jvm-opts code cribbed from Overtone
(def JVM-OPTS
  {:common   []
   :macosx   ["-XstartOnFirstThread"]
   :linux    []
   :windows  []})

(defn jvm-opts
  "Return a complete vector of jvm-opts for the current os."
  [] (let [os (leiningen.core.eval/get-os)]
       (vec (set (concat (get JVM-OPTS :common)
                         (get JVM-OPTS os))))))

(def LWJGL_NS "org.lwjgl")

;; Edit this to change the version.
(def LWJGL_VERSION "3.3.3")

;; Edit this to add/remove packages.
(def LWJGL_MODULES ["lwjgl"
                    "lwjgl-assimp"
                    "lwjgl-bgfx"
                    "lwjgl-egl"
                    "lwjgl-glfw"
                    "lwjgl-jawt"
                    "lwjgl-jemalloc"
                    "lwjgl-lmdb"
                    "lwjgl-lz4"
                    "lwjgl-nanovg"
                    "lwjgl-nfd"
                    "lwjgl-nuklear"
                    "lwjgl-odbc"
                    "lwjgl-openal"
                    "lwjgl-opencl"
                    "lwjgl-opengl"
                    "lwjgl-opengles"
                    "lwjgl-openvr"
                    "lwjgl-par"
                    "lwjgl-remotery"
                    "lwjgl-rpmalloc"
                    "lwjgl-sse"
                    "lwjgl-stb"
                    "lwjgl-tinyexr"
                    "lwjgl-tinyfd"
                    "lwjgl-tootle"
                    "lwjgl-vulkan"
                    "lwjgl-xxhash"
                    "lwjgl-yoga"
                    "lwjgl-zstd"])

;; It's safe to just include all native dependencies, but you might
;; save some space if you know you don't need some platform.
(def LWJGL_PLATFORMS ["linux" "macos" "windows"])

;; These packages don't have any associated native ones.
(def no-natives? #{"lwjgl-egl" "lwjgl-jawt" "lwjgl-odbc"
                   "lwjgl-opencl" "lwjgl-vulkan"})

;; These have no natives for mac M1/M2
(def no-natives-macos-arm64?
  #{"lwjgl-openvr" "lwjgl-sse" "lwjgl-tootle"})

(defn lwjgl-deps-with-natives []
  (apply concat
         (for [m LWJGL_MODULES]
           (let [prefix [(symbol LWJGL_NS m) LWJGL_VERSION]]
             (into [prefix]
                   (if (no-natives? m)
                     []
                     (for [p (if (no-natives-macos-arm64? m)
                               LWJGL_PLATFORMS
                               (conj LWJGL_PLATFORMS "macos-arm64"))]
                       (into prefix [:classifier (str "natives-" p)
                                     :native-prefix ""]))))))))

(def all-dependencies
  (into ;; Add your non-LWJGL dependencies here
   '[[org.clojure/clojure "1.11.1"]
     [org.joml/joml "1.10.5"]
     [org.l33tlabs.twl/pngdecoder "1.0"]]
   (let [spec (lwjgl-deps-with-natives)]
     ;;(print spec)
     spec)))

(defproject yaw-engine "0.7.0-SNAPSHOT"
  :description "A simple 3D programming world (LWJGL engine)."
  :url "https://github.com/yaw-central/yaw-engine"
  :license {:name "The MIT License"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies ~all-dependencies
  :jvm-opts ^:replace ~(jvm-opts)
  :source-paths ["src/clojure", "examples"]
  :java-source-paths ["src/java" "test/java"]
  :test-paths ["test/clojure"])

