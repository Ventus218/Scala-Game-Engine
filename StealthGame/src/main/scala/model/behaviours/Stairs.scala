package model.behaviours

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import behaviours.physics2d.{RectCollider, Velocity}
import sge.swing.*
import model.behaviours.player.Player

class Stairs(
    width: Double,
    height: Double,
    imagePath: String,
    nextScene: Scene,
    position: Vector2D = (0, 0)
)(
    scaleWidth: Double = 1,
    scaleHeight: Double = 1
) extends Behaviour
    with Positionable(position)
    with ImageRenderer(imagePath, width, height)
    with RectCollider(width, height)
    with Scalable(scaleWidth, scaleHeight):
        
  override def onLateUpdate: Engine => Unit = engine =>
    super.onLateUpdate(engine)
    val player = engine.find[Player]().head
    if (collides(player))
      then engine.loadScene(nextScene)
