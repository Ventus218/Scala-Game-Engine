package game.gameobjects

import sge.core.*
import sge.swing.*

class PlayButton(position: Vector2D) extends GameButton("Play", position):
  override def onButtonPressed: Engine => Unit = engine =>
    engine.loadScene(game.scenes.Game)
