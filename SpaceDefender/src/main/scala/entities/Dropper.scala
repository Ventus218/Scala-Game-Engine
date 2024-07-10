package entities

import sge.core.*
import behaviours.*
import physics2d.*
import dimension2d.*
import sge.swing.behaviours.ingame.SquareRenderer
import Enemies.*
import entities.DropperState.*

import scala.concurrent.duration.*
import util.*
import Timer.*
import managers.GameManager

import java.awt.Color
import scala.util.Random

private enum DropperState:
  case Spawning(inPosition: Vector2D)
  case Moving(speed: Double)
  case Shooting(lastSpeed: Double)
  case Dying(destroy: Boolean = false)

class Dropper(pos: Vector2D) extends Behaviour with Enemy
  with Health(dropperHealth)
  with SquareRenderer(enemySize, Color.red, rotation = 45.degrees)
  with CircleCollider(enemySize)
  with SingleScalable
  with Positionable(pos + (0, 5))
  with Velocity
  with TimerStateMachine[DropperState](Spawning(pos) forAbout 1500.millis):

  override def onStateChange(state: DropperState)(engine: Engine): Timer[DropperState] = state match
    case Spawning(_) =>
      Moving(dropperSpeed) forAbout Random.between(1d, 2d).seconds

    case Shooting(speed) =>
      Moving(speed) forAbout Random.between(1d, 2d).seconds

    case Moving(_) =>
      fireBullet(engine)
      Shooting(velocity.x) forAbout 700.millis

    case Dying(false) =>
      velocity = (0, 0)
      Dying(true) forAbout 500.millis

    case Dying(_) =>
      engine.destroy(this)
      Dying().forever

  override def whileInState(state: DropperState)(engine: Engine): Unit = state match
    case Spawning(pos) =>
      position = VectorUtils.lerp(position, pos, 0.2)

    case Moving(speed) =>
      (velocity.x, position.x) match
        case (v, x) if v > 0 && x >= GameManager.arenaRightBorder => velocity = (-speed, 0)
        case (v, x) if v < 0 && x <= GameManager.arenaLeftBorder  => velocity = (-speed, 0)
        case (0, _)                                               => velocity = (+speed, 0)
        case _ =>

    case Shooting(_) =>
      velocity = (0, 0)

    case Dying(true) =>
      scale = scale - 1.5 * engine.deltaTimeSeconds

    case _ =>

  override def onDeath(): Unit = state = Dying()
  private def fireBullet(engine: Engine): Unit =
    engine.create(Bullets.enemyBullet(position, size = 0.15, speed = 8))
