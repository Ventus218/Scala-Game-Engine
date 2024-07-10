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
import entities.EntityStateMachine.*
import managers.GameManager

import java.awt.Color
import scala.util.Random

object Ranger:

  /** Create a Ranger enemy
    * @param position
    *   the starting position 
    * @return
    */
  def apply(position: Vector2D): Enemy = RangerImpl(position)

  extension (v: Vector2D)
    def magnitude: Double = Math.sqrt(v.x*v.x + v.y*v.y)
    def normalized: Vector2D = v / v.magnitude

  private enum RangerState:
    case Moving(toPosition: Vector2D, count: Int)
    case WaitingToShoot()
    case Shooting()

  import RangerState.*

  private class RangerImpl(pos: Vector2D) 
    extends EntityStateMachine[RangerState](
      startingPosition = pos,
      startingState = Moving(GameManager.enemyRandomPosition(), 2) forAbout 800.millis
    )
    with Enemy
    with Health(dropperHealth)
    with CircleRenderer(enemySize/2, Color.red)
    with CircleCollider(enemySize/2):

    override def onEntityStateChange(state: RangerState)(engine: Engine): Timer[RangerState] = state match
      case Moving(_, step) if step > 0 =>
        Moving(GameManager.enemyRandomPosition(), step - 1) forAbout 800.millis

      case Moving(_, 0) =>
        WaitingToShoot() forAbout 1.second

      case WaitingToShoot() =>
        fireBullet(engine)
        Shooting() forAbout 400.millis

      case Shooting() =>
        Moving(GameManager.enemyRandomPosition(), 2) forAbout 800.millis

    override def whileInEntityState(state: RangerState)(engine: Engine): Unit = state match
      case Moving(pos, _) =>
        position = VectorUtils.lerp(position, pos, 0.2)
        
      case _ =>

    override def onDeath(): Unit = setDeathState()
    
    private def fireBullet(engine: Engine): Unit =
      GameManager.player.map(_.position).foreach(playerPos =>
        val vel: Vector2D = (playerPos - position).normalized * 8
        engine.create(Bullets.enemyBullet(position, size = 0.15, velocity = vel))
      )
      