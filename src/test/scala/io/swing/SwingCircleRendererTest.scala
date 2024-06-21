import SwingRendererTestUtilities.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.Color

object SwingCircleRendererTest:
  @main def testSwingRendererCircle(): Unit =
    // it should display a circle
    testSwingRenderer:
      circleRenderer(
        radius = 0.7,
        color = Color.magenta,
        offset = (0, 0),
        position = (0, 0)
      )

  @main def testSwingRendererCirclePlacement(): Unit =
    val r: Double = 0.3
    testSwingRendererPlacement(
      centered = circleRenderer(
        r,
        color = Color.red,
        offset = (0, 0),
        position = (0, 0)
      ),
      topLeft = circleRenderer(
        r,
        color = Color.blue,
        offset = (0, 0),
        position = (-2 + r, 2 - r)
      ),
      topRight = circleRenderer(
        r,
        color = Color.green,
        offset = (2 - r, 2 - r),
        position = (0, 0)
      )
    )

class SwingCircleRendererTest extends AnyFlatSpec:

  "Swing Circle" should "be initialized correctly" in:
    val circle = SwingRendererTestUtilities.circleRenderer(
      radius = 1,
      color = Color.red,
      offset = (0, 0)
    )
    circle.shapeWidth shouldBe 2
    circle.shapeHeight shouldBe 2
    circle.shapeRadius shouldBe 1
    circle.shapeColor shouldBe Color.red
    circle.renderOffset shouldBe (0, 0)

  "Swing Circle" should "not be initialized with negative sizes" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.circleRenderer(radius = 0, color = Color.red)
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.circleRenderer(radius = -4, color = Color.red)
    }

  "Swing Circle" should "not be initialized with null color" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.circleRenderer(radius = 1, color = null)
    }

  "Swing Circle" should "be able to change its properties" in:
    val circle = SwingRendererTestUtilities.circleRenderer(
      radius = 1,
      color = Color.red,
      offset = (0, 0)
    )
    SwingRendererTestUtilities.testShapeProperties(circle)

  "Swing Circle" should "not be able to change its properties to invalid values" in:
    val circle = SwingRendererTestUtilities.circleRenderer(
      radius = 1,
      color = Color.red,
      offset = (0, 0)
    )
    SwingRendererTestUtilities.testShapeInvalidValues(circle)

  "Swing Circle" should "always have the radius be half the width and height" in:
    val circle =
      SwingRendererTestUtilities.circleRenderer(radius = 1, color = Color.red)
    for i <- 1 to 10 do
      circle.shapeRadius = i
      circle.shapeRadius shouldBe circle.shapeWidth / 2
      circle.shapeRadius shouldBe circle.shapeHeight / 2
