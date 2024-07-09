package scenes

import sge.core.*
import config.*
import Difficulty.*
import model.behaviours.Player
import model.logic.Direction

object LevelOne extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    Player(width = 7, height = 7, Direction.RIGHT)
  )
