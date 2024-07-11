package ui

import managers.GameConstants.*
import sge.core.*

class YourScoreDisplay(pos: Vector2D) extends TwoLineText(
  pos,
  "Your score:", "???",
  yourScoreTextSize,
  yourScoreTextColor
):
  override def onInit: Engine => Unit =
    engine =>
      super.onInit(engine)
      line2.textContent = engine.storage.getOption[Int]("score")
        .map(_.toString)
        .getOrElse("???")
