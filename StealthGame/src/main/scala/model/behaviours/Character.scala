package model.behaviours

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import behaviours.physics2d.{RectCollider, Velocity}
import sge.swing.*
import model.logic.*
import Action.*
import Direction.*
import model.behaviours.CharacterCollisions.collidesWithWalls
import java.awt.Color
import config.Config.CHARACTERS_WIDTH
import config.Config.CHARACTERS_HEIGHT

private abstract class Character(
    width: Double = CHARACTERS_WIDTH,
    height: Double = CHARACTERS_HEIGHT,
    speed: Vector2D,
    imagePath: String,
    initialPosition: Vector2D = (0, 0)
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Behaviour
    with Positionable(initialPosition)
    with ImageRenderer(imagePath, width, height)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
    with Velocity:

  private val offsetToMoveFromCollider = 0.1

  override def onInit: Engine => Unit = engine =>
    super.onInit(engine)
    resetMovement()

  override def onEarlyUpdate: Engine => Unit = engine =>
    super.onEarlyUpdate(engine)
    updateSpeed(engine)

  private def updateSpeed(engine: Engine): Unit =
    velocity = direction match
      case TOP    => (0, speed.y)
      case BOTTOM => (0, -speed.y)
      case LEFT   => (-speed.x, 0)
      case RIGHT  => (speed.x, 0)

    if collidesWithWalls(engine, this)
    then velocity = velocity * -1
    else
      velocity = action match
        case IDLE   => (0, 0)
        case MOVE   => velocity
        case SPRINT => velocity * getSprint

  protected def resetMovement(): Unit
  protected def direction: Direction
  protected def action: Action
  protected def getSprint: Double

  private def updatePosition(offset: Vector2D) =
    position = position + offset
