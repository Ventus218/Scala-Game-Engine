package scenes

import sge.core.*
import sge.core.behaviours.dimension2d.*
import sge.swing.*
import java.awt.Color
import config.Config.*
import scenes.behaviours.{BackgroundImage, GameButton}

object StartingMenu extends Scene:
  import Privates.*

  override def apply(): Iterable[Behaviour] =
    Seq(
      GameButton(text = "Play", onButtonPressed = onPlayButton)(
        textColor = Color.GREEN
      ),
      GameButton(text = "Close", onButtonPressed = onExitButton)(
        textColor = Color.WHITE,
        position = (0, -BUTTON_OFFSET)
      ),
      BackgroundImage("menu.png")
    )

  private object Privates:
    def onPlayButton: Engine => Unit = engine =>
      engine.loadScene(DifficultyMenu)

    def onExitButton: Engine => Unit = engine => engine.stop()
