import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.{Color, Graphics2D}
class SwingIOTest extends AnyFlatSpec:

  val rectRenderer: Graphics2D => Unit = g => {g.setColor(Color.red); g.fillRect(0, 0, 50, 50)}

  "SwingIO" should "be an IO class" in:
    SwingIO("SwingTest", size = (500, 500)) shouldBe a [IO]

  it should "create a GUI on creation" in:
    SwingIO("Swing Test", size = (800, 800))
    Thread.sleep(3000)
    // test done through visual interface

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
    Thread.sleep(3000)
    // test done through visual interface

  it should "draw the renderers" in:
    val frame: SwingIO =
      SwingIO
        .withSize((400, 400))
        .withTitle("Swing Test")
        .build()
    
    frame.draw(rectRenderer)
    frame.show()
    Thread.sleep(3000)
    // test done through visual interface