package game

import sge.core.*
import model.Trump.Game
import model.Cards.*
import gameobjects.GameModel

object Utils:
  extension (engine: Engine)
    def gameModel: Game[String] =
      engine.find[GameModel](Values.Ids.gameModel).get.game

  extension (card: Card)
    def toImagePath: String =
      val suit = card.suit.match
        case model.Cards.Suit.Cups   => "cups"
        case model.Cards.Suit.Coins  => "coins"
        case model.Cards.Suit.Clubs  => "clubs"
        case model.Cards.Suit.Swords => "swords"
      s"cards/$suit${card.rank.power}.png"

export Utils.*
