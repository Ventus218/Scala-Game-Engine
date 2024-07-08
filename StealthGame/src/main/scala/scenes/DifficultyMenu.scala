package scenes

import sge.swing.*
import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import config.*
import gamebehaviours.GameButton
import Config.*
import Difficulty.*
import java.awt.Color

object DifficultyMenu extends Scene:
  import Privates.*
  override def apply(): Iterable[Behaviour] =
    Seq(
      GameButton(text = EASY.text, onButtonPressed = onDifficultyButton(EASY))(
        buttonColor = GREEN,
        position = (0, BUTTON_OFFSET * 2)
      ),
      GameButton(text = NORMAL.text, onButtonPressed = onDifficultyButton(NORMAL))(
        buttonColor = GREEN,
        position = (0, BUTTON_OFFSET)
      ),
      GameButton(text = HARD.text, onButtonPressed = onDifficultyButton(HARD))(
        buttonColor = BROWN
      ),
      GameButton(text = IMPOSSIBLE.text, onButtonPressed = onDifficultyButton(IMPOSSIBLE))(
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
    def onBackButton: Engine => Unit = engine => engine.loadScene(StartingMenu)
    def onDifficultyButton(difficulty: Difficulty): Engine => Unit = engine =>
      engine.storage.set("Difficulty", difficulty)
      engine.loadScene(LevelOne)
