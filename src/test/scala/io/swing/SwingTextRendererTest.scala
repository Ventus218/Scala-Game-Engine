import SwingRendererTestUtilities.*
import SwingRenderers.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.{Color, Font}

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

  val font: Font = Font("Arial", Font.PLAIN, 10)

  "SwingTextRenderer" should "be initialized correctly" in:
    val text = new Behaviour
      with SwingTextRenderer(
      "Test",
      font,
      Color.red
    )
    text.textContent shouldBe "Test"
    text.textSize shouldBe 10
    text.textColor shouldBe Color.red
    text.textOffset shouldBe (0, 0)
    text.textAnchor shouldBe TextAnchor.TopLeft

  it should "not be initialized with null text, color or font" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      new Behaviour with SwingTextRenderer(
        null,
        font,
        Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      new Behaviour with SwingTextRenderer(
        "Test",
        null,
        Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      new Behaviour with SwingTextRenderer(
        "Test",
        font,
        null
      )
    }

  it should "be able to change text, size and color"

  it should "always have positive size"

  it should "always have valid text and color"

  it should "be able to change its anchor point"