package scenes

import sge.swing.*
import behaviours.overlay.UITextRenderer
import output.overlay.UIAnchor
import sge.core.*
import java.awt.Font
import java.awt.Color
import config.Config.BUTTON_HEIGHT
import config.Config.PIXEL_UNIT_RATIO

object LoseGame extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    new UITextRenderer(
      "You lost",
      Font("Arial", Font.BOLD, 50),
      Color.BLACK,
      UIAnchor.Center,
      textOffset = (0, (-BUTTON_HEIGHT * 2 * PIXEL_UNIT_RATIO).toInt)
    ) with Behaviour
  ) ++ StartingMenu()
