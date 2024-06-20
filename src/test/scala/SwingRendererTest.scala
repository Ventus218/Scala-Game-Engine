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

  def circleRenderer(radius: Double, color: Color, offset: (Double, Double) = (0, 0), position: (Double, Double) = (0, 0)): SwingCircleRenderable =
    new Behaviour
      with SwingCircleRenderable(radius, color, offset)
      with Positionable(position._1, position._2)

  /* test for shape renderable */
  def testShapeRenderableProperties(renderer: SwingShapeRenderable): Unit =
    renderer.shapeWidth = 2
    renderer.shapeWidth shouldBe 2
    renderer.shapeHeight = 1.5
    renderer.shapeHeight shouldBe 1.5
    renderer.shapeColor = Color.blue
    renderer.shapeColor shouldBe Color.blue
    renderer.renderOffset = (1, 9)
    renderer.renderOffset shouldBe(1, 9)

  /* test for shape renderable */
  def testShapeRenderableInvalidValues(renderer: SwingShapeRenderable): Unit =
    an[IllegalArgumentException] shouldBe thrownBy {
      renderer.shapeWidth = -2
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      renderer.shapeHeight = 0
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      renderer.shapeColor = null
    }

  /* visual test for swing renderable */
  private def testSwingRenderable(renderer: SwingRenderable): Unit =
    val frame: SwingIO = io.build()
    frame.draw(renderer.renderer(frame))
    frame.show()

  /* visual test for swing renderable */
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

  @main def testSwingRendererSquarePlacement(): Unit =
    val s: Double = 1
    testSwingRenderablePlacement(
      centered = squareRenderer(s, color = Color.red, offset = (0, 0), position = (0, 0)),
      topLeft = squareRenderer(s, color = Color.blue, offset = (0, 0), position = (-2 + s / 2, 2 - s / 2)),
      topRight = squareRenderer(s, color = Color.green, offset = (2 - s / 2, 2 - s / 2), position = (0, 0))
    )

  @main def testSwingRendererCircle(): Unit =
    // it should display a circle
    testSwingRenderable:
      circleRenderer(radius = 0.7, color = Color.magenta, offset = (0, 0), position = (0, 0))

  @main def testSwingRendererCirclePlacement(): Unit =
    val r: Double = 0.3
    testSwingRenderablePlacement(
      centered = circleRenderer(r, color = Color.red, offset = (0, 0), position = (0, 0)),
      topLeft = circleRenderer(r, color = Color.blue, offset = (0, 0), position = (-2 + r, 2 - r)),
      topRight = circleRenderer(r, color = Color.green, offset = (2 - r, 2 - r), position = (0, 0))
    )

class SwingRectRenderableTest extends AnyFlatSpec:

  "Swing Rectangle" should "be initialized correctly" in:
    val rect = SwingRendererTest.rectRenderer(width = 1, height = 2, color = Color.red, offset = (0, 0))
    rect.shapeWidth shouldBe 1
    rect.shapeHeight shouldBe 2
    rect.shapeColor shouldBe Color.red
    rect.renderOffset shouldBe (0, 0)

  "Swing Rectangle" should "not be initialized with negative sizes" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = 0, height = 0, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = -4, height = 5, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = 2, height = -6, color = Color.red)
    }

  "Swing Rectangle" should "not be initialized with null color" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.rectRenderer(width = 1, height = 1, color = null)
    }

  "Swing Rectangle" should "be able to change its properties" in:
    val rect = SwingRendererTest.rectRenderer(width = 1, height = 2, color = Color.red, offset = (0, 0))
    SwingRendererTest.testShapeRenderableProperties(rect)

  "Swing Rectangle" should "not be able to change its properties to invalid values" in:
    val rect = SwingRendererTest.rectRenderer(width = 1, height = 2, color = Color.red, offset = (0, 0))
    SwingRendererTest.testShapeRenderableInvalidValues(rect)

