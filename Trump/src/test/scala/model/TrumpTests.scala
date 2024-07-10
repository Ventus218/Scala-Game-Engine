package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Decks.*
import PlayersInfo.*

class TrumpTests extends AnyFlatSpec:
  given seed: Int = 10

  val initialDeck = Deck.stockDeck.shuffle
  val p1 = "P1"
  val p2 = "P2"
  val playersInfo = PlayersInfo(p1, p2).get
  val game = Trump(initialDeck, playersInfo).right.get

  "Trump" should "be constructed only if the deck has enough cards to play a turn" in:
    val validDeck = Deck(initialDeck.cards.take(8).toSeq*).shuffle
    val invalidDeck = validDeck.deal.right.get._1
    Trump(validDeck, playersInfo).isRight shouldBe true
    Trump(invalidDeck, playersInfo).isRight shouldBe false

  it should "let players play in the correct order" in:
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
