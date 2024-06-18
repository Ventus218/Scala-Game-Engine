import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
class SwingIOTest extends AnyFlatSpec:

  "SwingIO" should "be an IO class" in:
    SwingIO("SwingTest", size = (500, 500)) shouldBe a [IO]

  "SwingIO" should "create a GUI on creation" in :
    SwingIO("SwingTest", size = (800, 800), pixelsPerUnit = 100, center = (0, 0))
    Thread.sleep(3000)
    // test done through visual interface
