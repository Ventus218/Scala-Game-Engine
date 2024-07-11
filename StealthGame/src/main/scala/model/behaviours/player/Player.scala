package model.behaviours.player

import sge.core.*
import sge.swing.*
import model.logic.*
import PlayerMovement.*
import model.behaviours.Character
import model.logic.MovementStateImpl.initialMovement
import model.behaviours.VisualRange
import model.behaviours.enemies.Enemy
import scenes.LoseGame
import scenes.LevelOne
import model.behaviours.Stairs

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
    val enemies = engine.find[VisualRange]() ++ engine.find[Enemy]()
    enemies.collectFirst(collider =>
      if collides(collider)
      then
        lifes = lifes - 1
        updateLifes(engine)
        if lifes <= 0 then engine.loadScene(LoseGame)
        else engine.loadScene(currentScene)
    )

    val stairs = engine.find[Stairs]()
    stairs.collectFirst(stair =>
      if (collides(stair)) then
        lifes = lifes + 1
        updateLifes(engine)
        engine.loadScene(nextScene)
    )

  def lifes_=(l: Int) = 
    require(l >= 0)
    _lifes = l
  def lifes = _lifes

  private def updateLifes(engine: Engine) =
    engine.storage.set("Lifes", lifes)

  override protected def action: Action = getAction
  override protected def direction: Direction = getDirection
  override protected def getSprint: Double = sprint

  override protected def resetMovement(): Unit = movement = initialMovement
