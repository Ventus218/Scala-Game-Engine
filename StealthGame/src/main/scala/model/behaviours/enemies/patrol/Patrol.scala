package model.behaviours.enemies.patrol
import sge.core.*
import behaviours.dimension2d.*
import behaviours.physics2d.*
import sge.swing.*
import model.behaviours.enemies.Enemy

class Patrol(
    width: Double,
    height: Double,
    speed: Vector2D,
    position: Vector2D = (0, 0)
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1,
    visualRangeSize: Double = width * 2,
) extends Enemy(width, height, speed, "patrol.png", position)(
      scaleWidth,
      scaleHeight,
      visualRangeSize
    )
