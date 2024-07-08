package gamebehaviours

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable

class Player(width: Double, height: Double)
    extends Behaviour
    with Positionable
    with ImageRenderer("ninja.png", width, height):
  override def onInit: Engine => Unit = engine =>
    val io = engine.io.asInstanceOf[SwingIO]
    position = position.setX(io.scenePosition(io.size).x * -1 + width)
