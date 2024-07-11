package model

import PlayersInfo.*
import Decks.{given_DeckOps_ShuffledDeck, *}
import Hand.*
import Cards.*
import statemonad.*
import DeckState.*
import scala.collection.immutable.ListSet

object Trump:
  case class Player[PlayerInfo](info: PlayerInfo, hand: Hand)

  opaque type Game[PlayerInfo] = GameImpl[PlayerInfo]
  private case class GameImpl[PlayerInfo](
      currentPlayer: Player[PlayerInfo],
      nextPlayer: Player[PlayerInfo],
      deck: ShuffledDeck,
      trumpCard: Card
  )

  def apply[PlayerInfo](
      deck: ShuffledDeck,
      playersInfo: PlayersInfo[PlayerInfo]
  ): Either[TrumpError, Game[PlayerInfo]] =
    val prepareGame = for
      hands <- deal(6)
      trumpCard <- deal()
      handsSeq = hands.toSeq
    yield (
      Player(playersInfo.player1, Hand(handsSeq(0), handsSeq(2), handsSeq(4))),
      Player(playersInfo.player2, Hand(handsSeq(1), handsSeq(3), handsSeq(5))),
      trumpCard
    )

    for
      (deck, config) <- prepareGame.run(deck)
      _ <- deck.deal // Ensure deck has at least one card (not modifying deck)
    yield (
      GameImpl(config._1, config._2, deck, config._3, Field())
    )

  extension [PI](game: Game[PI])
    def currentPlayer: Player[PI] = game.currentPlayer
    def nextPlayer: Player[PI] = game.nextPlayer
    def player(info: PI): Player[PI] = info match
      case currentPlayer.`info` => currentPlayer
      case _                    => nextPlayer
    def deck: ShuffledDeck = game.deck
    def trumpCard: Card = game.trumpCard
