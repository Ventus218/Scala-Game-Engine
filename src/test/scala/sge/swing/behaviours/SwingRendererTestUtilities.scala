package sge.swing.behaviours

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.core.*
import behaviours.dimension2d.*
import metrics.Vector.*
import metrics.Angle.*
import sge.swing.*
import SwingIO.*
import output.*
import overlay.*
import Text.*
import sge.swing.behaviours.ingame.*
import sge.swing.behaviours.overlay.*
import java.awt.{Color, Font}

object SwingRendererTestUtilities:

  private val io: SwingIOBuilder = SwingIO
    .withSize((400, 400))
    .withPixelsPerUnitRatio(100)
    .withTitle("Swing Test")

  def rectRenderer(
      width: Double,
      height: Double,
      color: Color,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector = (0, 0)
  ): SwingRectRenderer =
    new Behaviour
      with SwingRectRenderer(width, height, color, offset, rotation)
      with Positionable(position.x, position.y)

  def ovalRenderer(
      width: Double,
      height: Double,
      color: Color,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector = (0, 0)
  ): SwingOvalRenderer =
    new Behaviour
      with SwingOvalRenderer(width, height, color, offset, rotation)
      with Positionable(position.x, position.y)

  def squareRenderer(
      size: Double,
      color: Color,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector = (0, 0)
  ): SwingSquareRenderer =
    new Behaviour
      with SwingSquareRenderer(size, color, offset, rotation)
      with Positionable(position.x, position.y)

  def circleRenderer(
      radius: Double,
      color: Color,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector = (0, 0)
  ): SwingCircleRenderer =
    new Behaviour
      with SwingCircleRenderer(radius, color, offset, rotation)
      with Positionable(position.x, position.y)

  def imageRenderer(
      imagePath: String,
      width: Double,
      height: Double,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector = (0, 0)
  ): SwingImageRenderer =
    new Behaviour
      with SwingImageRenderer(imagePath, width, height, offset, rotation)
      with Positionable(position.x, position.y)

  def textRenderer(
      text: String,
      size: Double,
      color: Color,
      fontFamily: FontName = "Arial",
      fontStyle: TextStyle = TextStyle.Plain,
      offset: (Double, Double) = (0, 0),
      rotation: Angle = 0.degrees,
      position: (Double, Double) = (0, 0)
  ): SwingTextRenderer =
    new Behaviour
      with SwingTextRenderer(
        text,
        size,
        color,
        fontFamily,
        fontStyle,
        offset,
        rotation
      )
      with Positionable(position._1, position._2)

  def uiTextRenderer(
      text: String,
      size: Int,
      color: Color,
      offset: (Int, Int) = (0, 0),
      anchor: UIAnchor = UIAnchor.Center
  ): SwingUITextRenderer =
    new Behaviour
      with SwingUITextRenderer(
        text,
        Font("Arial", Font.PLAIN, size),
        color,
        textAnchor = anchor,
        textOffset = offset
      )

  /* test for shape renderable */
  def testShapeProperties(renderer: SwingShapeRenderer): Unit =
    renderer.shapeWidth = 2
    renderer.shapeWidth shouldBe 2
    renderer.shapeHeight = 1.5
    renderer.shapeHeight shouldBe 1.5
    renderer.shapeColor = Color.blue
    renderer.shapeColor shouldBe Color.blue
    renderer.renderOffset = (1, 9)
    renderer.renderOffset shouldBe (1, 9)

  /* test for shape renderable */
  def testShapeInvalidValues(renderer: SwingShapeRenderer): Unit =
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
  def testSwingRenderer(renderer: SwingRenderer): Unit =
    val frame: SwingIO = io.build()
    frame.draw(renderer.renderer(frame))
    frame.show()

  /* visual test for swing renderable */
  def testSwingRendererPlacement(
      centered: SwingRenderer,
      topLeft: SwingRenderer,
      topRight: SwingRenderer
  ): Unit =
    val frame: SwingIO = io.build()
    frame.draw(centered.renderer(frame))
    frame.draw(topLeft.renderer(frame))
    frame.draw(topRight.renderer(frame))
    frame.show()
