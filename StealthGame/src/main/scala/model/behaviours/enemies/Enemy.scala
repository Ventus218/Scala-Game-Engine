package model.behaviours.enemies

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import behaviours.physics2d.{RectCollider, Velocity}
import sge.swing.*
import model.behaviours.VisualRange

trait Enemy(visualRangeWidth: Double, visualRangeHeight: Double)
    extends Behaviour
    with Positionable
    with ImageRenderer
    with RectCollider
    with Scalable
    with Velocity:
  private val visualRange =
    VisualRange(visualRangeWidth, visualRangeHeight, this)
  override def onInit: Engine => Unit =
    engine =>
      super.onInit(engine)
      visualRange.positionOffset = (colliderWidth, colliderHeight)
      engine.create(visualRange)
