package sge.swing.behaviours.ingame

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.behaviours.SwingRendererTestUtilities
import SwingRendererTestUtilities.*
import java.awt.Color

object RectRendererTests:
  @main def testSwingRendererRect(): Unit =
    // it should display a rectangle
    testSwingRenderer:
      rectRenderer(
        width = 1.5,
        height = 1,
        color = Color.red,
        offset = (0, 0),
        position = (0, 0)
      )

  @main def testSwingRendererRectPlacement(): Unit =
    val w: Double = 0.5
    val h: Double = 0.3
    testSwingRendererPlacement(
      centered = rectRenderer(
        w,
        h,
        color = Color.red,
        offset = (0, 0),
        position = (0, 0)
      ),
      topLeft = rectRenderer(
        w,
        h,
        color = Color.blue,
        offset = (0, 0),
        position = (-2 + w / 2, 2 - h / 2)
      ),
      topRight = rectRenderer(
        w,
        h,
        color = Color.green,
        offset = (2 - w / 2, 2 - h / 2),
        position = (0, 0)
      )
    )

class RectRendererTests extends AnyFlatSpec:

  "Swing Rectangle" should "be initialized correctly" in:
    val rect = SwingRendererTestUtilities.rectRenderer(
      width = 1,
      height = 2,
      color = Color.red,
      offset = (0, 0)
    )
    rect.shapeWidth shouldBe 1
    rect.shapeHeight shouldBe 2
    rect.shapeColor shouldBe Color.red
    rect.renderOffset shouldBe (0, 0)

  it should "not be initialized with negative sizes" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.rectRenderer(
        width = 0,
        height = 0,
        color = Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.rectRenderer(
        width = -4,
        height = 5,
        color = Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.rectRenderer(
        width = 2,
        height = -6,
        color = Color.red
      )
    }

  it should "not be initialized with null color" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.rectRenderer(
        width = 1,
        height = 1,
        color = null
      )
    }

  it should "be able to change its properties" in:
    val rect = SwingRendererTestUtilities.rectRenderer(
      width = 1,
      height = 2,
      color = Color.red,
      offset = (0, 0)
    )
    SwingRendererTestUtilities.testShapeProperties(rect)

  it should "not be able to change its properties to invalid values" in:
    val rect = SwingRendererTestUtilities.rectRenderer(
      width = 1,
      height = 2,
      color = Color.red,
      offset = (0, 0)
    )
    SwingRendererTestUtilities.testShapeInvalidValues(rect)
