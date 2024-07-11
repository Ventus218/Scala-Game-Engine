package model

import PlayersInfo.*
import Decks.{given_DeckOps_ShuffledDeck, *}
import Hand.*
import Cards.*
import Field.*
import statemonad.*
import DeckState.*
import scala.collection.immutable.ListSet

object Trump:
  case class Player[PlayerInfo](
      info: PlayerInfo,
      hand: Hand,
      acquiredCards: Set[Card] = Set.empty
  )

  opaque type Game[PlayerInfo] = GameImpl[PlayerInfo]
  private case class GameImpl[PlayerInfo](
      currentPlayer: Player[PlayerInfo],
      nextPlayer: Player[PlayerInfo],
      deck: ShuffledDeck,
      trumpCard: Card,
      field: Field[PlayerInfo]
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

  private object GameState:
    def nop[PI](): EitherState[Game[PI], Game[PI], Unit, TrumpError] =
      EitherState(game => Right(game, ()))

    def dealFromDeck[PI](): EitherState[Game[PI], Game[PI], Card, TrumpError] =
      EitherState(game =>
        for (deck, card) <- game.deck.deal
        yield (game.copy(deck = deck), card)
      )

    def giveCardToPlayer[PI](
        card: Card,
        playerInfo: PI
    ): EitherState[Game[PI], Game[PI], Unit, TrumpError] =
      EitherState(game =>
        val player = game.player(playerInfo)
        val newPlayer = player.copy(hand = player.hand.pickupCard(card))

        game.currentPlayer.info match
          case newPlayer.`info` =>
            Right(game.copy(currentPlayer = newPlayer), ())
          case _ =>
            Right(game.copy(nextPlayer = newPlayer), ())
      )

    def giveFieldPlayer[PI](
        playerInfo: PI
    ): EitherState[Game[PI], Game[PI], Unit, TrumpError] =
      EitherState(game =>
        val player = game.player(playerInfo)
        val fieldCards = game.field.placedCards.map(_.card)
        val newPlayer =
          player.copy(acquiredCards = player.acquiredCards ++ fieldCards)

        game.currentPlayer.info match
          case newPlayer.`info` =>
            Right(game.copy(currentPlayer = newPlayer, field = Field()), ())
          case _ =>
            Right(game.copy(nextPlayer = newPlayer, field = Field()), ())
      )

    def takeCardFromCurrentPlayerHand[PI](
        card: Card
    ): EitherState[Game[PI], Game[PI], Card, TrumpError] =
      EitherState(game =>
        for (hand, card) <- game.currentPlayer.hand.placeCard(card)
        yield (
          game.copy(currentPlayer = game.currentPlayer.copy(hand = hand)),
          card
        )
      )

    def placeCardOnField[PI](
        card: Card,
        playerInfo: PI
    ): EitherState[Game[PI], Game[PI], Field[PI], TrumpError] =
      EitherState(game =>
        val field = game.field.place(card, playerInfo)
        Right(game.copy(field = field), field)
      )

    def currentPlayer[PI]()
        : EitherState[Game[PI], Game[PI], Player[PI], TrumpError] =
      EitherState(game => Right(game, game.currentPlayer))
    def nextPlayer[PI]()
        : EitherState[Game[PI], Game[PI], Player[PI], TrumpError] =
      EitherState(game => Right(game, game.nextPlayer))

    def player[PI](
        playerInfo: PI
    ): EitherState[Game[PI], Game[PI], Player[PI], TrumpError] =
      EitherState(game => Right(game, game.player(playerInfo)))
    def otherPlayer[PI](
        playerInfo: PI
    ): EitherState[Game[PI], Game[PI], Player[PI], TrumpError] =
      EitherState(game => Right(game, game.otherPlayer(playerInfo)))

    def swapPlayers[PI](): EitherState[Game[PI], Game[PI], Unit, TrumpError] =
      EitherState(game => Right(game.swappedPlayers, ()))

    def emptyField[PI](): EitherState[Game[PI], Game[PI], Unit, TrumpError] =
      EitherState(game => Right(game.copy(field = Field()), ()))

    def turnWinner[PI](): EitherState[Game[PI], Game[PI], PI, TrumpError] =
      EitherState(game =>
        TurnWinLogic.turnWinner(game.field, game.trumpCard.suit).map((game, _))
      )

  import GameState.*
  extension [PI](game: Game[PI])
    def currentPlayer: Player[PI] = game.currentPlayer
    def nextPlayer: Player[PI] = game.nextPlayer
    def player(info: PI): Player[PI] = info match
      case currentPlayer.`info` => currentPlayer
      case _                    => nextPlayer
    def otherPlayer(info: PI): Player[PI] = info match
      case currentPlayer.`info` => nextPlayer
      case _                    => currentPlayer
    def deck: ShuffledDeck = game.deck
    def trumpCard: Card = game.trumpCard
    def field = game.field
    def playCard(card: Card): Either[TrumpError, Game[PI]] =
      (for
        card <- takeCardFromCurrentPlayerHand(card)
        field <- placeCardOnField(card, currentPlayer.info)
        _ <-
          if field.placedCards.size != 2 then swapPlayers()
          else
            for
              turnWinner <- turnWinner[PI]()
              turnLoser <- GameState.otherPlayer(turnWinner)
              _ <- giveFieldPlayer(turnWinner)
              c1 <- dealFromDeck()
              c2 <- dealFromDeck()
              currentPlayer <- GameState.currentPlayer[PI]()
              nextPlayer <- GameState.nextPlayer[PI]()
              _ <- giveCardToPlayer(c1, turnWinner)
              _ <- giveCardToPlayer(c2, turnLoser.info)
              _ <-
                if turnWinner == currentPlayer.info then nop()
                else swapPlayers()
            yield ()
      yield ()).run(game).map((game, _) => game)

    private def swappedPlayers: Game[PI] =
      game.copy(currentPlayer = nextPlayer, nextPlayer = currentPlayer)
