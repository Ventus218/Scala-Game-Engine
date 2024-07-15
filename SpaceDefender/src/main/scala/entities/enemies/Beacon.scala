package entities.enemies

import AbstractDropper.DropperState.*
import entities.*
import Enemy.*
import managers.GameConstants
import sge.core.*
import util.Timer.*

/** Create a Beacon enemy
  * @param pos
  *   the starting position
  */
class Beacon(pos: Vector2D) extends AbstractDropper(
  pos,
  beaconHealth,
  beaconScore,
  initialState = Moving(beaconSpeed) forAbout beaconMovingTime,
  movingTime = beaconMovingTime,
  shootingTime = beaconShootingTime,
  image = "beacon.png"
):
  override def fireBullet(engine: Engine): Unit =
    engine.create(
      Bullets.enemyLaser(
        position,
        GameConstants.arenaHeight,
        beaconShootingTime
      )
    )
