package model

import Decks.*
import model.Cards.*
import statemonad.*
import scala.collection.immutable.ListSet

object DeckState:
  def deal[D: DeckOps](): State[D, D, Either[TrumpError, Card]] =
    State(d =>
      d.deal match
        case Right(newDeck, card) => (newDeck, Right(card))
        case Left(error)          => (d, Left(error))
    )

  def shuffle[D: DeckOps]()(using seed: Int): State[D, ShuffledDeck, Unit] =
    State(d => (d.shuffle, ()))

  def deal[D: DeckOps](n: Int): State[D, D, Either[TrumpError, ListSet[Card]]] =
    State(s =>
      val computation: State[D, D, Either[TrumpError, ListSet[Card]]] = n match
        case 0 => State(s => (s, Right(ListSet())))
        case n =>
          for
            cards <- deal(n - 1)
            card <- deal()
            res = for
              card <- card
              cards <- cards
            yield (cards + card)
          yield res
      val res = computation.run(s)

      // If the operation fails the state is not altered
      res._2 match
        case Left(error)  => (s, Left(error))
        case Right(cards) => res
    )
