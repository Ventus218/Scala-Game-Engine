package model.behaviours

import sge.core.*
import behaviours.dimension2d.*
import behaviours.physics2d.RectCollider
import sge.swing.*
import java.awt.Color
import model.behaviours.player.Player
import scenes.LoseGame
import config.Difficulty
import scenes.LevelOne

class VisualRange(width: Double, height: Double, follower: Positionable)
    extends Behaviour
    with Positionable
    with RectRenderer(width, height, Color.BLUE)
    with RectCollider(width, height)
    with PositionFollower(follower)
    with Scalable:

  override def onUpdate: Engine => Unit = engine =>
    super.onUpdate(engine)
    val player = engine.find[Player]().head
    if collides(player) 
      then
        var lifes = engine.storage.get[Int]("Lifes")
        if lifes <= 1 then
          engine.loadScene(LoseGame)
        else
          lifes = lifes - 1
          engine.storage.set("Lifes", lifes)
          LevelOne.reset()
          engine.loadScene(LevelOne)