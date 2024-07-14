package ui

import managers.*
import sge.core.*
import behaviours.dimension2d.*
import behaviours.physics2d.*
import sge.swing.*

/** The moving space background
 */
class Background extends Behaviour:
  def speed: Double = 0.3
  private def background(pos: Vector2D): ImageRenderer = new Behaviour
    with ImageRenderer(
      "space.jpg",
      GameConstants.arenaWidth,
      GameConstants.arenaHeight,
      priority = -1
    )
    with Positionable(pos)
    with Velocity(0, -speed):
    override def onEarlyUpdate: Engine => Unit =
      engine =>
        super.onEarlyUpdate(engine)
        if position.y < -GameConstants.arenaHeight then
          position = position * -1

  private val bg1 = background(0, 0)
  private val bg2 = background(0, GameConstants.arenaHeight)
  override def onInit: Engine => Unit =
    engine =>
      super.onInit(engine)
      engine.create(bg1)
      engine.create(bg2)

  override def onDeinit: Engine => Unit =
    engine =>
      super.onDeinit(engine)
      engine.destroy(bg1)
      engine.destroy(bg2)