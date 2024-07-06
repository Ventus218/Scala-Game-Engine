package sge.swing.behaviours.ingame

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.behaviours.SwingRendererTestUtilities
import SwingRendererTestUtilities.*
import java.awt.Color

object SquareRendererTests:
  @main def testSwingRendererSquare(): Unit =
    // it should display a square
    testSwingRenderer:
      squareRenderer(
        size = 2,
        color = Color.orange,
        offset = (0, 0),
        position = (0, 0)
      )

  @main def testSwingRendererSquarePlacement(): Unit =
    val s: Double = 1
    testSwingRendererPlacement(
      centered = squareRenderer(
        s,
        color = Color.red,
        offset = (0, 0),
        position = (0, 0)
      ),
      topLeft = squareRenderer(
        s,
        color = Color.blue,
        offset = (0, 0),
        position = (-2 + s / 2, 2 - s / 2)
      ),
      topRight = squareRenderer(
        s,
        color = Color.green,
        offset = (2 - s / 2, 2 - s / 2),
        position = (0, 0)
      )
    )

class SquareRendererTests extends AnyFlatSpec:

  "Swing Square" should "be initialized correctly" in:
    val square = SwingRendererTestUtilities.squareRenderer(
      size = 1,
      color = Color.red,
      offset = (0, 0)
    )
    square.shapeWidth shouldBe 1
    square.shapeHeight shouldBe 1
    square.shapeColor shouldBe Color.red
    square.renderOffset shouldBe (0, 0)

  it should "not be initialized with negative sizes" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.squareRenderer(size = 0, color = Color.red)
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.squareRenderer(size = -4, color = Color.red)
    }

  it should "not be initialized with null color" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.squareRenderer(size = 1, color = null)
    }

  it should "be able to change its properties" in:
    val square = SwingRendererTestUtilities.squareRenderer(
      size = 1,
      color = Color.red,
      offset = (0, 0)
    )
    SwingRendererTestUtilities.testShapeProperties(square)

  it should "not be able to change its properties to invalid values" in:
    val square = SwingRendererTestUtilities.squareRenderer(
      size = 1,
      color = Color.red,
      offset = (0, 0)
    )
    SwingRendererTestUtilities.testShapeInvalidValues(square)

  it should "always have the same width and height" in:
    val square =
      SwingRendererTestUtilities.squareRenderer(size = 1, color = Color.red)
    for i <- 1 to 10 do
      square.shapeWidth = i
      square.shapeWidth shouldBe square.shapeHeight
