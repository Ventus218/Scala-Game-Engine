import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable
import Config.*
import java.awt.Color

object GameBehaviours:
  class GameButton(
      text: String,
      override val onButtonPressed: Engine => Unit
  )(
      textColor: Color = Color.BLACK,
      buttonColor: Color = Color.GRAY,
      position: Vector2D = (0, 0)
  ) extends Behaviour
      with Positionable(position)
      with RectRenderer(WIDTH, HEIGHT, buttonColor)
      with Button(text, _textColor = textColor)
