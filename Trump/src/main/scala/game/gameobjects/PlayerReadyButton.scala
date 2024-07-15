package game.gameobjects

import sge.core.*
import sge.swing.*
import game.*

class PlayerReadyButton(id: String, position: Vector2D)
    extends GameButton("", position)
    with Identifiable(id):

  override def onButtonPressed: Engine => Unit = engine =>
    findField(engine).endOfTurn(engine)
    engine
      .find[Hand]()
      .find(_.player == engine.gameModel.currentPlayer.info) match
      case None => throw Exception("Unable to find current player Hand in game")
      case Some(hand) => hand.show = true
    engine.disable(this)

  override def onUpdate: Engine => Unit = engine =>
    def buildMessage(nextPlayer: String) = s"${nextPlayer} ready?"
    val message = findField(engine).rightCard.card match
      case None       => buildMessage(engine.gameModel.currentPlayer.info)
      case Some(card) =>
        // "Predicting" the turn winner
        engine.gameModel.playCard(card) match
          case Left(error)       => throw Exception(error.message)
          case Right(game, None) => buildMessage(game.currentPlayer.info)
          case Right(game, Some(result)) => "Finish!"

    buttonText = message
    super.onUpdate(engine)

  private def findField(engine: Engine): Field =
    engine.find[Field](Values.Ids.field) match
      case None        => throw Exception("Unable to find Field in game")
      case Some(field) => field
