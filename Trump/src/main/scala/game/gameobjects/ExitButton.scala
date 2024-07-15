package game.gameobjects

import sge.core.*
import sge.swing.*

class ExitButton(position: Vector2D) extends GameButton("Exit", position):
  override def onButtonPressed: Engine => Unit = _.stop()
