package model.behaviours

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import behaviours.physics2d.{RectCollider, Velocity}
import sge.swing.*

private abstract class Character(
    width: Double,
    height: Double,
    speed: Vector2D,
    imagePath: String,
    position: Vector2D = (0, 0)
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Behaviour
    with Positionable(position)
    with ImageRenderer(imagePath, width, height)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
    with Velocity
