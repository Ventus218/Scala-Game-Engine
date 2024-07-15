import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable

object ExampleGame extends App:
  val e = Engine(
    SwingIO
      .withTitle("ExampleGame")
      .withSize(400, 400)
      .withPixelsPerUnitRatio(10)
      .build(),
    Storage(),
    5
  ).run(() =>
    Seq(
      new Behaviour
        with Positionable
        with ImageRenderer("epic-crocodile.png", 10, 10)
    )
  )
  println("ExampleGame")
