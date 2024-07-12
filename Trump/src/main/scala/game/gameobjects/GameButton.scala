package game.gameobjects

import sge.core.*
import sge.swing.*
import java.awt.Color
import sge.swing.output.Text.*
import sge.core.behaviours.dimension2d.Positionable

class GameButton(
    val text: String,
    position: Vector2D
) extends Behaviour
    with RectRenderer(24, 8, Color.gray)
    with Positionable(position)
    with Button(
      _buttonText = text,
      _textSize = 3,
      _textColor = Color.black,
      _textFont = "Arial",
      _textStyle = TextStyle.Bold,
      _textOffset = (0, 0.5),
      _inputButtonTriggers = Set(MouseButton1, MouseButton3)
    )
