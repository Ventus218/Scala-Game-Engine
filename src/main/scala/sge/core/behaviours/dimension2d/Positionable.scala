package sge.core.behaviours.dimension2d

import sge.core.Behaviour
import sge.core.metrics.Vector.*

/** Add 2D position to a Behaviour
  *
  * @param position
  *   position of the Behaviour on the X and Y axis
  */
trait Positionable(var position: Vector = (0, 0)) extends Behaviour
