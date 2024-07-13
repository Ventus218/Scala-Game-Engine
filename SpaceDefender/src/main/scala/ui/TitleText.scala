package ui

import managers.GameConstants.*
import sge.core.*

/** The title of the game
  */
class TitleText(pos: Vector2D) extends TwoLineText(
  pos,
  "SPACE", "DEFENDER",
  titleSize,
  titleColor
)
