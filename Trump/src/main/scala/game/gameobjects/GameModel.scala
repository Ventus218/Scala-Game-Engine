package game.gameobjects

import sge.core.*
import model.Decks.*
import scala.util.Random
import game.*
import model.Trump
import model.Trump.*
import model.PlayersInfo

class GameModel(id: String) extends Behaviour with Identifiable(id):

  private var _game: Option[Game[String]] = Option.empty

  def game: Game[String] = _game match
    case Some(game) => game
    case None       => throw Exception("Game model was not yet initialized")

  def game_=(newValue: Game[String]) = _game = Some(newValue)

  override def onInit: Engine => Unit = engine =>
    given seed: Int = Random.nextInt()
    val deck = engine.storage.getOption[Boolean](StorageKeys.useSmallDeck) match
      case Some(true) =>
        Deck(Deck.stockDeck.shuffle.cards.take(10).toSeq*).shuffle
      case _ => Deck.stockDeck.shuffle

    val players = PlayersInfo("P1", "P2").get
    game = Trump(deck, players).right.get
    super.onInit(engine)
