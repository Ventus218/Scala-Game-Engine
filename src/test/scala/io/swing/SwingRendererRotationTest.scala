import SwingRendererTestUtilities.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingRenderers.Angle.*

import java.awt.Color

object SwingRendererRotationTest:
  @main def testSwingSquareRotation(): Unit =
    // it should display a rotated square
    testSwingRenderer:
      squareRenderer(
        size = 2,
        color = Color.orange,
        offset = (0, 0),
        rotation = 45.degrees,
        position = (1, 0)
      )
  @main def testSwingImageRotation(): Unit =
    // it should display a rotated image
    testSwingRenderer:
      imageRenderer(
        "epic-crocodile.png",
        3,
        3,
        offset = (0, 0),
        rotation = 120.degrees,
        position = (0, 0)
      )

  @main def testSwingSquareMultipleRotations(): Unit =
    // it should display several rotated squares
    val s: Double = 1
    testSwingRendererPlacement(
      centered = squareRenderer(
        s,
        color = Color.red,
        offset = (0, 0),
        rotation = 30.degrees,
        position = (0, 0)
      ),
      topLeft = squareRenderer(
        s,
        color = Color.blue,
        offset = (0, 2),
        rotation = 45.degrees,
        position = (0, 0)
      ),
      topRight = squareRenderer(
        s,
        color = Color.green,
        offset = (0, 0),
        rotation = 0.degrees,
        position = (-1, -1)
      )
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