package ui

import managers.GameConstants.*
import sge.core.*

class TitleText(pos: Vector2D) extends TwoLineText(
  pos,
  "SPACE", "DEFENDER",
  titleSize,
  titleColor
)
