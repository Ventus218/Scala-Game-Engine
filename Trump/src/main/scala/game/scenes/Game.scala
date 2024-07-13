package game.scenes

import sge.core.*
import game.gameobjects.*
import game.Values

object Game extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    GameModel(Values.Ids.gameModel),
    Deck(position = (-40, 0)),
    TrumpCard(position = (-30, 0)),
    Hand("P1", (0, -20), spacing = 1, show = true),
    Hand("P2", (0, 20), spacing = 1, show = false),
    Field()
  )
