package entities.enemies

import AbstractDropper.DropperState.*
import entities.*
import Enemy.*
import sge.core.*
import util.Timer.*

/** Create a Dropper enemy
  * @param pos
  *   the starting position
  */
class Dropper(pos: Vector2D) extends AbstractDropper(
  pos,
  dropperHealth,
  dropperScore,
  initialState = Moving(dropperSpeed) forAbout dropperMovingTime,
  movingTime   = dropperMovingTime,
  shootingTime = dropperShootingTime,
  image = "dropper.png"
):
  override def fireBullet(engine: Engine): Unit =
    engine.create(
      Bullets.enemyBullet(
        position,
        Bullets.bulletSize,
        (0, -Bullets.fastSpeed)
      )
    )
