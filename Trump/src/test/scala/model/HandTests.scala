package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Hand.*
import Cards.*

class HandTests extends AnyFlatSpec:
  val cards = Seq(Card(Cups, Ace), Card(Coins, Ace), Card(Swords, Ace))
  val hand = Hand(cards*)

  "Hand" should "have a size" in:
    hand.size shouldBe cards.size

  it should "allow to pickup a card" in:
    hand.pickupCard(Card(Coins, Two)).size shouldBe cards.size + 1

  it should "place a card" in:
    hand.placeCard(cards.head) shouldBe (Hand(cards.tail*), Option(cards.head))

  it should "not place a card which is not in it" in:
    hand.placeCard(Card(Clubs, Six)) shouldBe (Hand(cards*), None)

  it should "allow to list all cards in it" in:
    hand.cards should contain theSameElementsAs cards
