package model

import Decks.*
import model.Cards.*
import statemonad.State

object DeckState:
  def deal[D: DeckOps](): State[D, D, Option[Card]] = State(d => d.deal)
  def shuffle[D: DeckOps]()(using seed: Int): State[D, ShuffledDeck, Unit] =
    State(d => (d.shuffle, ()))
