(ns so.modernized.colder-wars.physics
  (:use [so.modernized.colder-wars.util]
        [so.modernized.colder-wars.math])
  (:import [com.badlogic.gdx.physics.bullet Bullet]
           [com.badlogic.gdx.physics.bullet.collision btDbvtBroadphase btSphereShape
            btDefaultCollisionConfiguration btCollisionDispatcher btStaticPlaneShape]
           [com.badlogic.gdx.physics.bullet.dynamics btSequentialImpulseConstraintSolver
            btDiscreteDynamicsWorld btRigidBodyConstructionInfo btRigidBody btDynamicsWorld]
           [com.badlogic.gdx.physics.bullet.linearmath btTransform btDefaultMotionState]))

;todos
; - with-dynamics-world wrapper?
; - some kind of rigid body generator loader
; - infinite seq of timesteps?



; A protocol for an object that has some physics nature in the game world. The
; physics engine will process its movement and return a transform.
(defprotocol PhysicsNature
  (transform-of [this]))


(def ^:private physics-world-impl
  {:set-gravity! (fn [^btDynamicsWorld world gravity-vec] (.setGravity world gravity-vec))
   :add-body! (fn [^btDynamicsWorld world body] (.addRigidBody world body))
   :add-action! (fn [^btDynamicsWorld world action] (.addAction world action))
   :advance-time (fn [^btDynamicsWorld world ^double timestep] (.stepSimulation world timestep))})

(defprotocol PhysicsWorld
  (set-gravity! [^btDynamicsWorld this ^VectorLike gravity])
  (add-body! [^btDynamicsWorld this ^PhysicsNature body])
  (add-action! [^btDynamicsWorld this action])
  (advance-time [^btDynamicsWorld this ^double timestep]))

(extend btDynamicsWorld
  PhysicsWorld
  physics-world-impl)


(Bullet/init )

(with-disposal [broadphase (btDbvtBroadphase. )
                collision-config (btDefaultCollisionConfiguration. )
                dispatcher (btCollisionDispatcher. collision-config)
                solver (btSequentialImpulseConstraintSolver. )
                dynamics-world (btDiscreteDynamicsWorld. dispatcher broadphase solver collision-config)
                ground-shape (btStaticPlaneShape. (make-vector 0 1 0) (float 1))
                fall-shape (btSphereShape. 1)
                ground-motion-state (doto (btDefaultMotionState.) (.setGraphicsWorldTrans (btTransform. (make-vector 0 0 0 1) (make-vector 0 -1 0))))
                ground-rigid-ci (btRigidBodyConstructionInfo. 0 ground-motion-state ground-shape (make-vector 0 0 0))
                ground-rigid-body (btRigidBody. ground-rigid-ci)
                fall-motion-state (doto (btDefaultMotionState.) (.setGraphicsWorldTrans (btTransform. (make-vector 0 0 0 1) (make-vector 0 50 0))))
                fall-rigid-ci (btRigidBodyConstructionInfo. 1 fall-motion-state fall-shape (make-vector 0 0 0))
                fall-rigid-body (btRigidBody. fall-rigid-ci)]
  (.calculateLocalInertia fall-shape 1 (make-vector 0 0 0))
  (doto dynamics-world
    (.setGravity (make-vector 0 -10 0))
    (.addRigidBody ground-rigid-body)
    (.addRigidBody fall-rigid-body))
;  (doto fall-rigid-body
;    (.getMotionState))
;  (-> fall-rigid-body (.getWorldTransform))
;  (.stepSimulation dynamics-world (float 1/60) 10)
  (-> fall-rigid-body (.getWorldTransform))
  (.stepSimulation dynamics-world (float 1/60) 10)
  (-> fall-rigid-body (.getWorldTransform)))
