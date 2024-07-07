package sge.core.behaviours.dimension2d

import sge.core.Behaviour
import sge.core.metrics.Vector2D.*

/** Add 2D position to a Behaviour
  *
  * @param position
  *   position of the Behaviour on the X and Y axis
  */
trait Positionable(var position: Vector2D = (0, 0)) extends Behaviour
