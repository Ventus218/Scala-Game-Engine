package entities

import sge.core.*
import behaviours.*
import physics2d.*
import dimension2d.*
import managers.*
import sge.swing.*
import util.*
import Timer.*

import java.awt.Color
import scala.concurrent.duration.*

object Bullets:

  val bulletDamage: Int    = 1
  
  val laserWidth: Double = 0.2
  
  val fastSpeed:  Double = 8
  val slowSpeed:  Double = 2
  val bulletSize: Double = 0.15

  def enemyBullet(position: Vector2D, size: Double, velocity: Vector2D): EnemyBullet =
    new Behaviour
      with CircleRenderer(size, Color.red, priority = -1)
      with CircleCollider(size)
      with SingleScalable
      with Positionable(position)
      with Velocity(velocity)
      with EnemyBullet(bulletDamage)

  def enemyLaser(startPosition: Vector2D, length: Double, lifeTime: FiniteDuration): EnemyLaser =
    new Behaviour
      with RectRenderer(laserWidth, length, Color.red, priority = -1)
      with RectCollider(laserWidth, length)
      with Scalable
      with Positionable(startPosition - (0, length/2))
      with EnemyBullet(bulletDamage)
      with EnemyLaser(lifeTime)
  
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
    * @tparam T
    *   the type of the targets. It must have a Health and a CircleCollider
    */
  trait Bullet[T <: CircleCollider & Health] extends Behaviour with Collider with Positionable:
    def damage: Int
    def targets: Engine => Set[T]

    override def onUpdate: Engine => Unit =
      engine =>
        hitTarget(engine) match
          case Some(t)           => t.hit(damage); onTargetHit(engine)
          case None if isOutside => engine.destroy(this)
          case _                 =>
        super.onUpdate(engine)

    protected def onTargetHit(engine: Engine): Unit = engine.destroy(this)
    private def hitTarget(engine: Engine): Option[T] = targets(engine).find(collides(_))
    private def isOutside: Boolean = GameManager.isOutsideArena(this)

  /** An enemy bullet
    */
  trait EnemyBullet(override val damage: Int) extends Bullet[Player]:
    override def targets: Engine => Set[Player] = _.find[Player]().toSet

  /** An enemy laser (is still a bullet)
   */
  trait EnemyLaser(lifeTime: FiniteDuration) extends EnemyBullet:
    private var lifeTimer: Timer[Unit] = () forAbout lifeTime
    override protected def onTargetHit(engine: Engine): Unit = ()
    override def onUpdate: Engine => Unit = 
      engine => 
        lifeTimer = lifeTimer.map(u => engine.destroy(this))
          .updated(engine.deltaTimeNanos.nanos)
        super.onUpdate(engine)
    

  /** A player bullet
    */
  trait PlayerBullet(override val damage: Int) extends Bullet[Enemy]:
    override def targets: Engine => Set[Enemy] = _.find[Enemy]().toSet

