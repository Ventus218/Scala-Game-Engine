import sge.swing.*
import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import java.awt.Color
import Config.*

object DifficultyScene extends Scene:
  import Privates.*
  override def apply(): Iterable[Behaviour] =
    Seq(
      DifficultyButton("Easy", green)(initY = offsetFromOtherButton * 2),
      DifficultyButton("Normal", green)(initY = offsetFromOtherButton),
      DifficultyButton("Hard", brown)(),
      DifficultyButton("Impossible", Color.red)(initY = -offsetFromOtherButton),
      BackButton(initY = -offsetFromOtherButton * 3)
    )

  private object Privates:
    class DifficultyButton(difficulty: String, color: Color)(
        initX: Double = 0,
        initY: Double = 0
    ) extends Behaviour
        with Positionable(initX, initY)
        with RectRenderer(buttonsWidth, buttonsHeight, color)
        with Button(difficulty)

    class BackButton(initX: Double = 0, initY: Double = 0)
        extends Behaviour
        with Positionable(initX, initY)
        with RectRenderer(buttonsWidth, buttonsHeight, Color.BLACK)
        with Button("Go back", _textColor = Color.WHITE):
      override def onButtonPressed: Engine => Unit = engine =>
        import GameUtils.*
        engine.loadScene(MenuScene)
