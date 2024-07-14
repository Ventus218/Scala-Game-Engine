package game.scenes

import sge.core.*
import game.gameobjects.*
import game.Values

object Game extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    GameModel(Values.Ids.gameModel),
    TrumpReminder((-42, 28)),
    Deck(position = (-40, 0)),
    TrumpCard(position = (-30, 0)),
    Hand("P1", (0, -20), spacing = 1),
    AcquiredCards("P1", (40, -20)),
    Hand("P2", (0, 20), spacing = 1),
    AcquiredCards("P2", (40, 20)),
    Field(id = Values.Ids.field, spacing = 1),
    PlayerReadyButton(id = Values.Ids.playerReadyButton, (35, 0))
  )
