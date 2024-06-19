import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.{Color, Graphics2D}

private val rectRenderer: Graphics2D => Unit = g => {g.setColor(Color.red); g.fillRect(0, 0, 50, 50)}
@main def testSwingIOcreation(): Unit =
  SwingIO("Swing Test", size = (800, 800))

@main def testSwingIOrendering(): Unit =
  val frame: SwingIO =
    SwingIO
      .withSize((400, 400))
      .withTitle("Swing Test")
      .build()

  frame.draw(rectRenderer)
  frame.show()

class SwingIOTest extends AnyFlatSpec:

  "SwingIO" should "be an IO class" in:
    SwingIO("SwingTest", size = (500, 500)) shouldBe a [IO]

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