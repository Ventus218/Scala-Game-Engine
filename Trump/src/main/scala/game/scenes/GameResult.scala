package game.scenes

import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import sge.swing.*
import game.gameobjects.*
import game.Trump.engine

object GameResult extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    PlayerFinalScore(player = "P1", position = (-20, 20)),
    PlayerFinalScore(player = "P2", position = (20, 20)),
    ResultMessage(),
    ExitButton(position = (0, -25)),
    new GameButton("Back to menu", (0, -15)):
      override def onButtonPressed: Engine => Unit = _.loadScene(Menu)
  )
