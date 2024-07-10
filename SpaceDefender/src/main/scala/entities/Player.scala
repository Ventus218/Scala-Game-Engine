package entities

import sge.core.*
import behaviours.*
import dimension2d.*
import physics2d.CircleCollider
import sge.swing.*
import util.*
import Timer.*
import managers.*

import java.awt.Color
import scala.concurrent.duration.*

/** Flag trait for identifying the player
  */
trait Player extends Behaviour
  with CircleCollider
  with Health
  with Positionable
  with Identifiable

object Player:

  val playerSize: Double    = 1
  val playerMaxHealth: Int  = 5
  val playerFireRate: Int   = 5
  val invulnerabilityTime: FiniteDuration = 2.seconds

  private val playerMovementLerpFactor: Double = 0.25
  private val invulnerabilityFlashes: Int      = 30
  private val firePeriod: FiniteDuration       = 1.second / playerFireRate

  /** Create the Player
    * @param position
    *   the starting position
    * @return
    */
  def apply(position: Vector2D): Behaviour = PlayerImpl(position)

  extension (e: Engine)
    def swingIO: SwingIO = e.io.asInstanceOf[SwingIO]
    def mousePos: Vector2D = e.swingIO.scenePointerPosition()

  private enum PlayerState:
    case Normal()
    case Hurt(invulnerableCounter: Int = invulnerabilityFlashes)

  import PlayerState.*

  private class PlayerImpl(pos: Vector2D)
    extends EntityStateMachine[PlayerState](
      startingPosition = pos,
      startingState = Normal().forever
    )
    with Player
    with Identifiable("player")
    with Health(playerMaxHealth)
    with CircleCollider(playerSize / 2)
    with SquareRenderer(playerSize, Color.cyan)
    with InputHandler:

    var inputHandlers: Map[InputButton, Handler] = Map(
      MouseButton1 -> (resetFireTimer.onlyWhenPressed and fire)
    )

    private var invulnerable: Boolean = false
    private var fireTimer: Timer[Unit] = Timer.runEvery(firePeriod, ())
    override def onEntityStateChange(state: PlayerState)(engine: Engine): Timer[PlayerState] = state match
      case Hurt(0) =>
        invulnerable = false
        renderOffset = (0, 0)
        Normal().forever

      case Hurt(i) =>
        invulnerable = true
        renderOffset = (0, -100 - renderOffset.y)
        Hurt(i - 1) forAbout invulnerabilityTime / invulnerabilityFlashes

      case _ => Normal().forever
    override def whileInEntityState(state: PlayerState)(engine: Engine): Unit =
      moveTo(engine.mousePos)

    override def onHit(): Unit =
      if !invulnerable then
        state = Hurt(invulnerabilityFlashes)
        super.onHit()
    override def onDeath(): Unit = setDeathState()

    private def moveTo(pos: Vector2D): Unit =
      position = VectorUtils.lerp(
        position,
        GameManager.adjustPlayerPosition(pos),
        playerMovementLerpFactor
      )
    private def resetFireTimer(input: InputButton)(engine: Engine): Unit =
      fireTimer = Timer.runEvery(firePeriod, ())
    private def fire(input: InputButton)(engine: Engine): Unit =
      fireTimer = fireTimer
        .map(f => engine.create(Bullets.playerBullet(position)))
        .updated(engine.deltaTimeNanos.nanos)