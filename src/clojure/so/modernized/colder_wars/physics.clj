(ns so.modernized.colder-wars.physics
  (:use [so.modernized.colder-wars.util]
        [so.modernized.colder-wars.math])
  (:import [com.badlogic.gdx.physics.bullet Bullet]
           [com.badlogic.gdx.physics.bullet.collision btDbvtBroadphase btSphereShape
            btDefaultCollisionConfiguration btCollisionDispatcher btStaticPlaneShape]
           [com.badlogic.gdx.physics.bullet.dynamics btSequentialImpulseConstraintSolver
            btDiscreteDynamicsWorld btRigidBodyConstructionInfo btRigidBody]
           [com.badlogic.gdx.physics.bullet.linearmath btTransform btDefaultMotionState]))

 (Bullet/init )

(with-disposal [broadphase (btDbvtBroadphase. )
                collision-config (btDefaultCollisionConfiguration. )
                dispatcher (btCollisionDispatcher. collision-config)
                solver (btSequentialImpulseConstraintSolver. )
                dynamics-world (btDiscreteDynamicsWorld. dispatcher broadphase solver collision-config)
                ground-shape (btStaticPlaneShape. (make-vector 0 1 0) (float 1))
                fall-shape (btSphereShape. 1)
                ground-motion-state (doto (btDefaultMotionState.) (.setGraphicsWorldTrans (btTransform. (make-quaternion 0 0 0 1) (make-vector 0 -1 0))))
                ground-rigid-ci (btRigidBodyConstructionInfo. 0 ground-motion-state ground-shape (make-vector 0 0 0))
                ground-rigid-body (btRigidBody. ground-rigid-ci)
                fall-motion-state (doto (btDefaultMotionState.) (.setGraphicsWorldTrans (btTransform. (make-quaternion 0 0 0 1) (make-vector 0 50 0))))
                fall-rigid-ci (btRigidBodyConstructionInfo. 1 fall-motion-state fall-shape (make-vector 0 0 0))
                fall-rigid-body (btRigidBody. fall-rigid-ci)]
  (.calculateLocalInertia fall-shape 1 (make-vector 0 0 0))
  (doto dynamics-world
    (.setGravity (make-vector 0 -10 0))
    (.addRigidBody ground-rigid-body)
    (.addRigidBody fall-rigid-body))
  (let [mat (eye 4)]
    (doto fall-rigid-body
      (.getMotionState))
    (safely-get fall-rigid-body world-transform mat)))
    ;(.set mat (.getWorldTransform fall-rigid-body))))
;  (->> #(.stepSimulation dynamics-world (float 1/60) 10)
;       repeatedly
;       (take 300)
;       (map (fn [_]
;              (let [mat (eye 4)]
;                (doto fall-rigid-body
;                  (.getMotionState)
;                  (.getWorldTransform mat))
;                (-> mat #(.getOrigin %) #(.getY %)))))))
;  (take 300 (repeatedly #(.stepSimulation dynamics-world (float 1/60) 10))))


 ; )

;(println "Hello World!")

