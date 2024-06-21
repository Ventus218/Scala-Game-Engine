import SwingRendererTestUtilities.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.awt.Color

object SwingOvalRendererTest:
  @main def testSwingRendererOval(): Unit =
    // it should display an oval
    testSwingRenderer:
      ovalRenderer(
        width = 1.2,
        height = 2,
        color = Color.blue,
        offset = (0, 0),
        position = (0, 0)
      )

  @main def testSwingRendererOvalPlacement(): Unit =
    val w: Double = 0.5
    val h: Double = 0.3
    testSwingRendererPlacement(
      centered = ovalRenderer(
        w,
        h,
        color = Color.red,
        offset = (0, 0),
        position = (0, 0)
      ),
      topLeft = ovalRenderer(
        w,
        h,
        color = Color.blue,
        offset = (0, 0),
        position = (-2 + w / 2, 2 - h / 2)
      ),
      topRight = ovalRenderer(
        w,
        h,
        color = Color.green,
        offset = (2 - w / 2, 2 - h / 2),
        position = (0, 0)
      )
    )

class SwingOvalRendererTest extends AnyFlatSpec:

  "Swing Oval" should "be initialized correctly" in:
    val oval = SwingRendererTestUtilities.ovalRenderer(
      width = 1,
      height = 2,
      color = Color.blue,
      offset = (0, 0)
    )
    oval.shapeWidth shouldBe 1
    oval.shapeHeight shouldBe 2
    oval.shapeColor shouldBe Color.blue
    oval.renderOffset shouldBe (0, 0)

  "Swing Oval" should "not be initialized with negative sizes" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.ovalRenderer(
        width = 0,
        height = 1,
        color = Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.ovalRenderer(
        width = -4,
        height = 5,
        color = Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.ovalRenderer(
        width = 2,
        height = -6,
        color = Color.red
      )
    }

  "Swing Oval" should "not be initialized with null color" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.ovalRenderer(
        width = 1,
        height = 1,
        color = null
      )
    }

  "Swing Oval" should "be able to change its properties" in:
    val oval = SwingRendererTestUtilities.ovalRenderer(
      width = 1,
      height = 2,
      color = Color.red,
      offset = (0, 0)
    )
    SwingRendererTestUtilities.testShapeProperties(oval)

  "Swing Oval" should "not be able to change its properties to invalid values" in:
    val oval = SwingRendererTestUtilities.ovalRenderer(
      width = 1,
      height = 2,
      color = Color.red,
      offset = (0, 0)
    )
    SwingRendererTestUtilities.testShapeInvalidValues(oval)
