package ui

import managers.*
import sge.core.*
import sge.swing.*

import java.awt.Color

/** The score display during game
 */
class ScoreText extends Behaviour with UITextRenderer(
    "Score: 0",
    GameConstants.scoreTextFont,
    Color.white,
    textOffset = (10, 10)
  ):
  def setScore(value: Int): Unit = textContent = s"Score: $value"
