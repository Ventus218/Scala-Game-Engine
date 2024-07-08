import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable
import java.awt.Color

object Alessandro extends App:
  val e = Engine(
    SwingIO
      .withTitle("Alessandro's game")
      .withSize(400, 400)
      .withPixelsPerUnitRatio(10)
      .build(),
    Storage(),
    60
  ).run(() =>
    Seq(
      new Behaviour with Positionable with SquareRenderer(10, Color.blue)
    )
  )
