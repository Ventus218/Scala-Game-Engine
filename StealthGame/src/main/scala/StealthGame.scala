import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.*
import GameUtils.*
import java.awt.Color

@main def main =
  val io = SwingIO
    .withTitle("Stealth Game")
    .withSize(1200, 720)
    .withPixelsPerUnitRatio(10)
    .build()

  MenuScene.width = 30
  MenuScene.height = 5

  Engine(io, Storage(), fpsLimit = 60).run(MenuScene)

class ExitButton(width: Double, height: Double)(
    initX: Double = 0,
    initY: Double = 0
) extends Behaviour
    with Positionable(initX, initY)
    with Identifiable("ExitButton")
    with Button("Close")
    with RectRenderer(width, height, Color.gray):
  override def onButtonPressed: Engine => Unit = engine => engine.stop()

class PlayButton(width: Double, height: Double)(
    initX: Double = 0,
    initY: Double = 0
) extends Behaviour
    with Positionable(initX, initY)
    with Button("Play")
    with RectRenderer(width, height, Color(0, 125, 50)):
  override def onButtonPressed: Engine => Unit = engine =>
    engine.setEnableAll[DifficultyButton](true)

    engine.setEnable[ExitButton]("ExitButton", false)
    engine.disable(this)

class DifficultyButton(difficulty: String)(
    width: Double,
    height: Double,
    color: Color
)(
    initX: Double = 0,
    initY: Double = 0
) extends Behaviour(enabled = false)
    with Positionable(initX, initY)
    with RectRenderer(width, height, color)
    with Button(difficulty)

object MenuScene extends Scene:
  var width: Double = 0
  var height: Double = 0

  override def apply(): Iterable[Behaviour] = Seq(
    PlayButton(width, height)(),
    ExitButton(width, height)(initY = -height - 1),
    DifficultyButton("Easy")(width, height, Color(0, 125, 50))(initY = (height + 1) * 2),
    DifficultyButton("Normal")(width, height, Color.gray)(initY = (height + 1)),
    DifficultyButton("Hard")(width, height, Color(125, 50, 0))(),
    DifficultyButton("Impossible")(width, height, Color.red)(initY = (-height - 1)),
  )
