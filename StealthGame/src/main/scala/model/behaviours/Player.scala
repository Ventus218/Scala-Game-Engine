package model.behaviours

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable
import sge.core.behaviours.physics2d.RectCollider
import sge.core.behaviours.dimension2d.Scalable

class Player(
    width: Double,
    height: Double,
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Behaviour
    with Positionable
    with ImageRenderer("ninja.png", width, height)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight):
  override def onInit: Engine => Unit = engine =>
    val io = engine.io.asInstanceOf[SwingIO]
    position = position.setX(io.scenePosition(io.size).x * -1 + width)
