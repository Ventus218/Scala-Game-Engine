import sge.core.*
import sge.core.behaviours.dimension2d.*
import sge.swing.*
import java.awt.Color

object MenuScene extends Scene:
  var buttonsWidth: Double = 0
  var buttonsHeight: Double = 0
  import Privates.*

  override def apply(): Iterable[Behaviour] =
    val offsetFromOtherButton = (buttonsHeight + 1)
    val offsetFromDifficultyButtons = 5
    Seq(
      PlayButton(),
      ExitButton(initY = -offsetFromOtherButton),
      DifficultyButton("Easy", green)(initY = offsetFromOtherButton * 2),
      DifficultyButton("Normal", green)(initY = offsetFromOtherButton),
      DifficultyButton("Hard", brown)(),
      DifficultyButton("Impossible", Color.red)(initY = -offsetFromOtherButton),
      BackButton(initY = -offsetFromOtherButton * 2 - offsetFromDifficultyButtons)
    )

  private object Privates:
    val green = Color(0, 125, 50)
    val brown = Color(125, 50, 0)

    class ExitButton(
        initX: Double = 0,
        initY: Double = 0
    ) extends Behaviour
        with Positionable(initX, initY)
        with Button("Close", _textColor = Color.WHITE)
        with RectRenderer(buttonsWidth, buttonsHeight, Color.BLACK):
      override def onButtonPressed: Engine => Unit = engine => engine.stop()

    class BackButton(initX: Double = 0, initY: Double = 0)
        extends Behaviour(enabled = false)
        with Positionable(initX, initY)
        with RectRenderer(buttonsWidth, buttonsHeight, Color.BLACK)
        with Button("Go back", _textColor = Color.WHITE):
      override def onButtonPressed: Engine => Unit = engine =>
        import GameUtils.*
        engine.setEnableAll[PlayButton](true)
        engine.setEnableAll[ExitButton](true)

        engine.setEnableAll[DifficultyButton](false)
        engine.disable(this)

    class DifficultyButton(difficulty: String, color: Color)(
        initX: Double = 0,
        initY: Double = 0
    ) extends Behaviour(enabled = false)
        with Positionable(initX, initY)
        with RectRenderer(buttonsWidth, buttonsHeight, color)
        with Button(difficulty)

    class PlayButton(
        initX: Double = 0,
        initY: Double = 0
    ) extends Behaviour
        with Positionable(initX, initY)
        with Button("Play")
        with RectRenderer(buttonsWidth, buttonsHeight, green):
      override def onButtonPressed: Engine => Unit = engine =>
        import GameUtils.*
        engine.setEnableAll[DifficultyButton](true)
        engine.setEnableAll[BackButton](true)

        engine.setEnableAll[ExitButton](false)
        engine.disable(this)
