package scenes.behaviours

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable
import config.Config.*
import java.awt.Color

/** Rappresents all the game buttons of the menus
  *
  * @param text
  *   text display on top of the button
  * @param onButtonPressed
  *   function executed when the button is pressed
  * @param textColor
  *   color of the text displayed on top of the button, default to black
  * @param buttonColor
  *   color of the button, default to Gray
  * @param position
  *   position of the button, default to (0,0)
  */
class GameButton(
    text: String,
    override val onButtonPressed: Engine => Unit
)(
    textColor: Color = Color.BLACK,
    buttonColor: Color = Color.GRAY,
    position: Vector2D = (0, 0)
) extends Behaviour
    with Positionable(position)
    with RectRenderer(BUTTON_WIDTH, BUTTON_HEIGHT, buttonColor)
    with Button(text, _textColor = textColor)
