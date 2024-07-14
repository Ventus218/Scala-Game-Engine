package game.behaviours

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.*
import sge.swing.behaviours.ingame.ShapeRenderer

trait ShapeHoverAnimation(animationSpeed: Double = 1, maxScale: Double = 1.1)
    extends Behaviour
    with Positionable
    with ShapeRenderer
    with SingleScalable:
  override def onUpdate: Engine => Unit = engine =>
    val pointer = engine.swingIO.scenePointerPosition()
    val top = position.y + shapeHeight / 2
    val bottom = position.y - shapeHeight / 2
    val right = position.x + shapeWidth / 2
    val left = position.x - shapeWidth / 2

    if pointer.x >= left &&
      pointer.x <= right &&
      pointer.y >= bottom &&
      pointer.y <= top
    then
      if scale < maxScale then
        scale = scale + animationSpeed * engine.deltaTimeSeconds
    else
      if scale > 1 then scale = scale - animationSpeed * engine.deltaTimeSeconds

      super.onUpdate(engine)

  override def onEnabled: Engine => Unit = engine =>
    scale = 1
    super.onEnabled(engine)

  override def onDisabled: Engine => Unit = engine =>
    scale = 1
    super.onDisabled(engine)
