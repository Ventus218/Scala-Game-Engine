package entities

import sge.core.*
import behaviours.*
import dimension2d.*
import physics2d.CircleCollider
import sge.swing.*
import util.VectorUtils

import java.awt.Color

object Player:

  val playerSize: Int = 1
  val playerHealth: Int = 5
  
  def apply(position: Vector2D): Behaviour = PlayerImpl(position)
  
  extension (e: Engine)
    def swingIO: SwingIO = e.io.asInstanceOf[SwingIO]
    def mousePos: Vector2D = e.swingIO.scenePointerPosition()
    
    
  private class PlayerImpl(pos: Vector2D) extends Behaviour
    with Identifiable("player")
    with Health(playerHealth)
    with CircleCollider(playerSize)
    with RectRenderer(playerSize, playerSize, Color.red)
    with SingleScalable
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
      engine.create(Bullets.playerBullet(position))