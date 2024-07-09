package entities

import sge.core.*
import behaviours.*
import physics2d.*
import dimension2d.*
import managers.GameManager
import sge.swing.behaviours.ingame.CircleRenderer

import java.awt.Color

object Bullets:

  def testBullet(position: Vector2D): Bullet =
    val bulletRadius = 0.1
    val speed = 12
    new Behaviour
      with CircleRenderer(bulletRadius, Color.red)
      with CircleCollider(bulletRadius)
      with SingleScalable
      with Positionable(position)
      with Velocity(0, speed)
      with Bullet:
      override def damage: Int = 0
      override def targets: Set[CircleCollider & Health] = Set.empty


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