package model.behaviours.enemies

import utils.*
import sge.core.*
import sge.swing.*
import model.logic.*
import EnemyMovement.*
import model.behaviours.*
import model.logic.MovementStateImpl.direction

abstract class Enemy(
    width: Double,
    height: Double,
    speed: Vector2D,
    imagePath: String,
    position: Vector2D = (0, 0)
)(
    scaleWidth: Double,
    scaleHeight: Double,
    visualRangeSize: Double
) extends Character(width, height, speed, imagePath, position)(
      scaleWidth,
      scaleHeight
    ):
  import Privates.*
  import Direction.*
  private val visualRange =
    VisualRange(width, visualRangeSize, this)
  override def onInit: Engine => Unit =
    engine =>
      super.onInit(engine)
      updateVisualRangeProperties()
      engine.create(visualRange)

  private object Privates:
    import metrics.Vector2D.Versor2D.*

    def updateVisualRangeProperties() =
      updateVisualRangeDirection()
      updateVisualRangeOffset()

    def updateVisualRangeDirection() =
      getDirection match
        case LEFT | RIGHT => visualRange.swapDimension()
        case _            =>

    def updateVisualRangeOffset(): Unit =
      getDirection match
        case TOP =>
          setVisualRangeOffset(up)
        case BOTTOM =>
          setVisualRangeOffset(down)
        case LEFT =>
          setVisualRangeOffset(left)
        case RIGHT =>
          setVisualRangeOffset(right)

    def setVisualRangeOffset(vector: Vector2D): Unit =
      visualRange.positionOffset = vector * verticalOffset

    def verticalOffset =
      (imageHeight / 2 + visualRange.shapeHeight / 2)

    def horizzontalOffset =
      (imageWidth / 2 + visualRange.shapeWidth / 2)
