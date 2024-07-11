package model.behaviours.enemies

import utils.*
import sge.core.*
import metrics.Vector2D.Versor2D.*
import sge.swing.*
import model.logic.*
import model.behaviours.*
import model.logic.MovementStateImpl.*
import scala.compiletime.ops.boolean
import model.behaviours.CharacterCollisions.collidesWithWalls
import config.Config.PATROL_SPEED

class Enemy(
    width: Double,
    height: Double,
    imagePath: String,
    initialDirection: Direction,
    position: Vector2D = (0, 0),
    speed: Vector2D = (PATROL_SPEED, PATROL_SPEED)
)(
    visualRangeSize: Double = height * 2,
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Character(width, height, speed, imagePath, position)(
      scaleWidth,
      scaleHeight
    ) with EnemyMovement:
  import Privates.*
  import Direction.*
  import Action.*

  private val visualRange =
    VisualRange(width, visualRangeSize, this)
  override def onInit: Engine => Unit = engine =>
    super.onInit(engine)
    movement = initialMovement
    turn(initialDirection)
    setupVisualRangeProperties()
    engine.create(visualRange)

  override protected def action: Action = getAction
  override protected def direction: Direction = getDirection
  override protected def getSprint: Double = 0

  override protected def resetMovement(): Unit = movement = initialMovement

  def updateVisualRangeProperties() =
    swapVisualRangeDimension()
    updateVisualRangeOffset()

  def visualRangeCollidesWithWalls(engine: Engine): Boolean =
    collidesWithWalls(engine, visualRange)

  private object Privates:
    def setupVisualRangeProperties() =
      setupVisualRangeDirection()
      updateVisualRangeOffset()

    def setupVisualRangeDirection() =
      getDirection match
        case LEFT | RIGHT => swapVisualRangeDimension()
        case _            =>

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

    def setVisualRangeOffsetVertical(vector: Vector2D): Unit =
      visualRange.positionOffset = vector * verticalOffset

    def setVisualRangeOffsetHorizzontal(vector: Vector2D): Unit =
      visualRange.positionOffset = vector * horizzontalOffset

    def verticalOffset =
      (imageHeight / 2 + visualRange.shapeHeight / 2)

    def horizzontalOffset =
      (imageWidth / 2 + visualRange.shapeWidth / 2)
