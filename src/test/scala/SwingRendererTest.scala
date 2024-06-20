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

  def squareRenderer(size: Double, color: Color, offset: (Double, Double) = (0, 0), position: (Double, Double) = (0, 0)): SwingSquareRenderable =
    new Behaviour
      with SwingSquareRenderable(size, color, offset)
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

  @main def testSwingRendererSquare(): Unit =
    // it should display a square
    testSwingRenderable:
      squareRenderer(size = 2, color = Color.orange, offset = (0, 0), position = (0, 0))

class SwingRendererTest extends AnyFlatSpec:

  private def testShapeRenderable(renderer: SwingShapeRenderable): Unit =
    renderer.shapeWidth = 2
    renderer.shapeWidth shouldBe 2
    renderer.shapeHeight = 1.5
    renderer.shapeHeight shouldBe 1.5
    renderer.shapeColor = Color.blue
    renderer.shapeColor shouldBe Color.blue
    renderer.renderOffset = (1, 9)
    renderer.renderOffset shouldBe (1, 9)

    an [IllegalArgumentException] shouldBe thrownBy {
      renderer.shapeWidth = -2
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      renderer.shapeHeight = 0
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      renderer.shapeColor = null
    }


  "Swing Rectangle" should "safely change its properties" in:
    val rect = SwingRendererTest.rectRenderer(width = 1, height = 2, color = Color.red, offset = (0, 0), position = (0, 0))

    rect.shapeWidth shouldBe 1
    rect.shapeHeight shouldBe 2
    rect.shapeColor shouldBe Color.red

    testShapeRenderable(rect)

  "Swing Rectangle" should "have positive sizes" in :
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = 0, height = 1, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = -4, height = 5, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = 2, height = -6, color = Color.red)
    }


  "Swing Oval" should "have positive sizes" in :
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = 0, height = 1, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = -4, height = 5, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = 2, height = -6, color = Color.red)
    }

  "Swing Oval" should "change its properties" in :
    val oval = SwingRendererTest.ovalRenderer(width = 1, height = 2, color = Color.blue, offset = (0, 0), position = (0, 0))

    oval.shapeWidth shouldBe 1
    oval.shapeHeight shouldBe 2
    oval.shapeColor shouldBe Color.blue

    testShapeRenderable(oval)