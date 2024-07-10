package scenes

import sge.swing.*
import behaviours.overlay.UITextRenderer
import output.overlay.UIAnchor
import sge.core.*
import java.awt.Font
import java.awt.Color

object LoseGame extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    new UITextRenderer(
      "You lost",
      Font("Arial", Font.BOLD, 18),
      Color.BLACK,
      UIAnchor.TopCenter
    ) with Behaviour
  ) ++ StartingMenu()
