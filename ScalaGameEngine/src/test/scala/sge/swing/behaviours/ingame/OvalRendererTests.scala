package sge.swing.behaviours.ingame

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.behaviours.RendererTestUtilities
import RendererTestUtilities.*
import java.awt.Color

object OvalRendererTests:
  @main def testRendererOval(): Unit =
    // it should display an oval
    testRenderer:
      ovalRenderer(
        width = 1.2,
        height = 2,
        color = Color.blue,
        offset = (0, 0),
        position = (0, 0)
      )

  @main def testRendererOvalPlacement(): Unit =
    val w: Double = 0.5
    val h: Double = 0.3
    testRendererPlacement(
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

class OvalRendererTests extends AnyFlatSpec:

  "Oval" should "be initialized correctly" in:
    val oval = RendererTestUtilities.ovalRenderer(
      width = 1,
      height = 2,
      color = Color.blue,
      offset = (0, 0)
    )
    oval.shapeWidth shouldBe 1
    oval.shapeHeight shouldBe 2
    oval.shapeColor shouldBe Color.blue
    oval.renderOffset shouldBe (0, 0)

  it should "not be initialized with negative sizes" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      RendererTestUtilities.ovalRenderer(
        width = 0,
        height = 1,
        color = Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      RendererTestUtilities.ovalRenderer(
        width = -4,
        height = 5,
        color = Color.red
      )
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      RendererTestUtilities.ovalRenderer(
        width = 2,
        height = -6,
        color = Color.red
      )
    }

  it should "not be initialized with null color" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      RendererTestUtilities.ovalRenderer(
        width = 1,
        height = 1,
        color = null
      )
    }

  it should "be able to change its properties" in:
    val oval = RendererTestUtilities.ovalRenderer(
      width = 1,
      height = 2,
      color = Color.red,
      offset = (0, 0)
    )
    RendererTestUtilities.testShapeProperties(oval)

  it should "not be able to change its properties to invalid values" in:
    val oval = RendererTestUtilities.ovalRenderer(
      width = 1,
      height = 2,
      color = Color.red,
      offset = (0, 0)
    )
    RendererTestUtilities.testShapeInvalidValues(oval)
