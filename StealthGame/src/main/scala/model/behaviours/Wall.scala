package model.behaviours

import sge.core.Behaviour
import sge.core.behaviours.physics2d.RectCollider
import sge.core.behaviours.dimension2d.Positionable
import sge.core.behaviours.dimension2d.Scalable
import sge.core.metrics.Vector2D.Vector2D

class Wall(width: Double, height: Double, position: Vector2D)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Behaviour
    with Positionable(position)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
