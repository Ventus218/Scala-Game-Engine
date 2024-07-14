package game.scenes

import sge.core.*
import game.gameobjects.*

object GameResult extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    PlayerFinalScore(player = "P1", position = (-20, 20)),
    PlayerFinalScore(player = "P2", position = (20, 20)),
    ResultMessage(),
    ExitButton(position = (0, -25))
  )
