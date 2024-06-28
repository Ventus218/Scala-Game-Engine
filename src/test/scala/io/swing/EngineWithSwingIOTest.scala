import SwingRenderers.*

import java.awt.Color
import Dimensions2D.Positionable
object EngineWithSwingIOTest:

  trait MoveX(velocityX: Double) extends Behaviour with Positionable:
    override def onUpdate: Engine => Unit = e =>
      val dx = velocityX * e.deltaTimeNanos * Math.pow(10, -9)
      x += dx
      super.onUpdate(e)

  val ioBuilder: SwingIO.SwingIOBuilder = SwingIO
    .withTitle("Engine with SwingIO Test")
    .withSize((600, 600))
    .withPixelsPerUnitRatio(50)

  val redRect = new Behaviour
    with MoveX(1)
    with SwingRectRenderer(3, 2, Color.red)
    with Positionable(-5, 1)

  val blueCircle = new Behaviour
    with MoveX(1)
    with SwingCircleRenderer(1, Color.blue)
    with Positionable(-5, 0)

  @main def runEngineWithSwingIO(): Unit =
    val engine = Engine(ioBuilder.build(), Storage())
    val scene: Scene = () => Seq(redRect, blueCircle)

    engine.run(scene)