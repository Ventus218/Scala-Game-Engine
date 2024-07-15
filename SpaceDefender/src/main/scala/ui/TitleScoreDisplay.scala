package ui

import managers.GameConstants.*
import sge.core.*

/** The score display at the menu or endgame. It pick the score value from teh storage, given the key
  */
class TitleScoreDisplay(pos: Vector2D, text: String, key: String) extends TwoLineText(
  pos,
  s"$text:", "???",
  yourScoreTextSize,
  yourScoreTextColor
):
  override def onInit: Engine => Unit =
    engine =>
      super.onInit(engine)
      line2.textContent = engine.storage.getOption[Int](key)
        .map(_.toString)
        .getOrElse("???")
