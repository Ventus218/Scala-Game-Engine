package game.gameobjects

import sge.core.Behaviour
import sge.core.Engine
import model.*
import Trump.*
import scala.util.Random
import game.Values
import sge.core.behaviours.Identifiable

class GameModel(id: String) extends Behaviour with Identifiable(id):

  private var _game: Game[String] =
    val deck = Decks.Deck.stockDeck.shuffle(using Random.nextInt())
    val players = PlayersInfo("P1", "P2").get
    Trump(deck, players).right.get

  def game: Game[String] = _game
  def game_=(newValue: Game[String]) = _game = newValue
