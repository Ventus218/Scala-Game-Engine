package ui

import managers.GameConstants.*
import sge.core.*
import behaviours.dimension2d.Positionable
import sge.swing.*
import output.Text.TextStyle

class GameOverText(pos: Vector2D) extends Behaviour
  with TextRenderer(
    "GAME OVER", 
    gameoverTextSize, 
    gameoverTextColor,
    fontStyle = TextStyle.Bold
  )
  with Positionable(pos)
