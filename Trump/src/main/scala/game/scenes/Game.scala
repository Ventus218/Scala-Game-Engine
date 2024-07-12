package game.scenes

import sge.core.*
import game.gameobjects.*
import game.Values

object Game extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    GameModel(Values.Ids.gameModel),
    Deck(position = (-40, 0)),
    TrumpCard(position = (-30, 0)),
    Hand(Player.Current, (0, -20), spacing = 1, show = true),
    Hand(Player.Next, (0, 20), spacing = 1, show = true),
    Field()
  )
