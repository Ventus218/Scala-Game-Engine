package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Decks.*
import PlayersInfo.*
import Field.*
import Trump.*

class TrumpTests extends AnyFlatSpec:
  given seed: Int = 10

  val initialDeck = Deck.stockDeck.shuffle
  val p1 = "P1"
  val p2 = "P2"
  val playersInfo = PlayersInfo(p1, p2).get
  val game = Trump(initialDeck, playersInfo).right.get

  "Trump" should "construct a game only if the deck has enough cards to play a turn" in:
    val validDeck = Deck(initialDeck.cards.take(8).toSeq*).shuffle
    val invalidDeck = validDeck.deal.right.get._1
    Trump(validDeck, playersInfo).isRight shouldBe true
    Trump(invalidDeck, playersInfo).isRight shouldBe false

  "Game" should "let players play in the correct order" in:
    game.currentPlayer.info shouldBe playersInfo.player1

  it should "give three cards to each player hand initially" in:
    game.player(p1).hand.size shouldBe 3
    game.player(p2).hand.size shouldBe 3

  it should "alternate giving cards to player" in:
    val deckCards = initialDeck.cards.toSeq
    game
      .player(p1)
      .hand
      .cards
      .toSeq should contain theSameElementsInOrderAs Seq(
      deckCards(0),
      deckCards(2),
      deckCards(4)
    )
    game
      .player(p2)
      .hand
      .cards
      .toSeq should contain theSameElementsInOrderAs Seq(
      deckCards(1),
      deckCards(3),
      deckCards(5)
    )

  it should "deal a trump card after dealing player hands" in:
    game.trumpCard shouldBe initialDeck.cards.toSeq(6)

  it should "have dealt 7 cards (two hands an the trump card) initially" in:
    game.deck.size shouldBe initialDeck.size - 7

  it should "have no card placed on the field initially" in:
    game.field.size shouldBe 0

  it should "allow the current player to place a card on the field" in:
    val card = game.currentPlayer.hand.cards.head

    val newGame = game.playCard(card).right.get
    newGame.field.placedCards should contain theSameElementsInOrderAs Seq(
      PlacedCard(card, game.currentPlayer.info)
    )

  it should "deny the current player to place a card which is not in his hand" in:
    val card = game.nextPlayer.hand.cards.head // notice wrong player

    game.playCard(card) shouldBe a[Left[TrumpError.RuleBroken, Game[String]]]

  it should "switch players after one has played its turn" in:
    val card = game.currentPlayer.hand.cards.head
    val oldNextPlayer = game.nextPlayer

    val newGame = game.playCard(card).right.get
    newGame.currentPlayer shouldBe oldNextPlayer

