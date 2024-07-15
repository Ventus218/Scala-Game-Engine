package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Cards.*
import Field.*

class FieldTests extends AnyFlatSpec:
  type PlayerInfo = String
  val field = Field[PlayerInfo]()
  val card1 = Card(Cups, Ace)
  val card2 = Card(Cups, Two)
  val p1 = "P1"
  val p2 = "P2"

  "Field" should "be initially empty" in:
    field.size shouldBe 0

  it should "allow a player to place a card in it" in:
    field.place(card1, p1).size shouldBe 1

  it should "store placed cards and which player placed them ordered by placement" in:
    val placedCards = field.place(card1, p1).place(card2, p2).placedCards
    placedCards should contain theSameElementsInOrderAs Seq(
      PlacedCard(card1, p1),
      PlacedCard(card2, p2)
    )
