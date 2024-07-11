package model.behaviours

import sge.core.*
import behaviours.physics2d.RectCollider
import behaviours.dimension2d.*

class Wall(width: Double, height: Double, initialPosition: Vector2D)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Behaviour
    with Positionable(initialPosition)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
