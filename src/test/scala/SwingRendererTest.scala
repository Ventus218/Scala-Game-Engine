import Behaviours.Positionable
import SwingRendererTest.rectRenderer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingRenderers.*

import java.awt.Color

object SwingRendererTest:

  private val io = SwingIO
    .withSize((400, 400))
    .withTitle("Swing Test")

  def rectRenderer(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0), position: (Double, Double) = (0, 0)): SwingRectRenderable =
    new Behaviour
      with SwingRectRenderable(width, height, color, offset)
      with Positionable(position._1, position._2)

  @main def testSwingRendererRect(): Unit =
    // it should display a rectangle
    val rect: SwingRenderable =
      rectRenderer(width = 1.5, height = 1, color = Color.red, offset = (0, 0), position = (0, 0))

    val frame: SwingIO = io.build()
    frame.draw(rect.renderer(frame))
    frame.show()

class SwingRendererTest extends AnyFlatSpec:

  "Swing Rectangle" should "have positive sizes" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      rectRenderer(width = 0, height = 1, color = Color.red, offset = (0, 0), position = (0, 0))
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      rectRenderer(width = -4, height = 5, color = Color.red, offset = (0, 0), position = (0, 0))
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      rectRenderer(width = 2, height = -6, color = Color.red, offset = (0, 0), position = (0, 0))
    }