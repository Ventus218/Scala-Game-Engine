package sge.swing

import sge.core.*
import behaviours.dimension2d.*
import java.awt.Color
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.EngineWithSwingIOTest.ioBuilder
import sge.core.mocks.IOMock
import sge.testing.*

object EngineWithSwingIOTest:
  trait MoveX(velocityX: Double) extends Behaviour with Positionable:
    override def onUpdate: Engine => Unit = e =>
      val dx = velocityX * e.deltaTimeNanos * Math.pow(10, -9)
      position = position + Versor2D.x * dx
      super.onUpdate(e)

  val ioBuilder: SwingIO.SwingIOBuilder = SwingIO
    .withTitle("Engine with SwingIO Test")
    .withSize((600, 600))
    .withPixelsPerUnitRatio(50)

  val redRect = new Behaviour
    with MoveX(1)
    with RectRenderer(3, 2, Color.red)
    with Positionable(-5, 1)

  val blueCircle = new Behaviour
    with MoveX(1)
    with CircleRenderer(1, Color.blue)
    with Positionable(-5, 0)

  @main def runEngineWithSwingIO(): Unit =
    val engine = Engine(ioBuilder.build(), Storage())
    val scene: Scene = () => Seq(redRect, blueCircle)

    engine.run(scene)

  @main def testSwingRendererPriority(): Unit =
    val engine = Engine(ioBuilder.build(), Storage())
    val scene: Scene = () => Seq(redRect, blueCircle)

    redRect.renderingPriority = 5
    blueCircle.renderingPriority = -5

    engine.run(scene)

class EngineWithSwingIOTest extends AnyFlatSpec:
  import sge.testing.behaviours.NFrameStopper
  "Engine" should "call IO.onEngineStop when stopped" in:
    val io = IOMock()
    val emptyScene = () => Seq()
    val engine = Engine(io, Storage())

    io.isStopped shouldBe false

    test(engine) on emptyScene soThat (builder => builder)
    
    io.isStopped shouldBe true
