package model.behaviours.player

import sge.core.*
import sge.swing.*
import model.logic.*
import MovementStateImpl.initialMovement
import model.behaviours.*
import PlayerCollisions.*
import PlayerMovement.*
import model.logic.MovementStateImpl.stop
import sge.core.behaviours.physics2d.RectCollider
import CharacterCollisions.*
import config.Config.*

/** It is the character controlled by the player, which can move up, left,
  * bottom or right and collides with enemies's visual ranges or stairs. When
  * colliding with an enemy life will decrement, and when going to 0 there will
  * be GameOver.
  *
  * @param currentScene
  *   current scene loaded, used to reload the scene if it collides with an
  *   enemy
  * @param nextScene
  *   next scene to load, used to load the next level when colliding with stairs
  * @param initialPosition
  * @param scaleWidth
  * @param scaleHeight
  * @param speed
  *   the character will move with this speed when using W, A, S, D
  * @param sprint
  *   multiplies the speed when sprinting with Space
  */
class Player(
    currentScene: Scene,
    nextScene: Scene,
    initialPosition: Vector2D,
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
)(
    speed: Vector2D = (1, 1),
    sprint: Double = 1.5
) extends Character(
      speed = speed,
      animationControllerBuilder = AnimationController
        .builder()
        .withDefault("idle_front")
        .withSize(CHARACTERS_WIDTH, CHARACTERS_HEIGHT)
        .addAnimation("idle_front", Animation.uniform(Seq("sprites/Front_0.png"), 0.1, false))
        .addAnimation("idle_back", Animation.uniform(Seq("sprites/Back_0.png"), 0.1, false))
        .addAnimation("idle_left", Animation.uniform(Seq("sprites/Left_0.png"), 0.1, false))
        .addAnimation("idle_right", Animation.uniform(Seq("sprites/Right_0.png"), 0.1, false))
        .addAnimation("move_front", Animation.fromPattern("sprites/Front", "png", 4, 0.15))
        .addAnimation("move_back", Animation.fromPattern("sprites/Back", "png", 4, 0.15))
        .addAnimation("move_left", Animation.fromPattern("sprites/Left", "png", 4, 0.15))
        .addAnimation("move_right", Animation.fromPattern("sprites/Right", "png", 4, 0.15))
        .addAnimation("sprint_front", Animation.fromPattern("sprites/Front", "png", 4, 0.08))
        .addAnimation("sprint_back", Animation.fromPattern("sprites/Back", "png", 4, 0.08))
        .addAnimation("sprint_left", Animation.fromPattern("sprites/Left", "png", 4, 0.08))
        .addAnimation("sprint_right", Animation.fromPattern("sprites/Right", "png", 4, 0.08))
        ,
      initialPosition = initialPosition
    )(scaleWidth, scaleHeight)
    with InputHandler:
  var inputHandlers: Map[InputButton, Handler] = Map(
    W -> (onMoveTop(this) and onResetSpeed.onlyWhenReleased),
    A -> (onMoveLeft(this) and onResetSpeed.onlyWhenReleased),
    S -> (onMoveBottom(this) and onResetSpeed.onlyWhenReleased),
    D -> (onMoveRight(this) and onResetSpeed.onlyWhenReleased),
    Space -> (onSprint and onResetSpeed.onlyWhenReleased)
  )
  private var _lifes = 0

  override def onInit: Engine => Unit = engine =>
    super.onInit(engine)
    _lifes = engine.storage.get[Int]("Lifes")

  override def onEarlyUpdate: Engine => Unit = engine =>
    action match
      case Action.IDLE => direction match
        case Direction.TOP => this.playAnimation("idle_back")
        case Direction.LEFT => this.playAnimation("idle_left")
        case Direction.BOTTOM => this.playAnimation("idle_front")
        case Direction.RIGHT => this.playAnimation("idle_right")
      case Action.MOVE => direction match
        case Direction.TOP => this.playAnimation("move_back")
        case Direction.LEFT => this.playAnimation("move_left")
        case Direction.BOTTOM => this.playAnimation("move_front")
        case Direction.RIGHT => this.playAnimation("move_right")
      case Action.SPRINT => direction match
        case Direction.TOP => this.playAnimation("sprint_back")
        case Direction.LEFT => this.playAnimation("sprint_left")
        case Direction.BOTTOM => this.playAnimation("sprint_front")
        case Direction.RIGHT => this.playAnimation("sprint_right")
    
    super.onEarlyUpdate(engine)

  override def onLateUpdate: Engine => Unit = engine =>
    collidesWithWalls(engine, this)
    collidesWithEnemies(engine, this, currentScene)
    collidesWithStairs(engine, this, nextScene)

    super.onLateUpdate(engine)

  def lifes_=(l: Int) =
    require(l >= 0)
    _lifes = l
  def lifes = _lifes

  override protected def action: Action = getAction
  override protected def direction: Direction = getDirection
  override protected def getSprint: Double = sprint

  override protected def resetMovement(): Unit = movement = initialMovement
