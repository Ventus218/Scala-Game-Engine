package model

enum TrumpError(val message: String):
  case NotEnoughCards extends TrumpError("Deck does not contain enough cards")
  case RuleBroken(_message: String = "A Trump rule was broken")
      extends TrumpError(_message)
