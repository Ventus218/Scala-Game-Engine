import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.{Color, Graphics2D}
import scala.reflect.TypeTest
import SwingIO.*
import scala.compiletime.ops.double

object SwingIOTest:

  val io: SwingIO.SwingIOBuilder =
    SwingIO
      .withSize((400, 400))
      .withTitle("Swing Test")

  private def rectRenderer(posX: Int, posY: Int): Graphics2D => Unit = g =>
    g.setColor(Color.red)
    g.fillRect(posX, posY, 50, 50)

  private def circleRenderer(posX: Int, posY: Int): Graphics2D => Unit = g =>
    g.setColor(Color.blue)
    g.fillOval(posX, posY, 60, 60)

  @main def testSwingIOCreation(): Unit =
    // it should create a visual interface
    SwingIO("Swing Test", size = (800, 800))

  @main def testSwingIORendering(): Unit =
    // it should allow to draw on the UI
    val frame: SwingIO = io.build()

    frame.draw(rectRenderer(0, 0))
    frame.show()

  @main def testSwingIOUpdate(): Unit =
    // it should allow to render multiple times in sequence
    val frame: SwingIO = io.build()

    for i <- 0 to 300 by 10 do
      frame.draw(rectRenderer(i, i))
      frame.show()
      Thread.sleep(150)

  @main def testSwingIOMultipleRendering(): Unit =
    // it should draw multiple renderers
    val frame: SwingIO = io.build()

    frame.draw(rectRenderer(0, 0))
    frame.draw(circleRenderer(100, 100))
    frame.draw(rectRenderer(300, 0))
    frame.draw(circleRenderer(50, 200))
    frame.show()

  @main def testSwingIORenderingPriority(): Unit =
    // it should draw multiple renderers ordered by their priority
    val frame: SwingIO = io.build()

    frame.draw(circleRenderer(100, 100), priority = 1)
    frame.draw(rectRenderer(120, 100), priority = 0)
    frame.draw(circleRenderer(140, 50), priority = -1)
    frame.show()

  // ********** Input **********

  @main def testSwingIOReceiveInputEvents(): Unit =
    val ioFrame = SwingIO.withSize((400, 400)).build()

    val engine = EngineMock(io = ioFrame, storage = Storage())
    ioFrame.show()
    var frame = 0
    while true do
      // Change this sleep time for debugging
      Thread.sleep(10)
      ioFrame.onFrameEnd(engine)
      println(s"Frame $frame:\t${io.inputButtonWasPressed(InputButton.N_0)}")
      frame += 1

  @main def testSwingPointerPosition(): Unit =
    val ioFrame = SwingIO
      .withTitle("Swing Test")
      .withSize(800, 800)
      .withPixelsPerUnitRatio(10)
      .build()
    ioFrame.show()

    while true do
      println(ioFrame.scenePointerPosition())
      Thread.sleep(200)


class SwingIOTest extends AnyFlatSpec:

  "SwingIO" should "be an IO class" in:
    SwingIO("SwingTest", size = (500, 500)) shouldBe a[IO]

  it should "always have positive size" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingIO("SwingTest", size = (-100, 500))
    }

  it should "always have positive pixels/unit ratio" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingIO("SwingTest", size = (500, 500), pixelsPerUnit = -20)
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingIO("SwingTest", size = (500, 500), pixelsPerUnit = 0)
    }

  it should "be fully customizable" in:
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withTitle("Swing Test")
        .withCenter((0, 0))
        .withPixelsPerUnitRatio(100)
        .withBackgroundColor(Color.green)
        .build()

    frame.size shouldBe (400, 400)
    frame.title shouldBe "Swing Test"
    frame.center shouldBe (0, 0)
    frame.pixelsPerUnit shouldBe 100
    frame.backgroundColor shouldBe Color.green

  it should "work in a game coordinate system" in:
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withCenter((0, 0))
        .withPixelsPerUnitRatio(100)
        .build()

    frame.center shouldBe (0, 0)

    frame.pixelPosition(frame.center) shouldBe (200, 200)
    frame.pixelPosition((1, 1)) shouldBe (300, 100)
    frame.pixelPosition((-1, -1)) shouldBe (100, 300)

    frame.scenePosition((0, 0)) shouldBe (-2, 2)
    frame.scenePosition((400, 400)) shouldBe (2, -2)
    frame.scenePosition((0, 400)) shouldBe (-2, -2)
    frame.scenePosition((400, 0)) shouldBe (2, 2)

  it should "change its pixel/unit ratio at runtime" in:
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withPixelsPerUnitRatio(100)
        .build()

    frame.pixelsPerUnit = 10
    frame.pixelsPerUnit shouldBe 10
    frame.pixelsPerUnit = 50
    frame.pixelsPerUnit shouldBe 50

  it should "not change its pixel/unit ratio to negative or 0 values" in:
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withPixelsPerUnitRatio(100)
        .build()

    an[IllegalArgumentException] shouldBe thrownBy {
      frame.pixelsPerUnit = 0
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      frame.pixelsPerUnit = -100
    }

  it should "change its center position at runtime" in:
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withCenter((0, 0))
        .build()

    frame.center = (10, 0.5)
    frame.center shouldBe (10, 0.5)
    frame.center = (11.2, -20)
    frame.center shouldBe (11.2, -20)
