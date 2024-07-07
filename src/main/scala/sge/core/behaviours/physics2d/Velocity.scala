package sge.core.behaviours.physics2d

import sge.core.*
import behaviours.dimension2d.*

/** Add velocity to a Positionable behaviour, in order to move it once the
  * onUpdate is called.
  *
  * @param _velocity
  *   vecto added to position according to its X and Y.
  */
trait Velocity(var velocity: Vector2D = (0, 0)) extends Positionable:
  override def onUpdate: Engine => Unit =
    engine =>
      super.onUpdate(engine)
      position = position + velocity * engine.deltaTimeSeconds
