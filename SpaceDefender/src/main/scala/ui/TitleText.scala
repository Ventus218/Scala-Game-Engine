package ui

import managers.GameConstants.*
import sge.core.*

class TitleText(pos: Vector2D) extends TwoLineText(
  pos,
  "SPACE", "DEFENDERS",
  titleSize,
  titleColor
)
