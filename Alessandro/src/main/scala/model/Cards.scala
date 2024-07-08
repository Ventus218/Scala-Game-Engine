package model

object Cards:
  export Rank.*
  export Suit.*

  case class Card(suit: Suit, rank: Rank)

  enum Rank(val power: Int, val value: Int = 0):
    case Two extends Rank(power = 1)
    case Four extends Rank(power = 2)
    case Five extends Rank(power = 3)
    case Six extends Rank(power = 4)
    case Seven extends Rank(power = 5)
    case Knave extends Rank(power = 6, value = 2)
    case Knight extends Rank(power = 7, value = 3)
    case King extends Rank(power = 8, value = 4)
    case Three extends Rank(power = 9, value = 10)
    case Ace extends Rank(power = 10, value = 11)

  given Ordering[Rank] = Ordering.by(_.power)

  enum Suit:
    case Cups
    case Coins
    case Clubs
    case Swords
