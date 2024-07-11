package entities

import util.*
import Timer.*
import VectorUtils.*
import entities.EntityStateMachine.*
import managers.*
import sge.core.*
import behaviours.*
import physics2d.*
import dimension2d.*
import sge.swing.behaviours.ingame.*
import Enemy.*

import scala.concurrent.duration.*

import java.awt.Color
import scala.util.Random

object Ranger:

  /** Create a Ranger enemy
    * @param position
    *   the starting position
    * @return
    */
  def apply(position: Vector2D = GameManager.frontalEnemyRandomPosition()): Enemy = RangerImpl(position)

  private enum RangerState:
    case Moving(toPosition: Vector2D, stepCount: Int)
    case WaitingToShoot()
    case Shooting(bulletCount: Int, target: Option[Vector2D])

  import RangerState.*

  private class RangerImpl(pos: Vector2D)
    extends EntityStateMachine[RangerState](
      startingPosition = pos,
      entityHealth = rangerHealth,
      startingState = Moving(GameManager.frontalEnemyRandomPosition(), 2) forAbout 800.millis
    )
    with Enemy
    with ImageRenderer("ranger.png", enemySize, enemySize)
    with CircleCollider(enemySize/2):

    override def score: Int = rangerScore

    override def onEntityStateChange(state: RangerState)(engine: Engine): Timer[RangerState] = state match
      case Moving(_, 0) =>
        WaitingToShoot() forAbout 1.second

      case Moving(_, step) =>
        Moving(GameManager.frontalEnemyRandomPosition(), step - 1) forAbout 800.millis

      case WaitingToShoot() =>
        Shooting(3, engine.find[Player]().headOption.map(_.position)).immediately

      case Shooting(0, _) =>
        Moving(GameManager.frontalEnemyRandomPosition(), 2) forAbout 800.millis

      case Shooting(n, target) =>
        fireBullet(target)(engine)
        Shooting(n - 1, target) forAbout 100.millis

    override def whileInEntityState(state: RangerState)(engine: Engine): Unit = state match
      case Moving(pos, _) =>
        position = VectorUtils.lerp(position, pos, 0.2)

      case _ =>

    private def fireBullet(target: Option[Vector2D])(engine: Engine): Unit =
      target.foreach(playerPos =>
        val vel: Vector2D = (playerPos - position).normalized * 8
        engine.create(Bullets.enemyBullet(position, size = 0.15, velocity = vel))
      )
