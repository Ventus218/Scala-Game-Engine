package scenes

import config.Config.*

import sge.core.* 
import sge.swing.*
import output.overlay.UIAnchor

import java.awt.Color

/** Scene containing the items of the main menu plus an UI Renderer to tell the
  * player he won.
  */
object WinGame extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    new UITextRenderer(
      "Congratulations, you won!",
      UITextFontWithSize(50),
      Color.YELLOW,
      UIAnchor.Center,
      textOffset = (0, (-BUTTON_HEIGHT * 2 * PIXEL_UNIT_RATIO).toInt)
    ) with Behaviour
  ) ++ StartingMenu()
