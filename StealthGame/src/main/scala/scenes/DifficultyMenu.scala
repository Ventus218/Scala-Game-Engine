package scenes

import sge.swing.*
import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import config.*
import Config.*
import Difficulty.*
import java.awt.Color
import scenes.behaviours.{GameButton, BackgroundImage}
import levels.LevelOne

/** Scene containing the items of the difficulty menu in order to chose between
  * Easy, Normal, Hard and Impossible difficulty, plus a button to go back to
  * the main menu
  */
object DifficultyMenu extends Scene:
  import Privates.*
  override def apply(): Iterable[Behaviour] =
    Seq(
      GameButton(text = EASY.text, onButtonPressed = onDifficultyButton(EASY))(
        textColor = Color.GREEN,
        position = (0, BUTTON_OFFSET * 2)
      ),
      GameButton(
        text = NORMAL.text,
        onButtonPressed = onDifficultyButton(NORMAL)
      )(
        textColor = Color.GREEN,
        position = (0, BUTTON_OFFSET)
      ),
      GameButton(text = HARD.text, onButtonPressed = onDifficultyButton(HARD))(
        textColor = Color.ORANGE
      ),
      GameButton(
        text = IMPOSSIBLE.text,
        onButtonPressed = onDifficultyButton(IMPOSSIBLE)
      )(
        textColor = Color.RED,
        position = (0, -BUTTON_OFFSET)
      ),
      GameButton(text = "Back", onButtonPressed = onBackButton)(
        textColor = Color.WHITE,
        position = (0, -BUTTON_OFFSET * 3)
      ),
      BackgroundImage("menu.png")
    )

  private object Privates:
    def onBackButton: Engine => Unit = engine => engine.loadScene(StartingMenu)
    def onDifficultyButton(difficulty: Difficulty): Engine => Unit = engine =>
      engine.storage.set("Lifes", difficulty.lifes)
      engine.storage.set("Difficulty", difficulty)

      engine.loadScene(LevelOne)
