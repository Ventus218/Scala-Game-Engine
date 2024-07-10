package model.behaviours.enemies

import sge.core.*
import sge.swing.*
import model.logic.*
import enemies.EnemyMovement.*
import model.behaviours.*

abstract class Enemy(
    width: Double,
    height: Double,
    speed: Vector2D,
    imagePath: String,
    position: Vector2D = (0, 0)
)(
    scaleWidth: Double,
    scaleHeight: Double,
    visualRangeWidth: Double,
    visualRangeHeight: Double
) extends Character(width, height, speed, imagePath, position)(
      scaleWidth,
      scaleHeight
    ):
  private val visualRange =
    VisualRange(visualRangeWidth, visualRangeHeight, this)

  override def onInit: Engine => Unit =
    engine =>
      super.onInit(engine)
      visualRange.positionOffset = (colliderWidth, colliderHeight)
      engine.create(visualRange)
