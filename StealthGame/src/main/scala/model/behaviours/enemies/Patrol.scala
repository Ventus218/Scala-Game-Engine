package model.behaviours.enemies
import sge.core.*
import behaviours.dimension2d.*
import behaviours.physics2d.*
import sge.swing.*

class Patrol(
    width: Double,
    height: Double,
    speed: Vector2D,
    position: Vector2D = (0, 0)
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1,
    visualRangeWidth: Double = width,
    visualRangeHeight: Double = height * 2
) extends Enemy(width, height, speed, "patrol.png", position)(
      scaleWidth,
      scaleHeight,
      visualRangeWidth,
      visualRangeHeight
    )
