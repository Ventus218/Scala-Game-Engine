package game.gameobjects

import sge.core.*
import sge.swing.*
import game.*

class PlayerReadyButton(id: String, position: Vector2D)
    extends GameButton("", position)
    with Identifiable(id):

  override def onButtonPressed: Engine => Unit = engine =>
    engine
      .find[Hand]()
      .find(_.player == engine.gameModel.currentPlayer.info) match
      case None       => throw Exception("Didn't find the player hand")
      case Some(hand) => { hand.show = true; engine.disable(this) }

  override def onUpdate: Engine => Unit = engine =>
    buttonText = s"${engine.gameModel.currentPlayer.info} ready?"
    super.onUpdate(engine)