class SwingOvalRenderableTest extends AnyFlatSpec:

  "Swing Oval" should "be initialized correctly" in :
    val oval = SwingRendererTest.ovalRenderer(width = 1, height = 2, color = Color.blue, offset = (0, 0))
    oval.shapeWidth shouldBe 1
    oval.shapeHeight shouldBe 2
    oval.shapeColor shouldBe Color.blue
    oval.renderOffset shouldBe (0, 0)

  "Swing Oval" should "not be initialized with negative sizes" in :
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = 0, height = 1, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = -4, height = 5, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = 2, height = -6, color = Color.red)
    }

  "Swing Oval" should "not be initialized with null color" in :
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.ovalRenderer(width = 1, height = 1, color = null)
    }

  "Swing Oval" should "be able to change its properties" in :
    val oval = SwingRendererTest.ovalRenderer(width = 1, height = 2, color = Color.red, offset = (0, 0))
    SwingRendererTest.testShapeRenderableProperties(oval)

  "Swing Oval" should "not be able to change its properties to invalid values" in :
    val oval = SwingRendererTest.ovalRenderer(width = 1, height = 2, color = Color.red, offset = (0, 0))
    SwingRendererTest.testShapeRenderableInvalidValues(oval)

class SwingSquareRenderableTest extends AnyFlatSpec:

  "Swing Square" should "be initialized correctly" in:
    val square = SwingRendererTest.squareRenderer(size = 1, color = Color.red, offset = (0, 0))
    square.shapeWidth shouldBe 1
    square.shapeHeight shouldBe 1
    square.shapeColor shouldBe Color.red
    square.renderOffset shouldBe (0, 0)

  "Swing Square" should "not be initialized with negative sizes" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.squareRenderer(size = 0, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.squareRenderer(size = -4, color = Color.red)
    }

  "Swing Square" should "not be initialized with null color" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.squareRenderer(size = 1, color = null)
    }

  "Swing Square" should "be able to change its properties" in:
    val square = SwingRendererTest.squareRenderer(size = 1, color = Color.red, offset = (0, 0))
    SwingRendererTest.testShapeRenderableProperties(square)

  "Swing Square" should "not be able to change its properties to invalid values" in:
    val square = SwingRendererTest.squareRenderer(size = 1, color = Color.red, offset = (0, 0))
    SwingRendererTest.testShapeRenderableInvalidValues(square)

  "Swing Square" should "always have the same width and height" in:
    val square = SwingRendererTest.squareRenderer(size = 1, color = Color.red)
    for i <- 1 to 10 do
      square.shapeWidth = i
      square.shapeWidth shouldBe square.shapeHeight

class SwingCircleRenderableTest extends AnyFlatSpec:

  "Swing Circle" should "be initialized correctly" in:
    val circle = SwingRendererTest.circleRenderer(radius = 1, color = Color.red, offset = (0, 0))
    circle.shapeWidth shouldBe 2
    circle.shapeHeight shouldBe 2
    circle.shapeRadius shouldBe 1
    circle.shapeColor shouldBe Color.red
    circle.renderOffset shouldBe (0, 0)

  "Swing Circle" should "not be initialized with negative sizes" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.circleRenderer(radius = 0, color = Color.red)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.circleRenderer(radius = -4, color = Color.red)
    }

  "Swing Circle" should "not be initialized with null color" in:
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTest.circleRenderer(radius = 1, color = null)
    }

  "Swing Circle" should "be able to change its properties" in:
    val circle = SwingRendererTest.circleRenderer(radius = 1, color = Color.red, offset = (0, 0))
    SwingRendererTest.testShapeRenderableProperties(circle)

  "Swing Circle" should "not be able to change its properties to invalid values" in:
    val circle = SwingRendererTest.circleRenderer(radius = 1, color = Color.red, offset = (0, 0))
    SwingRendererTest.testShapeRenderableInvalidValues(circle)

  "Swing Circle" should "always have the radius be half the width and height" in :
    val circle = SwingRendererTest.circleRenderer(radius = 1, color = Color.red)
    for i <- 1 to 10 do
      circle.shapeRadius = i
      circle.shapeRadius shouldBe circle.shapeWidth / 2
      circle.shapeRadius shouldBe circle.shapeHeight / 2
