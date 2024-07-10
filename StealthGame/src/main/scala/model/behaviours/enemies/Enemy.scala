package model.behaviours.enemies

import utils.*
import sge.core.*
import metrics.Vector2D.Versor2D.*
import sge.swing.*
import model.logic.*
import EnemyMovement.*
import model.behaviours.*
import model.logic.MovementStateImpl.*

class Enemy(
    width: Double,
    height: Double,
    speed: Vector2D,
    imagePath: String,
    position: Vector2D = (0, 0)
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1,
    visualRangeSize: Double = height * 2
) extends Character(width, height, speed, imagePath, position)(
      scaleWidth,
      scaleHeight
    ):
  import Privates.*
  import Direction.*
  import Action.*

  private val visualRange =
    VisualRange(width, visualRangeSize, this)
  override def onInit: Engine => Unit = engine =>
    super.onInit(engine)
    setupVisualRangeProperties()
    engine.create(visualRange)

  override protected def action: Action = getAction
  override protected def direction: Direction = getDirection
  override protected def getSprint: Double = 0

  def swapVisualRangeDimension() = visualRange.swapDimension()

  def updateVisualRangeOffset(): Unit =
    direction match
      case TOP =>
        setVisualRangeOffsetVertical(up)
      case BOTTOM =>
        setVisualRangeOffsetVertical(down)
      case LEFT =>
        setVisualRangeOffsetHorizzontal(left)
      case RIGHT =>
        setVisualRangeOffsetHorizzontal(right)

  private object Privates:
    def setupVisualRangeProperties() =
      setupVisualRangeDirection()
      updateVisualRangeOffset()

    def setupVisualRangeDirection() =
      getDirection match
        case LEFT | RIGHT => swapVisualRangeDimension()
        case _            =>

    def setVisualRangeOffsetVertical(vector: Vector2D): Unit =
      visualRange.positionOffset = vector * verticalOffset

    def setVisualRangeOffsetHorizzontal(vector: Vector2D): Unit =
      visualRange.positionOffset = vector * horizzontalOffset

    def verticalOffset =
      (imageHeight / 2 + visualRange.shapeHeight / 2)

    def horizzontalOffset =
      (imageWidth / 2 + visualRange.shapeWidth / 2)
