package model.behaviours.enemies
import sge.core.*
import behaviours.dimension2d.*
import behaviours.physics2d.*
import sge.swing.*

class Patrol(
    width: Double,
    height: Double,
    speed: Double,
    position: Vector2D = (0, 0)
)(
    visualRangeWidth: Double = width,
    visualRangeHeight: Double = height * 2
) extends Enemy(visualRangeWidth, visualRangeHeight)
    with Behaviour
    with Positionable(position)
    with ImageRenderer("patrol.png", width, height)
    with RectCollider(width, height)
    with Scalable
    with Velocity
