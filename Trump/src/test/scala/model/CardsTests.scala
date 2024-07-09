package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Cards.*

class CardsTests extends AnyFlatSpec:
  "Card" should "have suit and rank" in:
    val card1 = Card(Cups, Ace)
    card1.suit shouldBe Cups
    card1.rank shouldBe Ace
    val card2 = Card(Clubs, Three)
    card2.suit shouldBe Clubs
    card2.rank shouldBe Three

  "Ranks" should "be 10" in:
    Rank.values.size shouldBe 10

  they should "all have different power" in:
    Rank.values.map(_.power).distinct.size shouldBe Rank.values.size

  they should "respect a specific power ordering" in:
    Four should be < Five
    Four should be < Five
    Five should be < Six
    Six should be < Seven
    Seven should be < Knave
    Knave should be < Knight
    Knight should be < King
    King should be < Three
    Three should be < Ace

  they should "have specific values (points when acquired by the player)" in:
    Rank.values.foreach:
      _ match
        case v @ Knave  => v.value shouldBe 2
        case v @ Knight => v.value shouldBe 3
        case v @ King   => v.value shouldBe 4
        case v @ Three  => v.value shouldBe 10
        case v @ Ace    => v.value shouldBe 11
        case other      => other.value shouldBe 0

  "Suits" should "be 4" in:
    Suit.values.size shouldBe 4
