package model.behaviours.player

import sge.core.*
import sge.swing.*
import model.logic.*
import MovementStateImpl.initialMovement
import model.behaviours.*
import PlayerCollisions.*
import PlayerMovement.*

class Player(
    width: Double,
    height: Double,
    currentScene: Scene,
    nextScene: Scene,
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
  private var _lifes = 0

  override def onInit: Engine => Unit = engine =>
    super.onInit(engine)
    _lifes = engine.storage.get[Int]("Lifes")
    val io = engine.io.asInstanceOf[SwingIO]
    position = position.setX(io.scenePosition(io.size).x * -1 + width)

  override def onLateUpdate: Engine => Unit = engine =>
    super.onLateUpdate(engine)
    collidesWithEnemies(engine, this, currentScene)
    collidesWithStairs(engine, this, nextScene)

  def lifes_=(l: Int) = 
    require(l >= 0)
    _lifes = l
  def lifes = _lifes

  override protected def action: Action = getAction
  override protected def direction: Direction = getDirection
  override protected def getSprint: Double = sprint

  override protected def resetMovement(): Unit = movement = initialMovement
