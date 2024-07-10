package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Decks.*
import Cards.*

abstract class DecksTests[D: DeckOps] extends AnyFlatSpec:
  def deckType: String
  def deck: D

  deckType should "allow to deal a card" in:
    deck.deal match
      case Right(newDeck, card) => newDeck.size shouldBe deck.size - 1
      case Left(error)          => fail(error.message)

  it should "return TrumpError.NotEnoughCards if the deck is empty" in:
    def _emptyDeck(d: D): D = d.deal match
      case Right(newDeck, _) => _emptyDeck(newDeck)
      case Left(value)       => d

    _emptyDeck(deck).deal shouldBe Left(TrumpError.NotEnoughCards)

  it should "not contain duplicate cards" in:
    val card = Card(Cups, Ace)
    Deck(card, card).size shouldBe 1

  it should "allow to get all cards" in:
    deck.cards.size shouldBe deck.size

  given seed: Int = 10

  it should "allow to be shuffled" in:
    val shuffledStockDeck = deck.shuffle
    shuffledStockDeck.cards should contain theSameElementsAs deck.cards
    shuffledStockDeck.cards.toSeq shouldNot contain theSameElementsInOrderAs deck.cards.toSeq

  it should "be shuffled differently based on the provided seed" in:
    val shuffle1 = deck.shuffle
    val shuffle2 = deck.shuffle(using seed = 20)
    shuffle1.cards should contain theSameElementsAs shuffle2.cards
    shuffle1.cards.toSeq shouldNot contain theSameElementsInOrderAs shuffle2.cards.toSeq

  it should "be shuffled the same way if the provided seed is the same" in:
    deck.shuffle.cards.toSeq should contain theSameElementsInOrderAs deck.shuffle.cards.toSeq

class DeckTests extends DecksTests[Deck]:
  val cards: Seq[Card] = Seq(
    Card(Cups, Ace),
    Card(Clubs, King),
    Card(Coins, Three),
    Card(Swords, Three)
  )
  override def deck: Deck = Deck(cards*)
  override def deckType: String = "Deck"

  it should "Deal cards in order" in:
    def _testDealingCardsInOrder(d: Deck, expectedCards: Seq[Card]): Unit =
      d.deal match
        case Right(newDeck, card) =>
          card shouldBe expectedCards.head;
          _testDealingCardsInOrder(newDeck, expectedCards.tail)
        case _ => ()

    _testDealingCardsInOrder(deck, cards)

  it should "allow to get all remaining cards in order" in:
    deck.cards.toSeq should contain theSameElementsInOrderAs cards

class ShuffledDeckTests extends DecksTests[ShuffledDeck]:
  override def deck: ShuffledDeck = ShuffledDeck(Deck.stockDeck)
  override def deckType: String = "ShuffledDeck"

class StockDeckTests extends AnyFlatSpec:

  val stockDeck = Deck.stockDeck

  "Deck.stockDeck" should "be of size 40" in:
    stockDeck.size shouldBe 40

  it should "have 10 cards for each Suit" in:
    val suitsCards = stockDeck.cards.foldLeft(Map[Suit, Int]())((map, card) =>
      map.updatedWith(card.suit)(_ match
        case Some(cardAmount) => Some(cardAmount + 1)
        case None             => Some(1)
      )
    )
    Suit.values.foreach(
      suitsCards(_) shouldBe 10
    )
