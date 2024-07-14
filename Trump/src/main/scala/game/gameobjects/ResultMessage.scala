package game.gameobjects

import sge.core.*
import sge.swing.*
import sge.swing.output.Text
import java.awt.Font
import game.Values
import sge.swing.output.overlay.UIAnchor
import game.GameResult
import game.StorageKeys
import model.TrumpResult

class ResultMessage()
    extends Behaviour
    with UITextRenderer(
      text = "",
      font = Font(
        Values.Text.fontName,
        Values.Text.textStyle.style,
        Values.Text.sizePixel
      ),
      color = Values.Text.color,
      textAnchor = UIAnchor.Center,
      textOffset = (0, -75)
    ):

  override def onStart: Engine => Unit = engine =>
    engine.storage
      .getOption[GameResult](StorageKeys.gameResult)
      .map(_.result) match
      case None =>
        throw Exception("Expected a GameResult to be saved in storage")
      case Some(TrumpResult.Win(player)) => textContent = s"$player won!!!"
      case Some(TrumpResult.Draw)        => textContent = "Draw!!"

    super.onStart(engine)

  override def onDeinit: Engine => Unit = engine =>
    engine.storage.unset(StorageKeys.gameResult)
    super.onDeinit(engine)
