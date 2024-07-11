package model.behaviours

import sge.core.*
import behaviours.physics2d.Collider

private object CharacterCollisions:
  def collidesWithWalls(engine: Engine, collider: Collider): Boolean =
    val walls = engine.find[Wall]()
    val collisions =
      for
        wall <- walls
        b = collider.collides(wall)
        if b
      yield b

    collisions.size > 0
