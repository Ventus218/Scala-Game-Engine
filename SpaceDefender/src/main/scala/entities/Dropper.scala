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
    case Spawning(_)                         => Moving(dropperSpeed) forAbout Random.between(1d, 2d).second
    case Moving(_)                           => Shooting(velocity.x) forAbout 700.millis
    case Shooting(s)                         => Moving(s) forAbout Random.between(1d, 2d).second
    case Dying(destroy) if !destroy          => Dying(true) forAbout 500.millis
    case Dying(_)                            => engine.destroy(this); Dying().forever

  override def whileInState(state: DropperState)(engine: Engine): Unit = state match
    case Spawning(pos) =>
      position = VectorUtils.lerp(position, pos, 0.2)

    case Moving(speed) =>
      (velocity.x, position.x) match
        case (v, x) if v > 0 && x >= GameManager.arenaRightBorder => velocity = velocity.setX(-speed)
        case (v, x) if v < 0 && x <= GameManager.arenaLeftBorder  => velocity = velocity.setX(-speed)
        case (v, _) if v == 0                                     => velocity = velocity.setX(speed)
        case _ =>

    case Shooting(_) =>
      velocity = velocity.setX(0)

    case Dying(destroy) if destroy =>
      scale = scale - 0.98 * engine.deltaTimeSeconds

    case _ =>
