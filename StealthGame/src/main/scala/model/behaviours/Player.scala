package model.behaviours

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.{Positionable, Scalable}
import sge.core.behaviours.physics2d.RectCollider
import model.logic.Direction

class Player(
    width: Double,
    height: Double,
    initialDirection: Direction,
    scaleWidth: Double = 1,
    scaleHeight: Double = 1,
) extends Behaviour
    with Positionable
    with ImageRenderer("ninja.png", width, height)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)
    with InputHandler:

  import Privates.*
  var inputHandlers: Map[InputButton, Handler] = Map(
    W -> onMoveTop
  )

  override def onInit: Engine => Unit = engine =>
    val io = engine.io.asInstanceOf[SwingIO]
    position = position.setX(io.scenePosition(io.size).x * -1 + width)

  private object Privates:
    // import Movement.IDLE

    // var initialState: State = IDLE(initialDirection)

    def onMoveTop(input: InputButton): Engine => Unit = engine =>
      ???
      
