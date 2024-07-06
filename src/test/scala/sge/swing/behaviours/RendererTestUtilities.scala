package sge.swing.behaviours

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.core.*
import behaviours.dimension2d.*
import metrics.Vector2D.*
import metrics.Angle.*
import sge.swing.*
import SwingIO.*
import output.*
import overlay.*
import Text.*
import sge.swing.behaviours.ingame.*
import sge.swing.behaviours.overlay.*
import java.awt.{Color, Font}

object RendererTestUtilities:

  private val io: SwingIOBuilder = SwingIO
    .withSize((400, 400))
    .withPixelsPerUnitRatio(100)
    .withTitle("Swing Test")

  def rectRenderer(
      width: Double,
      height: Double,
      color: Color,
      offset: Vector2D = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector2D = (0, 0)
  ): RectRenderer =
    new Behaviour
      with RectRenderer(width, height, color, offset, rotation)
      with Positionable(position.x, position.y)

  def ovalRenderer(
      width: Double,
      height: Double,
      color: Color,
      offset: Vector2D = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector2D = (0, 0)
  ): OvalRenderer =
    new Behaviour
      with OvalRenderer(width, height, color, offset, rotation)
      with Positionable(position.x, position.y)

  def squareRenderer(
      size: Double,
      color: Color,
      offset: Vector2D = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector2D = (0, 0)
  ): SquareRenderer =
    new Behaviour
      with SquareRenderer(size, color, offset, rotation)
      with Positionable(position.x, position.y)

  def circleRenderer(
      radius: Double,
      color: Color,
      offset: Vector2D = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector2D = (0, 0)
  ): CircleRenderer =
    new Behaviour
      with CircleRenderer(radius, color, offset, rotation)
      with Positionable(position.x, position.y)

  def imageRenderer(
      imagePath: String,
      width: Double,
      height: Double,
      offset: Vector2D = (0, 0),
      rotation: Angle = 0.degrees,
      position: Vector2D = (0, 0)
  ): ImageRenderer =
    new Behaviour
      with ImageRenderer(imagePath, width, height, offset, rotation)
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
  ): TextRenderer =
    new Behaviour
      with TextRenderer(
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
  ): UITextRenderer =
    new Behaviour
      with UITextRenderer(
        text,
        Font("Arial", Font.PLAIN, size),
        color,
        textAnchor = anchor,
        textOffset = offset
      )

  /* test for shape renderable */
  def testShapeProperties(renderer: ShapeRenderer): Unit =
    renderer.shapeWidth = 2
    renderer.shapeWidth shouldBe 2
    renderer.shapeHeight = 1.5
    renderer.shapeHeight shouldBe 1.5
    renderer.shapeColor = Color.blue
    renderer.shapeColor shouldBe Color.blue
    renderer.renderOffset = (1, 9)
    renderer.renderOffset shouldBe (1, 9)

  /* test for shape renderable */
  def testShapeInvalidValues(renderer: ShapeRenderer): Unit =
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
  def testRenderer(renderer: Renderer): Unit =
    val frame: SwingIO = io.build()
    frame.draw(renderer.renderer(frame))
    frame.show()

  /* visual test for swing renderable */
  def testRendererPlacement(
      centered: Renderer,
      topLeft: Renderer,
      topRight: Renderer
  ): Unit =
    val frame: SwingIO = io.build()
    frame.draw(centered.renderer(frame))
    frame.draw(topLeft.renderer(frame))
    frame.draw(topRight.renderer(frame))
    frame.show()
