package entities

import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import sge.swing.*
import util.VectorUtils

import java.awt.Color

extension (e: Engine)
  def swingIO: SwingIO = e.io.asInstanceOf[SwingIO]
  def mousePos: Vector2D = e.swingIO.scenePointerPosition()
class Player(pos: Vector2D) extends Behaviour
  with Health(5)
  with RectRenderer(1, 1, Color.red)
  with Positionable(pos)
  with InputHandler:
  
  var inputHandlers: Map[InputButton, Handler] = Map(
    MouseButton1 -> fire.onlyWhenPressed
  )
  override def onUpdate: Engine => Unit = 
    engine => 
      moveTo(engine.mousePos)
      super.onUpdate(engine)
      
  private def moveTo(pos: Vector2D): Unit =
    position = VectorUtils.lerp(position, pos, 0.3)
    
  private def fire(input: InputButton)(engine: Engine): Unit =
    engine.create(Bullets.testBullet(position))