package model.behaviours

import config.Config.*
import sge.core.*
import behaviours.physics2d.RectCollider
import behaviours.dimension2d.*
import sge.swing.*
import java.awt.Color

class Wall(
    width: Double = WALL_SIZE,
    height: Double = WALL_SIZE,
    initialPosition: Vector2D
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Behaviour
    with Positionable(initialPosition)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight)

class RendererWall(
    width: Double = WALL_SIZE,
    height: Double = WALL_SIZE,
    initialPosition: Vector2D
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Wall(
      width = width,
      height = height,
      initialPosition = initialPosition
    )(
      scaleWidth = scaleWidth,
      scaleHeight = scaleHeight
    )
    with RectRenderer(width, height, Color.BLACK, priority = 100)
