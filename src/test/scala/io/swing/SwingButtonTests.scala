import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingRenderers.SwingRectRenderer
import java.awt.Color
import Dimensions2D.Positionable

class SwingButtonTests extends AnyFlatSpec:
  def button = new Behaviour
    with SwingButton
    with Positionable
    with SwingRectRenderer(100, 20, Color.gray)

  "SwingButton" should "have a rectangular shape" in:
    button.isInstanceOf[SwingRectRenderer] shouldBe true
