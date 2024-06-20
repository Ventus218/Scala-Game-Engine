import Behaviours.Positionable
import SwingRendererTest.rectRenderer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingRenderers.*

import java.awt.Color

object SwingRendererTest:

  private val io = SwingIO
    .withSize((400, 400))
    .withPixelsPerUnitRatio(100)
    .withTitle("Swing Test")

  def rectRenderer(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0), position: (Double, Double) = (0, 0)): SwingRectRenderable =
    new Behaviour
      with SwingRectRenderable(width, height, color, offset)
      with Positionable(position._1, position._2)

  def ovalRenderer(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0), position: (Double, Double) = (0, 0)): SwingOvalRenderable =
    new Behaviour
      with SwingOvalRenderable(width, height, color, offset)
      with Positionable(position._1, position._2)

  private def testSwingRenderable(renderer: SwingRenderable): Unit =
    val frame: SwingIO = io.build()
    frame.draw(renderer.renderer(frame))
    frame.show()

  private def testSwingRenderablePlacement(centered: SwingRenderable, topLeft: SwingRenderable, topRight: SwingRenderable): Unit =
    val frame: SwingIO = io.build()
    frame.draw(centered.renderer(frame))
    frame.draw(topLeft.renderer(frame))
    frame.draw(topRight.renderer(frame))
    frame.show()

  @main def testSwingRendererRect(): Unit =
    // it should display a rectangle
    testSwingRenderable:
      rectRenderer(width = 1.5, height = 1, color = Color.red, offset = (0, 0), position = (0, 0))

  @main def testSwingRendererRectPlacement(): Unit =
    val w: Double = 0.5
    val h: Double = 0.3
    testSwingRenderablePlacement(
      centered = rectRenderer(w, h, color = Color.red, offset = (0, 0), position = (0, 0)),
      topLeft = rectRenderer(w, h, color = Color.blue, offset = (0, 0), position = (- 2 + w/2, 2 - h/2)),
      topRight = rectRenderer(w, h, color = Color.green, offset = (2 - w/2, 2 - h/2), position = (0, 0))
    )

  @main def testSwingRendererOval(): Unit =
    // it should display an oval
    testSwingRenderable:
      ovalRenderer(width = 1.2, height = 2, color = Color.blue, offset = (0, 0), position = (0, 0))

  @main def testSwingRendererOvalPlacement(): Unit =
    val w: Double = 0.5
    val h: Double = 0.3
    testSwingRenderablePlacement(
      centered = ovalRenderer(w, h, color = Color.red, offset = (0, 0), position = (0, 0)),
      topLeft = ovalRenderer(w, h, color = Color.blue, offset = (0, 0), position = (-2 + w / 2, 2 - h / 2)),
      topRight = ovalRenderer(w, h, color = Color.green, offset = (2 - w / 2, 2 - h / 2), position = (0, 0))
    )

class SwingRendererTest extends AnyFlatSpec:

  "Swing Rectangle" should "have positive sizes" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = 0, height = 1, color = Color.red, offset = (0, 0), position = (0, 0))
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = -4, height = 5, color = Color.red, offset = (0, 0), position = (0, 0))
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = 2, height = -6, color = Color.red, offset = (0, 0), position = (0, 0))
    }

  "Swing Oval" should "have positive sizes" in :
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = 0, height = 1, color = Color.red, offset = (0, 0), position = (0, 0))
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = -4, height = 5, color = Color.red, offset = (0, 0), position = (0, 0))
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = 2, height = -6, color = Color.red, offset = (0, 0), position = (0, 0))
    }