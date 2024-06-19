import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.{Color, Graphics2D}
import scala.reflect.TypeTest

object SwingIOTest:
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
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withTitle("Swing Test")
        .build()

    frame.draw(rectRenderer(0, 0))
    frame.show()

  @main def testSwingIOUpdate(): Unit =
    // it should allow to render multiple times in sequence
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withTitle("Swing Test")
        .build()

    for i <- 0 to 300 by 10 do
      frame.draw(rectRenderer(i, i))
      frame.show()
      Thread.sleep(150)

  @main def testSwingIOMultipleRendering(): Unit =
    // it should draw multiple renderers
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withTitle("Swing Test")
        .build()

    frame.draw(rectRenderer(0, 0))
    frame.draw(circleRenderer(100, 100))
    frame.draw(rectRenderer(300, 0))
    frame.draw(circleRenderer(50, 200))
    frame.show()

class SwingIOTest extends AnyFlatSpec:

  "SwingIO" should "be an IO class" in:
    SwingIO("SwingTest", size = (500, 500)) shouldBe a [IO]

  it should "always have positive size" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingIO("SwingTest", size = (-100, 500))
    }

  it should "always have positive pixels/unit ratio" in :
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingIO("SwingTest", size = (500, 500), pixelsPerUnit = -20)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
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