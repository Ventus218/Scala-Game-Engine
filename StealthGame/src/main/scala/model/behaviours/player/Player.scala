package model.behaviours.player

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import behaviours.physics2d.{RectCollider, Velocity}
import sge.swing.*
import model.logic.*
import player.PlayerMovement.*

class Player(
    width: Double,
    height: Double,
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
)(
    speed: Vector2D = (1, 1),
    sprint: Double = 1.5
) extends Behaviour
    with Positionable
    with ImageRenderer("ninja.png", width, height)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
    with Velocity
    with InputHandler:
  import Privates.*

  var inputHandlers: Map[InputButton, Handler] = Map(
    W -> (onMoveTop and onResetSpeed.onlyWhenReleased),
    A -> (onMoveLeft and onResetSpeed.onlyWhenReleased),
    S -> (onMoveBottom and onResetSpeed.onlyWhenReleased),
    D -> (onMoveRight and onResetSpeed.onlyWhenReleased),
    Space -> onSprint
  )

  override def onInit: Engine => Unit = engine =>
    super.onInit(engine)
    val io = engine.io.asInstanceOf[SwingIO]
    position = position.setX(io.scenePosition(io.size).x * -1 + width)

  override def onUpdate: Engine => Unit = engine =>
    super.onUpdate(engine)
    updateSpeed()

  private object Privates:
    import Direction.*
    import Action.*

    def updateSpeed() =
      val direction = getDirection(movement)._2
      val action = getAction(movement)._2

      velocity = direction match
        case TOP    => (0, speed.y)
        case BOTTOM => (0, -speed.y)
        case LEFT   => (-speed.x, 0)
        case RIGHT  => (speed.x, 0)

      velocity = action match
        case IDLE   => (0, 0)
        case MOVE   => velocity
        case SPRINT => velocity * sprint
