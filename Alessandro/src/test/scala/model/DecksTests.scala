package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Decks.*
import Cards.*

class DecksTests extends AnyFlatSpec:
  val deck = Deck(Card(Cups, Ace), Card(Clubs, King))

  "Deck" should "allow to deal a card" in:
    val (newDeck, card) = deck.deal
    newDeck.size shouldBe deck.size - 1
    card.isDefined shouldBe true

  it should "Deal cards in order" in:
    var (newDeck, card) = deck.deal
    card shouldBe Some(Card(Cups, Ace))

    card = newDeck.deal._2
    card shouldBe Some(Card(Clubs, King))

  it should "deal no card if the deck is empty" in:
    val (newDeck, card) = deck.deal._1.deal._1.deal
    newDeck.size shouldBe 0
    card shouldBe None

  it should "allow to get all remaining cards in order" in:
    deck.cards.toSeq should contain theSameElementsInOrderAs Seq(
      Card(Cups, Ace),
      Card(Clubs, King)
    )

  it should "not contain duplicate cards" in:
    val card = Card(Cups, Ace)
    Deck(card, card).size shouldBe 1

  it should "allow to be shuffled" in:
    val initialDeck = Deck.stockDeck
    val shuffledStockDeck = initialDeck.shuffle
    shuffledStockDeck.cards should contain theSameElementsAs initialDeck.cards
    shuffledStockDeck.cards.toSeq shouldNot contain theSameElementsInOrderAs initialDeck.cards.toSeq

  "Deck.stockDeck" should "be of size 40" in:
    Deck.stockDeck.size shouldBe 40
