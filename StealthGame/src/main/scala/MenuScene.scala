import sge.core.*
import sge.core.behaviours.dimension2d.*
import sge.swing.*
import java.awt.Color
import Config.*

object MenuScene extends Scene:
  import Privates.*

  override def apply(): Iterable[Behaviour] =
    Seq(
      PlayButton(),
      ExitButton(initY = -offsetFromOtherButton)
    )

  private object Privates:
    class ExitButton(
        initX: Double = 0,
        initY: Double = 0
    ) extends Behaviour
        with Positionable(initX, initY)
        with Button("Close", _textColor = Color.WHITE)
        with RectRenderer(buttonsWidth, buttonsHeight, Color.BLACK):
      override def onButtonPressed: Engine => Unit = engine => engine.stop()

    class PlayButton(
        initX: Double = 0,
        initY: Double = 0
    ) extends Behaviour
        with Positionable(initX, initY)
        with Button("Play")
        with RectRenderer(buttonsWidth, buttonsHeight, green):
      override def onButtonPressed: Engine => Unit = engine =>
        import GameUtils.*
        engine.loadScene(DifficultyScene)
