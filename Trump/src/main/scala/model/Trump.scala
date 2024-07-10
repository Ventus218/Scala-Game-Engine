package model

import PlayersInfo.*
import Decks.{given_DeckOps_ShuffledDeck, *}
import Hand.*
import Cards.*
import statemonad.*
import DeckState.*
import scala.collection.immutable.ListSet

object Trump:
  opaque type Game[PlayerInfo] = GameImpl[PlayerInfo]
  private case class GameImpl[PlayerInfo](
      currentPlayer: Player[PlayerInfo],
      nextPlayer: Player[PlayerInfo]
  )

  extension [PI](game: Game[PI])
    def currentPlayer: Player[PI] = game.currentPlayer
    def nextPlayer: Player[PI] = game.nextPlayer
    def player(info: PI): Player[PI] = info match
      case currentPlayer.`info` => currentPlayer
      case _                    => nextPlayer

  def apply[PlayerInfo](
      deck: ShuffledDeck,
      playersInfo: PlayersInfo[PlayerInfo]
  ): Either[TrumpError, Game[PlayerInfo]] =
    val prepareGame = for
      hands <- deal(6)
      trumpCard <- deal()
      handsSeq = hands.toSeq
      _ <- deal()
    yield GameImpl(
      Player(playersInfo.player1, Hand(handsSeq(0), handsSeq(2), handsSeq(4))),
      Player(playersInfo.player2, Hand(handsSeq(1), handsSeq(3), handsSeq(5)))
    )

    prepareGame.run(deck).map((_, game) => game)

  case class Player[PlayerInfo](info: PlayerInfo, hand: Hand)
