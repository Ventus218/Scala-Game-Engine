package scenes

import config.Config.*

import sge.core.* 
import sge.swing.*
import output.overlay.UIAnchor

import java.awt.Color

object WinGame extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    new UITextRenderer(
      "Congratulasions, you won!",
      UITextFontWithSize(50),
      Color.BLACK,
      UIAnchor.Center,
      textOffset = (0, (-BUTTON_HEIGHT * 2 * PIXEL_UNIT_RATIO).toInt)
    ) with Behaviour
  ) ++ StartingMenu()
