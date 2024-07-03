import Dimensions2D.Positionable
import SwingRenderers.SwingTextRenderer
import SwingRenderers.Text.{FontName, TextStyle}
import SwingRendererTestUtilities.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.Color

class SwingTextRendererTest extends AnyFlatSpec:

  val font: FontName = "Arial"
  val style: TextStyle = TextStyle.Plain
  def textTest: SwingTextRenderer = textRenderer(
      "Test",
      10,
      Color.red,
      font,
      style
  )

  "SwingTextRenderer" should "be initialized correctly" in:
    val text = textTest
    text.textContent shouldBe "Test"
    text.textSize shouldBe 10
    text.textColor shouldBe Color.red
    text.textStyle shouldBe style
    text.textFont shouldBe font
    text.renderOffset shouldBe (0, 0)

  it should "not be initialized with null text or color" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      textRenderer(
        null,
        10,
        Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      textRenderer(
        "Test",
        10,
        null
      )
    }

  it should "be able to change text, size and color" in:
    val text = textTest
    text.textContent = "New Text"
    text.textContent shouldBe "New Text"
    text.textSize = 15
    text.textSize shouldBe 15
    text.textColor = Color.orange
    text.textColor shouldBe Color.orange

  it should "always have positive size" in:
    val text = textTest
    an[IllegalArgumentException] shouldBe thrownBy {
      text.textSize = -2
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      text.textSize = 0
    }

  it should "always have valid text and color" in:
    val text = textTest
    an[IllegalArgumentException] shouldBe thrownBy {
      text.textContent = null
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      text.textColor = null
    }
