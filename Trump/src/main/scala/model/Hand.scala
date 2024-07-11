package model

import scala.collection.immutable.ListSet
import model.Cards.*

object Hand:
  opaque type Hand = ListSet[Card]

  def apply(cards: Card*): Hand = ListSet(cards*)

  extension (h: Hand)
    def pickupCard(c: Card): Hand = h + c
    def placeCard(c: Card): Either[TrumpError, (Hand, Card)] =
      h(c) match
        case true  => Right(h - c, c)
        case false => Left(TrumpError.RuleBroken("Card was not in hand"))
    def size: Int = h.size
    def cards: Iterable[Card] = h
