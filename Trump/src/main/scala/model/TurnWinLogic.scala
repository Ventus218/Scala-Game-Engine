package model

import Field.*
import Cards.*

private[model] object TurnWinLogic:
  def turnWinner[PlayerInfo](
      field: Field[PlayerInfo],
      trump: Suit
  ): Either[TrumpError, PlayerInfo] =
    field.placedCards.size match
      case 2 =>
        val c1 = field.placedCards.head // safe
        val c2 = field.placedCards.drop(1).head // safe

        Right(
          (c1.card, c2.card) match
            case (Card(`trump`, _), Card(`trump`, _))   => winnerByValue(c1, c2)
            case (Card(`trump`, _), Card(_, _))         => c1.playerInfo
            case (Card(_, _), Card(`trump`, _))         => c2.playerInfo
            case (Card(s1, _), Card(s2, _)) if s1 != s2 => c1.playerInfo
            case (Card(s1, _), Card(s2, _)) if s1 == s2 => winnerByValue(c1, c2)
            case _                                      => c2.playerInfo
        )
      case _ =>
        Left(
          TrumpError.RuleBroken(
            "To establish a turn winner the field should have two cards"
          )
        )

  private def winnerByValue[PI](c1: PlacedCard[PI], c2: PlacedCard[PI]): PI =
    Seq(c1, c2).sortBy(_.card.rank).last.playerInfo
