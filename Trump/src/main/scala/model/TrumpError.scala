package model

enum TrumpError(val message: String):
  case NotEnoughCards extends TrumpError("Deck does not contain enough cards")
