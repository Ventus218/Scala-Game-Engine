package utils

import sge.core.behaviours.physics2d.RectCollider
import sge.swing.behaviours.ingame.RectRenderer

extension (visualRange: RectCollider & RectRenderer)
  /** Extension method of those Behaviour extending both RectCollider and
    * RectRenderer in order to switch their dimensions
    */
  def swapDimension() =
    val height = visualRange.shapeHeight
    visualRange.shapeHeight = visualRange.shapeWidth
    visualRange.shapeWidth = height

    val colliderHeight = visualRange.colliderHeight
    visualRange.colliderHeight = visualRange.colliderWidth
    visualRange.colliderWidth = colliderHeight
