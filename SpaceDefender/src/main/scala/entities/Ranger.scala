package entities

import sge.core.*
import behaviours.*
import physics2d.*
import dimension2d.*
import sge.swing.behaviours.ingame.CircleRenderer
import Enemies.*

import scala.concurrent.duration.*
import util.*
import Timer.*
import managers.GameManager

import java.awt.Color
import scala.util.Random

object Ranger:

  def apply(position: Vector2D): Enemy = RangerImpl(position)

  extension (v: Vector2D)
    def magnitude: Double = Math.sqrt(v.x*v.x + v.y*v.y)
    def normalized: Vector2D = v / v.magnitude

  private enum RangerState:
    case Spawning(inPosition: Vector2D)
    case Moving(toPosition: Vector2D, count: Int)
    case WaitingToShoot()
    case Shooting()
    case Dying(destroy: Boolean = false)

  import RangerState.*

  private class RangerImpl(pos: Vector2D) extends Behaviour
    with Enemy
    with Health(dropperHealth)
    with CircleRenderer(enemySize/2, Color.red)
    with CircleCollider(enemySize/2)
    with SingleScalable
    with Positionable(pos + (0, 5))
    with Velocity
    with TimerStateMachine[RangerState](Spawning(pos) forAbout 1500.millis):

    override def onStateChange(state: RangerState)(engine: Engine): Timer[RangerState] = state match
      case Spawning(_) =>
        Moving(GameManager.enemyRandomPosition(), 2) forAbout 800.millis

      case Moving(_, step) if step > 0 =>
        Moving(GameManager.enemyRandomPosition(), step - 1) forAbout 800.millis

      case Moving(_, 0) =>
        WaitingToShoot() forAbout 1.second

      case WaitingToShoot() =>
        fireBullet(engine)
        Shooting() forAbout 400.millis

      case Shooting() =>
        Moving(GameManager.enemyRandomPosition(), 2) forAbout 800.millis

      case Dying(false) =>
        velocity = (0, 0)
        Dying(true) forAbout 500.millis

      case Dying(_) =>
        engine.destroy(this)
        Dying().forever

    override def whileInState(state: RangerState)(engine: Engine): Unit = state match
      case Spawning(pos) =>
        position = VectorUtils.lerp(position, pos, 0.2)

      case Moving(pos, _) =>
        position = VectorUtils.lerp(position, pos, 0.2)

      case Dying(true) =>
        scale = scale - 1.5 * engine.deltaTimeSeconds

      case _ =>

    override def onDeath(): Unit = state = Dying()
    private def fireBullet(engine: Engine): Unit =
      GameManager.player.map(_.position).foreach(playerPos =>
        val vel: Vector2D = (playerPos - position).normalized * 8
        engine.create(Bullets.enemyBullet(position, size = 0.15, velocity = vel))
      )
      