package model

import Decks.*
import model.Cards.*
import statemonad.*
import scala.collection.immutable.ListSet

object DeckState:
  def deal[D: DeckOps](): EitherState[D, D, Card, TrumpError] =
    EitherState(d =>
      d.deal match
        case Right(newDeck, card) => Right(newDeck, card)
        case Left(error)          => Left(error)
    )

  def shuffle[D: DeckOps]()(using
      seed: Int
  ): EitherState[D, ShuffledDeck, Unit, Unit] =
    EitherState(d => Right(d.shuffle, ()))

  def deal[D: DeckOps](n: Int): EitherState[D, D, ListSet[Card], TrumpError] =
    n match
      case 0 => EitherState(s => Right(s, ListSet()))
      case n =>
        for
          cards <- deal(n - 1)
          card <- deal()
        yield cards + card
