package model.behaviours.player

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.{Positionable, Scalable}
import sge.core.behaviours.physics2d.{RectCollider, Velocity}
import model.logic.*

class Player(
    width: Double,
    height: Double,
    initialDirection: Direction,
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
)(
    movementVelocity: Vector2D = (1, 1),
    sprint: Double = 1.5
) extends Behaviour
    with Positionable
    with ImageRenderer("ninja.png", width, height)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
    with Velocity
    with InputHandler:
  import PlayerMovement.*
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
        case TOP    => (0, movementVelocity.y)
        case BOTTOM => (0, -movementVelocity.y)
        case LEFT   => (-movementVelocity.x, 0)
        case RIGHT  => (movementVelocity.x, 0)

      velocity = action match
        case IDLE   => (0, 0)
        case MOVE   => velocity
        case SPRINT => velocity * sprint
