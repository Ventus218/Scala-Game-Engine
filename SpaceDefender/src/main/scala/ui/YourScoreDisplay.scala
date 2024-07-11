package ui

import managers.GameConstants.*
import sge.core.Engine

class YourScoreDisplay extends TwoLineText(
  (0, 0),
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
