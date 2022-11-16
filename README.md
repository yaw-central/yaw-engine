# Yaw : live-coding a (simple) 3D world

**(ultimately) a 3D live-coding experience for Clojurians of all ages**

(for now a very basic 3D engine based on LLWJGL with a thin and imperative
Clojure wrapper)

## Disclaimer

Yaw is *not yet* usable, except for extremely simplistic examples!
The Java/OpenGL-side of things is a basic engine relatively well-tested.
The Clojure part is minimalistic, a separate projet, yaw-reactive, investigates
a more idiomatic way to 3D programming in Clojure.

## Requirements

 - Clojure 1.8+
 - Java : 1.8 +
 - Leiningen 2.7.1+
 - LWJGL 3.0+ (via Hello_jgl, thanks !)

## How to start ?

For now the Clojure API is just a thin layer above
 the java mess... So expect many *bang*s (`!`'s) ...

Most of the current API can be found in the
`yaw.world` namespace.

So you can fire your repl 

```
$ lein repl
```

and type (after the `=>` prompt of course):

```
(use 'yaw.world)
```

An openGL window lives in a `world` (because we're megalomaniacs!)

```clojure
(def world (create-world!))
```

If all goes well a window will open, empty and dark as hell!

Yaw will only use basic 3D objects with simple materials.
For new there's only a bunch of available objects, and the 
material only talks about *color* and *reflectance*.

Let's create a *solid block* (a.k.a. a cube) with the default properties.

```clojure
(def block (create-block! world))
```

Now you should see a blue block in the middle of your window.
If not, then something went wrong ... please fill an issue
in this case (if you're on a mac please stay tuned and
blame the company behind it)...

To see something we should put it a little bit farther away.

```clojure
(translate! block :z -2)
```

We can rotate our cube, e.g. 20 degrees on y...

```clojure
(rotate! block :y 20)
```

It's working (hopefully) but we don't really see the edges
of the block. That's because the only light in the scene for
now is *ambient light*.

### More shapes

One can also quickly create a square-based pyramid like so
```clojure
(def pyr (create-pyramid! world))
```

## Summary

```clojure
(use 'yaw.world)
(def univ (start-universe!))
(def world (:world @univ))
(def block (create-block! world))
(translate! block :z -2)
(rotate! block :y 20)
```

# How to develop ?

This project being a mixed Java/Clojure project, we actively encourage 
the use of Intellij Idea together with the Cursive plugins for Clojure
 development.

For now we use the leiningen tool for build management, at some point we
will also support the newer Deps.

----
Copyright (C) 2016-∞ F. Peschanski et al. under the MIT License
