import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.Color
class SwingIOTest extends AnyFlatSpec:

  "SwingIO" should "be an IO class" in:
    SwingIO("SwingTest", size = (500, 500)) shouldBe a [IO]

  it should "create a GUI on creation" in:
    SwingIO("Swing Test", size = (800, 800), pixelsPerUnit = 100, center = (0, 0))
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

    Thread.sleep(3000)
    // test done through visual interface