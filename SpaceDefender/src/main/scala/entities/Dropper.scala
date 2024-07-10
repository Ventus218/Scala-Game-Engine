package entities

import sge.core.*
import behaviours.*
import physics2d.*
import dimension2d.*
import sge.swing.behaviours.ingame.SquareRenderer
import Enemies.*

import scala.concurrent.duration.*
import util.*
import Timer.*
import entities.EntityStateMachine.*
import managers.GameManager

import java.awt.Color
import scala.util.Random

object Dropper:

  /** Create a Dropper enemy
    * @param position
    *   the starting position 
    * @return
    */
  def apply(position: Vector2D): Enemy = DropperImpl(position)
  
  private enum DropperState:
    case Moving(speed: Double)
    case Shooting(lastSpeed: Double)
  
  import DropperState.*
  
  private class DropperImpl(pos: Vector2D) 
    extends EntityStateMachine[DropperState](
      startingPosition = pos, 
      startingState = Moving(dropperSpeed) forAbout Random.between(1d, 2d).seconds
    )
    with Enemy
    with Health(dropperHealth)
    with SquareRenderer(enemySize, Color.red, rotation = 45.degrees)
    with CircleCollider(enemySize/2)
    with Velocity:
  
    override def onEntityStateChange(state: DropperState)(engine: Engine): Timer[DropperState] = state match
      case Shooting(speed) =>
        Moving(speed) forAbout Random.between(1d, 2d).seconds
  
      case Moving(_) =>
        fireBullet(engine)
        Shooting(velocity.x) forAbout 700.millis
  
    override def whileInEntityState(state: DropperState)(engine: Engine): Unit = state match
      case Moving(speed) =>
        (velocity.x, position.x) match
          case (v, x) if v > 0 && x >= GameManager.arenaRightBorder => velocity = (-speed, 0)
          case (v, x) if v < 0 && x <= GameManager.arenaLeftBorder  => velocity = (-speed, 0)
          case (0, _)                                               => velocity = (+speed, 0)
          case _ =>
  
      case Shooting(_) =>
        velocity = (0, 0)
  
    override def onDeath(): Unit = 
      velocity = (0, 0)
      setDeathState()
      
    private def fireBullet(engine: Engine): Unit =
      engine.create(Bullets.enemyBullet(position, size = 0.15, velocity = (0, -8)))
