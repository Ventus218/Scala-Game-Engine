package model

import Cards.*
import scala.collection.immutable.ListSet
import scala.util.Random

object Decks:
  trait DeckOps[T]:
    extension (d: T)
      def size: Int
      def deal: Either[TrumpError, (T, Card)]
      def shuffle(using seed: Int): ShuffledDeck
      def cards: ListSet[Card]

  opaque type Deck = ListSet[Card]

  given DeckOps[Deck] with
    extension (d: Deck)
      def size: Int = d.size
      def deal: Either[TrumpError, (Deck, Card)] = d.headOption match
        case Some(card) => Right((d.drop(1), card))
        case None       => Left(TrumpError.NotEnoughCards)
      def shuffle(using seed: Int): ShuffledDeck =
        ShuffledDeck(d)(using given_DeckOps_Deck)
      def cards: ListSet[Card] = d

  object Deck:
    def apply(cards: Card*): Deck =
      ListSet.from(cards)
    val stockDeck: Deck =
      val cards = for
        rank <- Rank.values
        suit <- Suit.values
      yield Card(suit, rank)
      ListSet.from(cards)

  opaque type ShuffledDeck = Deck

  given DeckOps[ShuffledDeck] = summon[DeckOps[Deck]]

  object ShuffledDeck:
    def apply[D: DeckOps](deck: D)(using seed: Int): ShuffledDeck =
      Random(seed).shuffle(deck.cards)
