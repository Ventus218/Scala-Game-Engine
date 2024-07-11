package model.behaviours

import sge.core.Engine

private object CharacterCollisions:
  def collidesWithWalls(engine: Engine, player: Character): Boolean =
    val walls = engine.find[Wall]()
    val collisions =
      for
        wall <- walls
        b = player.collides(wall)
        if b
      yield b

    collisions.size > 0
