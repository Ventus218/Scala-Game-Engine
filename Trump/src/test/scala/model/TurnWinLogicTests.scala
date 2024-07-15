package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Cards.*
import Field.*
import TurnWinLogic.*
import TrumpError.*

class TurnWinLogicTests extends AnyFlatSpec:
  val p1 = "P1"
  val p2 = "P2"
  val trump = Cups
  val notTrump = Swords
  val otherSuit = Coins

  extension (field: Field[String])
    def alternatePlace(card: Card): Field[String] =
      field.placedCards.lastOption match
        case None                      => field.place(card, p1)
        case Some(PlacedCard(_, `p2`)) => field.place(card, p1)
        case _                         => field.place(card, p2)

  "turnWinner" should "fail if the field has not exactly 2 cards in it" in:
    val field =
      Field()
        .alternatePlace(Card(Clubs, Three))
        .alternatePlace(Card(Swords, Three))

    val emptyField = Field()
    val oneCardField = Field().alternatePlace(Card(Clubs, Three))
    val twoCardsField = Field()
      .alternatePlace(Card(Clubs, Three))
      .alternatePlace(Card(Swords, Three))
    val threeCardsField = Field()
      .alternatePlace(Card(Clubs, Three))
      .alternatePlace(Card(Swords, Three))
      .alternatePlace(Card(Swords, Four))

    turnWinner(emptyField, trump) shouldBe a[Left[RuleBroken, String]]
    turnWinner(oneCardField, trump) shouldBe a[Left[RuleBroken, String]]
    turnWinner(twoCardsField, trump) shouldBe a[Right[RuleBroken, String]]
    turnWinner(threeCardsField, trump) shouldBe a[Left[RuleBroken, String]]

  // Card in CAPITAL LETTERS means it has a higher power than the other

  it should "be p1 when:\t TRUMP vs trump" in:
    val field =
      Field()
        .alternatePlace(Card(trump, Three))
        .alternatePlace(Card(trump, Two))

    turnWinner(field, trump) shouldBe Right(p1)

  it should "be p1 when:\t trump vs card" in:
    val field =
      Field()
        .alternatePlace(Card(trump, Three))
        .alternatePlace(Card(notTrump, Two))

    turnWinner(field, trump) shouldBe Right(p1)

  it should "be p1 when:\t CARD vs card (same suit)" in:
    val field =
      Field()
        .alternatePlace(Card(notTrump, Three))
        .alternatePlace(Card(notTrump, Two))

    turnWinner(field, trump) shouldBe Right(p1)

  it should "be p1 when:\t card vs card (different suit)" in:
    val field =
      Field()
        .alternatePlace(Card(notTrump, Three))
        .alternatePlace(Card(otherSuit, Two))

    turnWinner(field, trump) shouldBe Right(p1)

  it should "be p2 when:\t trump vs TRUMP" in:
    val field =
      Field()
        .alternatePlace(Card(trump, Two))
        .alternatePlace(Card(trump, Three))

    turnWinner(field, trump) shouldBe Right(p2)

  it should "be p2 when:\t card vs trump" in:
    val field =
      Field()
        .alternatePlace(Card(notTrump, Two))
        .alternatePlace(Card(trump, Three))

    turnWinner(field, trump) shouldBe Right(p2)

  it should "be p2 when:\t card vs CARD (same suit)" in:
    val field =
      Field()
        .alternatePlace(Card(notTrump, Two))
        .alternatePlace(Card(notTrump, Three))

    turnWinner(field, trump) shouldBe Right(p2)
