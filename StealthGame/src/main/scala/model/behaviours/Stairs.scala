package model.behaviours

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import behaviours.physics2d.{RectCollider, Velocity}
import sge.swing.*
import model.behaviours.player.Player

/** Stairs that let the player go to the next scene when colliding with them
  *
  * @param width
  * @param height
  * @param imagePath
  * @param position
  * @param scaleWidth
  * @param scaleHeight
  */
class Stairs(
    width: Double,
    height: Double,
    imagePath: String,
    position: Vector2D = (0, 0)
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Behaviour
    with Positionable(position)
    with ImageRenderer(imagePath, width, height, priority = -2)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
