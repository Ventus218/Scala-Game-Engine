package model.behaviours

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.{Positionable, Scalable}
import sge.core.behaviours.physics2d.RectCollider
import model.logic.Direction
import sge.core.behaviours.physics2d.Velocity

class Player(
    width: Double,
    height: Double,
    initialDirection: Direction,
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
)(
    movementVelocity: Vector2D = (1, 1)
) extends Behaviour
    with Positionable
    with ImageRenderer("ninja.png", width, height)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
    with Velocity
    with InputHandler:

  import Privates.*
  var inputHandlers: Map[InputButton, Handler] = Map(
    W -> onMoveTop.onlyWhenHeld
  )

  override def onInit: Engine => Unit = engine =>
    super.onInit(engine)
    val io = engine.io.asInstanceOf[SwingIO]
    position = position.setX(io.scenePosition(io.size).x * -1 + width)

  override def onUpdate: Engine => Unit = engine =>
    super.onUpdate(engine)
    updateSpeed()

  override def onLateUpdate: Engine => Unit = engine =>
    super.onLateUpdate(engine)
    resetSpeed()

  private object Privates:
    import Direction.*
    import model.logic.{*, given}
    import MovementStateImpl.*
    import State.*
    import Action.*

    var movement = MovementStateImpl(initialDirection)
    def getDirection =
      for d <- direction
      yield d

    def getAction =
      for a <- action
      yield a

    def onMoveTop(input: InputButton): Engine => Unit =
      engine =>
        val moveState =
          for
            a <- action
            _ <- if a == SPRINT then sprint() else move()
          yield ()

        val turnTopState =
          for
            _ <- moveState
            d <- direction
            _ <- turnToTop(d)
          yield ()

        movement = turnTopState(movement)._1

    def turnToTop(d: Direction): State[Movement, Direction] =
      if d == TOP
      then direction
      else
        for
          _ <- turnLeft()
          d <- direction
          _ <- turnToTop(d)
        yield d

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
        case SPRINT => velocity * 1.5

    def resetSpeed() =
      val stopState =
        for _ <- stop()
        yield ()
      movement = stopState(movement)._1
