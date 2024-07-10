package model.behaviours.enemies

import utils.*
import sge.core.*
import sge.swing.*
import model.logic.*
import enemies.EnemyMovement.*
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
      setupDirection(getDirection, visualRange)
      setVisualRangeProperties()
      engine.create(visualRange)

  private object Privates:
    import metrics.Vector2D.Versor2D.*

    def setVisualRangeProperties() =
      getDirection match
        case TOP =>
          visualRange.positionOffset = up * verticalOffset
        case BOTTOM =>
          visualRange.positionOffset = down * verticalOffset
        case LEFT =>
          visualRange.positionOffset = left * horizzontalOffset
        case RIGHT =>
          visualRange.positionOffset = right * horizzontalOffset

    def setupDirection(direction: Direction, visualRange: VisualRange) =
      direction match
        case LEFT | RIGHT => visualRange.swapDimension()
        case _     =>

    private def verticalOffset =
      (imageHeight / 2 + visualRange.shapeHeight / 2)

    private def horizzontalOffset =
      (imageWidth / 2 + visualRange.shapeWidth / 2)
