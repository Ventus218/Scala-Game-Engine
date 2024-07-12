package game

import sge.core.*
import model.Trump.Game
import model.Cards.*
import gameobjects.GameModel

object Utils:
  extension (engine: Engine)
    def gameModel: Game[String] =
      engine.find[GameModel](Values.Ids.gameModel).get.game
    def gameModel_=(newValue: Game[String]): Unit =
      engine.find[GameModel](Values.Ids.gameModel).get.game = newValue

  extension (card: Card)
    def toImagePath: String =
      val suit = card.suit.match
        case model.Cards.Suit.Cups   => "cups"
        case model.Cards.Suit.Coins  => "coins"
        case model.Cards.Suit.Clubs  => "clubs"
        case model.Cards.Suit.Swords => "swords"
      val rank = card.rank match
        case model.Cards.Rank.Ace    => 1
        case model.Cards.Rank.Two    => 2
        case model.Cards.Rank.Three  => 3
        case model.Cards.Rank.Four   => 4
        case model.Cards.Rank.Five   => 5
        case model.Cards.Rank.Six    => 6
        case model.Cards.Rank.Seven  => 7
        case model.Cards.Rank.Knave  => 8
        case model.Cards.Rank.Knight => 9
        case model.Cards.Rank.King   => 10

      s"cards/$suit$rank.png"

export Utils.*
