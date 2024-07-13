package entities.enemies

import entities.*
import Enemy.*
import sge.core.*
import behaviours.*
import physics2d.*
import sge.swing.behaviours.ingame.*
import util.*
import Timer.*
import VectorUtils.*

import scala.concurrent.duration.*
object Turret:
  enum TurretState:
    case Rotating()
    case Shooting()

import Turret.*
import TurretState.*

/** Create a Turret enemy
  * @param position
  *   the starting position
  */
class Turret(pos: Vector2D)
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