package sge.swing.behaviours.ingame

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.behaviours.RendererTestUtilities
import RendererTestUtilities.*
import java.awt.Color

object CircleRendererTests:
  @main def testRendererCircle(): Unit =
    // it should display a circle
    testRenderer:
      circleRenderer(
        radius = 0.7,
        color = Color.magenta,
        offset = (0, 0),
        position = (0, 0)
      )

  @main def testRendererCirclePlacement(): Unit =
    val r: Double = 0.3
    testRendererPlacement(
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

class CircleRendererTests extends AnyFlatSpec:

  "Circle" should "be initialized correctly" in:
    val circle = RendererTestUtilities.circleRenderer(
      radius = 1,
      color = Color.red,
      offset = (0, 0)
    )
    circle.shapeWidth shouldBe 2
    circle.shapeHeight shouldBe 2
    circle.shapeRadius shouldBe 1
    circle.shapeColor shouldBe Color.red
    circle.renderOffset shouldBe (0, 0)

  it should "not be initialized with negative sizes" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      RendererTestUtilities.circleRenderer(radius = 0, color = Color.red)
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      RendererTestUtilities.circleRenderer(radius = -4, color = Color.red)
    }

  it should "not be initialized with null color" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      RendererTestUtilities.circleRenderer(radius = 1, color = null)
    }

  it should "be able to change its properties" in:
    val circle = RendererTestUtilities.circleRenderer(
      radius = 1,
      color = Color.red,
      offset = (0, 0)
    )
    RendererTestUtilities.testShapeProperties(circle)

  it should "not be able to change its properties to invalid values" in:
    val circle = RendererTestUtilities.circleRenderer(
      radius = 1,
      color = Color.red,
      offset = (0, 0)
    )
    RendererTestUtilities.testShapeInvalidValues(circle)

  it should "always have the radius be half the width and height" in:
    val circle =
      RendererTestUtilities.circleRenderer(radius = 1, color = Color.red)
    for i <- 1 to 10 do
      circle.shapeRadius = i
      circle.shapeRadius shouldBe circle.shapeWidth / 2
      circle.shapeRadius shouldBe circle.shapeHeight / 2
