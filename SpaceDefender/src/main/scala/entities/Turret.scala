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
object Turret:

  /** Create a Turret enemy
    *
    * @param position
    *   the starting position
    * @return
    */
  def apply(position: Vector2D = GameManager.frontalEnemyRandomPosition()): Enemy = TurretImpl(position)
  private enum TurretState:
    case Rotating()
    case Shooting()

  import TurretState.*

  private class TurretImpl(pos: Vector2D)
    extends EntityStateMachine[TurretState](
      startingPosition = pos,
      entityHealth = turretHealth,
      startingState = Rotating() forAbout 1300.millis
    )
      with Enemy
      with ImageRenderer("turret.png", enemySize, enemySize)
      with CircleCollider(enemySize / 2):

    override def score: Int = turretScore

    override def onEntityStateChange(state: TurretState)(engine: Engine): Timer[TurretState] = state match
      case Rotating() =>
        val shootingDirection = (Math.cos(-renderRotation), Math.sin(-renderRotation))
        fireBullet(shootingDirection)(engine)
        Shooting().immediately

      case Shooting() =>
        Rotating() forAbout 1300.millis

    override def whileInEntityState(state: TurretState)(engine: Engine): Unit =
      renderRotation = renderRotation + (turretSpeed * engine.deltaTimeSeconds).degrees

    private def fireBullet(direction: Vector2D)(engine: Engine): Unit =
      val v = direction.normalized
      val allDirections = Set(
        v,
        (v.y, -v.x),
        (-v.x, -v.y),
        (-v.y, v.x)
      )
      allDirections.foreach: versor =>
        engine.create(
          Bullets.enemyBullet(
            position + versor * enemySize / 2,
            size = Bullets.bulletSize,
            velocity = versor * Bullets.slowSpeed
          )
        )