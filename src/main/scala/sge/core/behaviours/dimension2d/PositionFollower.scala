package sge.core.behaviours.dimension2d

import sge.core.*
import sge.core.metrics.Vector2D.*

/** Gives the capability to follow another Positionable. The position of this
  * Behaviour is initialized as the follower position in the init and updated on
  * the lateUpdate, according to an offset.
  *
  * @param followed
  * @param offset
  */
trait PositionFollower(
    followed: Positionable,
    var positionOffset: Vector2D = (0, 0)
) extends Positionable:
  override def onInit: Engine => Unit =
    engine =>
      super.onInit(engine)
      position = followed.position + positionOffset

  override def onLateUpdate: Engine => Unit =
    engine =>
      super.onLateUpdate(engine)
      position = followed.position + positionOffset
