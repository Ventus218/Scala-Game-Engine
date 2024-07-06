package sge.swing.behaviours.ingame

import sge.core.*
import behaviours.dimension2d.*
import metrics.Vector2D.*
import metrics.Angle.*
import sge.swing.*
import sge.swing.behaviours.Renderer
import Utils.*
import output.*
import GameElements.*
import Shapes.*
import Text.*
import Images.*
import java.awt.{Graphics2D, Color}

/** Behaviour for rendering a generic swing game element on a SwingIO
  */
trait GameElementRenderer
    extends Renderer
    with Positionable
    with ScalableElement:
  /** The offset of the element. It is used to translate the element starting
    * from the position of the Positionable. Translation is applied before
    * rotation.
    */
  var renderOffset: Vector2D = (0, 0)

  /** The angle of rotation of the element. It is used to rotate the element
    * around its position. Rotation is applied after translation.
    */
  var renderRotation: Angle = 0

  /** The element to draw
    */
  protected def element: GameElement
  override def renderer: SwingIO => Graphics2D => Unit = io =>
    g2d =>
      val offsetPos = io.pixelPosition(
        (
          position.x + renderOffset.x - element.elementWidth * scaleWidth / 2,
          position.y + renderOffset.y + element.elementHeight * scaleHeight / 2
        )
      )
      val rotationPos = io.pixelPosition(
        (position.x, position.y)
      )
      val w = (element.elementWidth * scaleWidth * io.pixelsPerUnit).toInt
      val h = (element.elementHeight * scaleHeight * io.pixelsPerUnit).toInt
      val initialTransform = g2d.getTransform()
      g2d.rotate(renderRotation, rotationPos._1, rotationPos._2)
      element.drawElement(g2d)(offsetPos._1, offsetPos._2, w, h)
      g2d.setTransform(initialTransform)

/** Behaviour for rendering geometric shapes on a SwingIO.
  */
trait ShapeRenderer extends GameElementRenderer:
  protected val element: Shape

  /* Using the "=>" syntax instead of "as" because Metals didn't like this code
   * formatted using the default formatter
   */
  export element.{
    elementWidth => shapeWidth,
    elementWidth_= => shapeWidth_=,
    elementHeight => shapeHeight,
    elementHeight_= => shapeHeight_=,
    shapeColor,
    shapeColor_=
  }

/** Behaviour for rendering a rectangle on a SwingIO. Sizes must be > 0. The
  * rectangle is centered at the position of the behaviour, then moved by offset
  * units and then rotated by rotation angle.
  */
trait RectRenderer(
    width: Double,
    height: Double,
    color: Color,
    offset: Vector2D = (0, 0),
    rotation: Angle = 0.degrees,
    priority: Int = 0
) extends ShapeRenderer:
  override protected val element: Rect =
    Shapes.rect(width, height, color)
  this.renderOffset = offset
  this.renderRotation = rotation
  this.renderingPriority = priority

/** Behaviour for rendering a square on a SwingIO. Size must be > 0. The square
  * is centered at the position of the behaviour, then moved by offset units and
  * then rotated by rotation angle.
  */
trait SquareRenderer(
    size: Double,
    color: Color,
    offset: Vector2D = (0, 0),
    rotation: Angle = 0.degrees,
    priority: Int = 0
) extends ShapeRenderer:
  override protected val element: Square = Shapes.square(size, color)
  this.renderOffset = offset
  this.renderRotation = rotation
  this.renderingPriority = priority

/** Behaviour for rendering an oval on a SwingIO. Sizes must be > 0. The oval is
  * centered at the position of the behaviour, then moved by offset units and
  * then rotated by rotation angle.
  */
trait OvalRenderer(
    width: Double,
    height: Double,
    color: Color,
    offset: Vector2D = (0, 0),
    rotation: Angle = 0.degrees,
    priority: Int = 0
) extends ShapeRenderer:
  override protected val element: Oval =
    Shapes.oval(width, height, color)
  this.renderOffset = offset
  this.renderRotation = rotation
  this.renderingPriority = priority

/** Behaviour for rendering a circle on a SwingIO. Radius must be > 0. The
  * circle is centered at the position of the behaviour, then moved by offset
  * units and then rotated by rotation angle.
  */
trait CircleRenderer(
    radius: Double,
    color: Color,
    offset: Vector2D = (0, 0),
    rotation: Angle = 0.degrees,
    priority: Int = 0
) extends ShapeRenderer:
  override protected val element: Circle = Shapes.circle(radius, color)
  export element.{shapeRadius, shapeRadius_=}
  this.renderOffset = offset
  this.renderRotation = rotation
  this.renderingPriority = priority

/** Behaviour for rendering an image on a SwingIO. Sizes must be > 0, and the
  * image must be located in a resource folder. The image is centered at the
  * position of the behaviour, then moved by offset units and then rotated by
  * rotation angle.
  */
trait ImageRenderer(
    imagePath: String,
    width: Double,
    height: Double,
    offset: Vector2D = (0, 0),
    rotation: Angle = 0.degrees,
    priority: Int = 0
) extends GameElementRenderer:
  protected val element: Image =
    Images.simpleImage(imagePath, width, height)

  /* Using the "=>" syntax instead of "as" because Metals didn't like this code
   * formatted using the default formatter
   */
  export element.{
    elementWidth => imageWidth,
    elementWidth_= => imageWidth_=,
    elementHeight => imageHeight,
    elementHeight_= => imageHeight_=,
    image
  }
  this.renderOffset = offset
  this.renderRotation = rotation
  this.renderingPriority = priority

/** Behaviour for rendering a text on a SwingIO. Size must be > 0. The text is
  * centered at the position of the behaviour, then moved by offset units and
  * then rotated by rotation angle.
  */
trait TextRenderer(
    text: String,
    size: Double,
    color: Color,
    fontFamily: FontName = "Arial",
    fontStyle: TextStyle = TextStyle.Plain,
    offset: Vector2D = (0, 0),
    rotation: Angle = 0.degrees,
    priority: Int = 0
) extends GameElementRenderer:
  protected val element: Text =
    Text.oneLineText(text, size, color, fontFamily, fontStyle)

  export element.{
    elementWidth => textWidth,
    elementHeight => textSize,
    elementHeight_= => textSize_=,
    textContent,
    textContent_=,
    textFont,
    textFont_=,
    textStyle,
    textStyle_=,
    textColor,
    textColor_=
  }
  this.renderOffset = offset
  this.renderRotation = rotation
  this.renderingPriority = priority
