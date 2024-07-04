import SwingRendererTestUtilities.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingRenderers.Angle.*

import java.awt.Color

object SwingRendererRotationTest:
  @main def testSwingGameElementRotation(): Unit =
    // it should display a rotated square
    testSwingRenderer:
      squareRenderer(
        size = 2,
        color = Color.orange,
        offset = (0, 0),
        rotation = 30.degrees,
        position = (0, 0)
      )

class SwingRendererRotationTest extends AnyFlatSpec:

  "A game-element swing renderer" should "be able initialize its rotation correctly" in:
    val square = SwingRendererTestUtilities.squareRenderer(
      size = 2,
      color = Color.red,
      rotation = 5.degrees
    )
    square.renderRotation shouldBe 5.degrees

  it should "be able to change its rotation" in:
    val square = SwingRendererTestUtilities.squareRenderer(
      size = 2,
      color = Color.red,
    )
    square.renderRotation = 10.degrees
    square.renderRotation shouldBe 10.degrees
    Double

  "An Angle" should "be converted correctly" in:
    0.degrees shouldBe 0.radians
    180.degrees shouldBe Math.PI.radians
    -90.degrees shouldBe (-Math.PI/2).radians