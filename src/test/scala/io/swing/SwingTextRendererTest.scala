import SwingRendererTestUtilities.*
import SwingRenderers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.Color

object SwingTextRendererTest:

  @main def testSwingRendererText(): Unit =
    testSwingRenderer:
      textRenderer(
        "Hello World!",
        20,
        Color.blue
      )

  @main def testSwingRendererTextPlacement(): Unit =
    testSwingRendererPlacement(
      centered = textRenderer(
        "Hello World!",
        20,
        Color.red,
        offset = (200, 200)
      ),
      topLeft = textRenderer(
        "Ciao Mondo!",
        14,
        Color.blue,
        offset = (0, 0)
      ),
      topRight = textRenderer(
        "Hola Mundo!",
        17,
        Color.green,
        offset = (400, 0)
      )
    )

class SwingTextRendererTest extends AnyFlatSpec:

  "SwingTextRenderer" should "be initialized correctly" in:
    val text = SwingRendererTestUtilities.textRenderer(
      "Hello World!",
      20,
      Color.red,
      offset = (0, 0)
    )
    text.textContent shouldBe "Hello World!"
    text.textSize shouldBe 20
    text.textColor shouldBe Color.red
    text.textOffset shouldBe (0, 0)
    text.textAnchor shouldBe TextAnchor.TopLeft

  it should "not be initialized with null text, color or font"

  it should "be able to change text, size and color"

  it should "always have positive size"

  it should "always have valid text and color"

  it should "be able to change its anchor point"