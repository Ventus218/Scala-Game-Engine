package model

import scala.collection.immutable.ListSet
import model.Cards.*

object Hand:
  opaque type Hand = ListSet[Card]

  def apply(cards: Card*): Hand = ListSet(cards*)

  extension (h: Hand)
    def pickupCard(c: Card): Hand = h + c
    def placeCard(c: Card): (Hand, Option[Card]) =
      (h.removedAll(Seq(c)), h.find(_ == c))
    def size: Int = h.size
    def cards: Iterable[Card] = h
