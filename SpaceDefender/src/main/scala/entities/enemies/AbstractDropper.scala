package entities.enemies

import entities.*
import EntityStateMachine.*
import Enemy.*
import managers.*
import sge.core.*
import behaviours.*
import physics2d.*
import sge.swing.behaviours.ingame.ImageRenderer
import util.Timer
import Timer.*

import scala.concurrent.duration.FiniteDuration

object AbstractDropper:
  enum DropperState:
    case Moving(speed: Double)
    case Shooting(lastSpeed: Double)

import AbstractDropper.*
import DropperState.*

/** Abstract implementation of a Dropper enemy
  * 
  * @param pos the starting position
  * @param h the health
  * @param scoreVal the score of the enemy
  * @param initialState its initial state
  * @param movingTime the period spent moving
  * @param shootingTime the period spent shooting
  * @param image
  */
abstract class AbstractDropper(
    pos: Vector2D,
    h: Int,
    scoreVal: Int,
    initialState: Timer[DropperState | EntityState],
    movingTime: => FiniteDuration,
    shootingTime: => FiniteDuration,
    image: String
  )
  extends EntityStateMachine[DropperState](
    startingPosition = pos,
    entityHealth = h,
    startingState = initialState
  )
    with Enemy
    with ImageRenderer(image, enemySize, enemySize)
    with CircleCollider(enemySize/2)
    with Velocity:

  override def score: Int = scoreVal

  override def onEntityStateChange(state: DropperState)(engine: Engine): Timer[DropperState] = state match
    case Shooting(speed) =>
      Moving(speed) forAbout movingTime

    case Moving(_) =>
      fireBullet(engine)
      Shooting(velocity.x) forAbout shootingTime

  override def whileInEntityState(state: DropperState)(engine: Engine): Unit = state match
    case Moving(speed) =>
      (velocity.x, position.x) match
        case (v, x) if v > 0 && x >= GameConstants.arenaRightBorder => velocity = (-speed, 0)
        case (v, x) if v < 0 && x <= GameConstants.arenaLeftBorder  => velocity = (-speed, 0)
        case (0, _)                                                 => velocity = (+speed, 0)
        case _ =>

    case Shooting(_) =>
      velocity = (0, 0)

  override def onDeath(): Unit =
    super.onDeath()
    velocity = (0, 0)

  def fireBullet(engine: Engine): Unit