package entities

import sge.core.*
import behaviours.*
import physics2d.*
import dimension2d.*
import managers.{GameConstants, GameManager}
import sge.swing.behaviours.ingame.CircleRenderer

import java.awt.Color

object Bullets:

  val bulletDamage: Int = 1

  def enemyBullet(position: Vector2D, size: Double, velocity: Vector2D): EnemyBullet =
    new Behaviour
      with CircleRenderer(size, Color.red, priority = -1)
      with CircleCollider(size)
      with SingleScalable
      with Positionable(position)
      with Velocity(velocity)
      with EnemyBullet(bulletDamage)

  def playerBullet(position: Vector2D): PlayerBullet =
    val bulletRadius = 0.1
    val speed = 15
    new Behaviour
      with CircleRenderer(bulletRadius, Color.cyan, priority = -1)
      with CircleCollider(bulletRadius)
      with SingleScalable
      with Positionable(position)
      with Velocity(0, speed)
      with PlayerBullet(bulletDamage)

  /** Main trait that represents a bullet entity. Has a damage value and a set of targets.
    * Destroys itself on hit and if is outside the arena.
    */
  trait Bullet extends Behaviour with CircleCollider with Positionable:
    def damage: Int
    def targets: Set[CircleCollider & Health]

    override def onUpdate: Engine => Unit =
      engine =>
        hitTarget match
          case Some(t)           => t.hit(damage); engine.destroy(this)
          case None if isOutside => engine.destroy(this)
          case _                 =>
        super.onUpdate(engine)

    private def hitTarget: Option[Health] = targets.find(collides(_))
    private def isOutside: Boolean = GameManager.isOutsideArena(this)

  /** An enemy bullet
    */
  trait EnemyBullet(override val damage: Int) extends Bullet:
    override def targets: Set[CircleCollider & Health] = GameManager.player.toSet

  /** A player bullet
    */
  trait PlayerBullet(override val damage: Int) extends Bullet:
    override def targets: Set[CircleCollider & Health] = GameManager.enemies.toSet

