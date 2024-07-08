import sge.swing.*
import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import java.awt.Color
import Config.*
import GameBehaviours.GameButton
import scala.languageFeature.postfixOps

object DifficultyScene extends Scene:
  import Privates.*
  override def apply(): Iterable[Behaviour] =
    Seq(
      GameButton(text = "Easy", onButtonPressed = _ => ())(
        buttonColor = GREEN,
        position = (0, BUTTON_OFFSET * 2)
      ),
      GameButton(text = "Normal", onButtonPressed = _ => ())(
        buttonColor = GREEN,
        position = (0, BUTTON_OFFSET)
      ),
      GameButton(text = "Hard", onButtonPressed = _ => ())(
        buttonColor = BROWN
      ),
      GameButton(text = "Impossible", onButtonPressed = _ => ())(
        buttonColor = Color.red,
        position = (0, -BUTTON_OFFSET)
      ),
      GameButton(text = "Back", onButtonPressed = onBackButton)(
        textColor = Color.WHITE,
        buttonColor = Color.BLACK,
        position = (0, -BUTTON_OFFSET * 3)
      )
    )

  private object Privates:
    def onBackButton: Engine => Unit = engine =>
      import GameUtils.*
      engine.loadScene(MenuScene)
