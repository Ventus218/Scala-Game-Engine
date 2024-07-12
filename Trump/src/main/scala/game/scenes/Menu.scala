package game.scenes

import sge.core.*
import game.gameobjects.*

object Menu extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    PlayButton(position = (0, -5)),
    PlaySmallDeckButton(position = (0, -15)),
    ExitButton(position = (0, -25))
  )
