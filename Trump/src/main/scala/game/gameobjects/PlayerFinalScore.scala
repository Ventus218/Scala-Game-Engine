package game.gameobjects

import sge.core.*
import sge.swing.*
import game.*
import sge.core.behaviours.dimension2d.Positionable
import model.Trump.Game
import model.TrumpResult

class PlayerFinalScore(val player: String, position: Vector2D)
    extends Behaviour
    with Positionable(position)
    with TextRenderer(
      text = "",
      size = Values.Text.size,
      color = Values.Text.color
    ):
  override def onStart: Engine => Unit = engine =>
    engine.storage.getOption[GameResult](StorageKeys.gameResult) match
      case None =>
        throw Exception("Expected a GameResult to be saved in storage")
      case Some(gameResult) =>
        val gameModel = gameResult.game
        val score =
          gameModel.player(player).acquiredCards.toSeq.map(_.rank.value).sum
        textContent = s"$player: $score"

    super.onStart(engine)

  override def onDeinit: Engine => Unit = engine =>
    engine.storage.unset(StorageKeys.gameResult)
    super.onDeinit(engine)
