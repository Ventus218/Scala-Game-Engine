package sge.core.behaviours.physics2d

import sge.core.*

/** Add an acceleration to a Velocity behaviour in order to increment (or
  * decrement) its velocity every time the onEarlyUpdate is called.
  *
  * @param _acceleration
  *   vector added to velocity according to its X and Y
  */
trait Acceleration(var acceleration: Vector2D = (0, 0)) extends Velocity:
  override def onEarlyUpdate: Engine => Unit =
    engine =>
      super.onEarlyUpdate(engine)
      velocity = velocity + acceleration * engine.deltaTimeSeconds
