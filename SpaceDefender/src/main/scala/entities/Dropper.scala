package entities

import util.*
import Timer.*
import entities.EntityStateMachine.*
import managers.*
import sge.core.*
import behaviours.*
import physics2d.*
import sge.swing.behaviours.ingame.*
import Enemy.*

import scala.concurrent.duration.*

import java.awt.Color
import scala.util.Random

object Dropper:

  /** Create a Dropper enemy
    * @param position
    *   the starting position
    * @return
    */
  def apply(position: Vector2D = GameManager.rearEnemyRandomPosition()): Enemy = dropper(position)

  /** Create a Dropper enemy
    * @param position
    *   the starting position 
    * @return
    */
  def dropper(position: Vector2D): Enemy = DropperImpl(position)

  /** Create a Beacon enemy
    * @param position
    *   the starting position
    * @return
    */
  def beacon(position: Vector2D): Enemy = BeaconImpl(position)
  
  private enum DropperState:
    case Moving(speed: Double)
    case Shooting(lastSpeed: Double)
  
  import DropperState.*
  
  private abstract class AbstractDropper(
      pos: Vector2D,
      h: Int,
      scoreVal: Int,
      initialState: Timer[DropperState | EntityState],
      movingTime: => FiniteDuration,
      shootingTime: => FiniteDuration
    )
    extends EntityStateMachine[DropperState](
      startingPosition = pos,
      entityHealth = h,
      startingState = initialState
    )
    with Enemy
    with RectRenderer(enemySize, enemySize/2, Color.red)
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


  private class DropperImpl(pos: Vector2D) extends AbstractDropper(
    pos,
    dropperHealth,
    dropperScore,
    initialState = Moving(dropperSpeed) forAbout dropperMovingTime,
    movingTime   = dropperMovingTime,
    shootingTime = dropperShootingTime
  ):
    override def fireBullet(engine: Engine): Unit =
      engine.create(
        Bullets.enemyBullet(
          position,
          Bullets.bulletSize,
          (0, -Bullets.fastSpeed)
        )
      )

  private class BeaconImpl(pos: Vector2D) extends AbstractDropper(
    pos,
    beaconHealth,
    beaconScore,
    initialState = Moving(beaconSpeed) forAbout beaconMovingTime,
    movingTime   = beaconMovingTime,
    shootingTime = beaconShootingTime
  ):
    override def fireBullet(engine: Engine): Unit =
      engine.create(
        Bullets.enemyLaser(
          position,
          GameConstants.arenaHeight,
          beaconShootingTime
        )
      )