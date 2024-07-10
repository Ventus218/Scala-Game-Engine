package ui

import managers.*
import sge.core.Behaviour
import sge.swing.behaviours.overlay.UITextRenderer

import java.awt.Color

class ScoreText extends Behaviour with UITextRenderer(
  s"Score: 0",
  GameConstants.scoreTextFont,
  Color.white,
  textOffset = (10, 10)
)
