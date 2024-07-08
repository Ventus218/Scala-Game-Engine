package scenes

import sge.core.*
import config.*
import Difficulty.*
import gamebehaviours.Player

object LevelOne extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    Player(7, 7)
  )
