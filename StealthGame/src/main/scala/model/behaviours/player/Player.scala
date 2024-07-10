package model.behaviours.player

import sge.core.*
import sge.swing.*
import model.logic.*
import PlayerMovement.*
import model.behaviours.Character

class Player(
    width: Double,
    height: Double,
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
)(
    speed: Vector2D = (1, 1),
    sprint: Double = 1.5
) extends Character(width, height, speed, "ninja.png")(scaleWidth, scaleHeight)
    with InputHandler:
  var inputHandlers: Map[InputButton, Handler] = Map(
    W -> (onMoveTop and onResetSpeed.onlyWhenReleased),
    A -> (onMoveLeft and onResetSpeed.onlyWhenReleased),
    S -> (onMoveBottom and onResetSpeed.onlyWhenReleased),
    D -> (onMoveRight and onResetSpeed.onlyWhenReleased),
    Space -> (onSprint and onResetSpeed.onlyWhenReleased)
  )

  override def onInit: Engine => Unit = engine =>
    super.onInit(engine)
    val io = engine.io.asInstanceOf[SwingIO]
    position = position.setX(io.scenePosition(io.size).x * -1 + width)

  override protected def action(): Action = getAction
  override protected def direction(): Direction = getDirection
  override protected def getSprint(): Double = sprint
