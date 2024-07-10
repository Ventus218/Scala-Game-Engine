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
        
  override def onUpdate: Engine => Unit = engine =>
    super.onUpdate(engine)
    val player = engine.find[Player]()
    if (collides(player.head)) 
      then engine.loadScene(nextScene)
