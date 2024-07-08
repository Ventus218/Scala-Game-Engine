package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Decks.*
import Cards.*

class DecksTests extends AnyFlatSpec:
  "StockDeck" should "be of size 40" in:
    Deck().size shouldBe 40

  def printDeck[D: DeckOps](deck: D): Unit =
    val a = deck.deal

  it should "allow to deal a card" in:
    val (newDeck, card) = Deck().deal
    newDeck.size shouldBe 39
    card.isDefined shouldBe true

  it should "be composed of all different cards" in:
    val deck = Deck()
    import DeckUtils.*
    deck.getCards().size shouldBe deck.size

