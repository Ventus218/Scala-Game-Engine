package ui

import sge.core.*
import behaviours.dimension2d.Positionable
import managers.GameConstants.*
import sge.swing.*
import output.Text.TextStyle.Bold

import java.awt.Color

class GameButton(text: String, pos: Vector2D, onPress: Engine => Unit) extends Behaviour
  with Button(
    _buttonText = text,
    _textSize = buttonTextSize,
    _textColor = Color.white,
    _textStyle = Bold,
    _textOffset = (0, (buttonSize.y - buttonTextSize)/2),
    _inputButtonTriggers = Set(MouseButton1)
  )
  with Positionable(pos)
  with RectRenderer(buttonSize.x, buttonSize.y, buttonColor):
  override def onButtonPressed: Engine => Unit = onPress
