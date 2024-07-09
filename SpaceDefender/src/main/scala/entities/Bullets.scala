package entities

import sge.core.*
import behaviours.*
import physics2d.*
import dimension2d.*

object Bullets:

  /** Main trait that represents a bullet entity. Has a damage value and a set of targets
    */
  trait Bullet extends Behaviour with CircleCollider:
    def damage: Int
    def targets: Set[CircleCollider & Health]

    override def onUpdate: Engine => Unit =
      engine =>
        hitTarget.foreach(t =>
          t.hit(damage)
          engine.destroy(this)
        )
        super.onUpdate(engine)

    private def hitTarget: Option[Health] = targets.find(collides(_))