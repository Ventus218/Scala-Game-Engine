package model.behaviours

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import behaviours.physics2d.{RectCollider, Velocity}
import sge.swing.*
import model.logic.*
import Action.* 
import Direction.*

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
    with Velocity:

  override def onUpdate: Engine => Unit = engine =>
    updateSpeed()
    super.onUpdate(engine)

  private def updateSpeed(): Unit =
    velocity = direction() match
      case TOP    => (0, speed.y)
      case BOTTOM => (0, -speed.y)
      case LEFT   => (-speed.x, 0)
      case RIGHT  => (speed.x, 0)

    velocity = action() match
      case IDLE   => (0, 0)
      case MOVE   => velocity
      case SPRINT => velocity * getSprint()

  protected def direction(): Direction
  protected def action(): Action
  protected def getSprint(): Double
