import Dimensions2D.{Positionable, ScalableElement, SingleScalable}
import SwingRenderers.Text.{FontName, TextStyle}

import java.awt.image.BufferedImage
import java.awt.{Color, Font, FontMetrics, Graphics2D, Image}
import java.io.File
import javax.imageio.ImageIO
import scala.util.Try
import Dimensions2D.Vector.*

import java.awt.geom.AffineTransform

object SwingRenderers:

  object Angle:
    /** Basic type for manipulating angles in a simpler way.
      */
    type Angle = Double
    extension [N <: Int | Double](n: N)
      /** Convert the angle from radians to Angle
        * @return
        *   the corresponding angle
        */
      def radians: Angle = n match
        case i: Int    => i.toDouble
        case d: Double => d

      /** Convert the angle from degrees to Angle
        * @return
        *   the corresponding angle
        */
      def degrees: Angle = n.radians.toRadians

  object GameElements:
    /** Basic trait for manipulating and drawing game entities using Swing. The
      * main properties of the element (width, height) are mutable, and are
      * represented in game units.
      */
    trait SwingGameElement:
      /** The width of the element in game units.
        *
        * @return
        *   the width
        */
      def elementWidth: Double

      /** Set the width of the element.
        *
        * @param w
        *   the new width. It can't be negative or 0.
        */
      def elementWidth_=(w: Double): Unit

      /** The height of the element in game units.
        *
        * @return
        *   the height
        */
      def elementHeight: Double

      /** Set the height of the element.
        *
        * @param h
        *   the new height. It can't be negative or 0.
        */
      def elementHeight_=(h: Double): Unit

      /** The function defining the operation to apply on the graphic context of
        * the Swing GUI. It accepts in input: the graphic context, and a tuple
        * of composed of the starting x screen position, the starting y screen
        * position, the pixel width of the element and the pixel height of the
        * element.
        */
      def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit

    /** Base implementation of SwingGameElement.
      * @param width
      *   the width. It can't be negative or 0.
      * @param height
      *   the height. It can't be negative or 0.
      */
    abstract class BaseSwingGameElement(
        private var width: Double,
        private var height: Double
    ) extends SwingGameElement:
      elementWidth = width
      elementHeight = height
      override def elementWidth: Double = width
      override def elementWidth_=(w: Double): Unit =
        require(w > 0, "width must be positive")
        width = w

      override def elementHeight: Double = height
      override def elementHeight_=(h: Double): Unit =
        require(h > 0, "height must be positive")
        height = h

  /* Utility object for shapes */
  object Shapes:
    import GameElements.*

    /** Basic trait for manipulating and drawing geometric shapes using Swing.
      * The main properties of the shape (width, height, and color) are mutable.
      */
    trait SwingShape extends SwingGameElement:
      /** The color of the shape.
        * @return
        *   the color
        */
      def shapeColor: Color

      /** Set the color of the shape.
        * @param c
        *   the new color. It can't be null.
        */
      def shapeColor_=(c: Color): Unit

    /** Base implementation of SwingShape.
      * @param width
      *   the width. It can't be negative or 0.
      * @param height
      *   the height. It can't be negative or 0.
      * @param color
      *   the color. It can't be null.
      */
    private abstract class BaseSwingShape(
        width: Double,
        height: Double,
        private var color: Color
    ) extends BaseSwingGameElement(width, height)
        with SwingShape:
      shapeColor = color

      override def shapeColor: Color = color
      override def shapeColor_=(c: Color): Unit =
        require(c != null, "color can't be null")
        color = c

    /** A rectangular shape.
      */
    trait SwingRect extends SwingShape:
      override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
        g2d =>
          (posX, posY, w, h) =>
            g2d.setColor(shapeColor)
            g2d.fillRect(posX, posY, w, h)

    /** A square shape.
      */
    trait SwingSquare extends SwingRect:
      override def elementHeight: Double = elementWidth
      override def elementHeight_=(h: Double): Unit = elementWidth = h

    /** An oval shape.
      */
    trait SwingOval extends SwingShape:
      override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
        g2d =>
          (posX, posY, w, h) =>
            g2d.setColor(shapeColor)
            g2d.fillOval(posX, posY, w, h)

    /** A circular shape.
      */
    trait SwingCircle extends SwingOval:
      /** The radius of the circle in game units.
        * @return
        *   the radius
        */
      def shapeRadius: Double = elementWidth / 2

      /** Set the radius of the circle.
        * @param r
        *   the new radius. It can't be negative or 0.
        */
      def shapeRadius_=(r: Double): Unit =
        require(r > 0, "radius must be positive")
        elementWidth = r * 2
      override def elementHeight: Double = elementWidth
      override def elementHeight_=(h: Double): Unit = elementWidth = h

    /** Create a rectangular SwingShape.
      * @param width
      *   the width in game units
      * @param height
      *   the height in game units
      * @param color
      *   the color of the shape
      * @return
      *   a new SwingRect
      */
    def rect(width: Double, height: Double, color: Color): SwingRect =
      new BaseSwingShape(width, height, color) with SwingRect

    /** Create a square SwingShape.
      * @param size
      *   the size in game units
      * @param color
      *   the color of the shape
      * @return
      *   a new SwingSquare
      */
    def square(size: Double, color: Color): SwingSquare =
      new BaseSwingShape(size, size, color) with SwingSquare

    /** Create an oval SwingShape.
      * @param width
      *   the width in game units
      * @param height
      *   the height in game units
      * @param color
      *   the color of the shape
      * @return
      *   a new SwingOval
      */
    def oval(width: Double, height: Double, color: Color): SwingOval =
      new BaseSwingShape(width, height, color) with SwingOval

    /** Create a circular SwingShape.
      * @param radius
      *   the radius in game units
      * @param color
      *   the color of the shape
      * @return
      *   a new SwingCircle
      */
    def circle(radius: Double, color: Color): SwingCircle =
      new BaseSwingShape(radius * 2, radius * 2, color) with SwingCircle

  /* Utility object for images */
  object Images:
    import GameElements.*

    /** Basic trait for manipulating and drawing images using Swing. The main
      * properties of the element (width, height) are mutable, and are
      * represented in game units.
      */
    trait SwingImage extends SwingGameElement:
      /** The image to draw
        */
      val image: Image
      override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
        g2d =>
          (posX, posY, w, h) =>
            val img = image.getScaledInstance(w, h, Image.SCALE_DEFAULT)
            g2d.drawImage(img, posX, posY, null)

    private class SingleSwingImage(
        override val image: Image,
        width: Double,
        height: Double
    ) extends BaseSwingGameElement(width, height)
        with SwingImage

    /** Create a SwingImage from an image path
      * @param imgPath
      *   the path of the image file. Must be in a resource folder
      * @param width
      *   the wanted width of the image in game units
      * @param height
      *   the wanted height of the image in game units
      * @return
      *   a new SwingImage
      */
    def simpleImage(
        imgPath: String,
        width: Double,
        height: Double
    ): SwingImage =
      SingleSwingImage(
        ImageIO.read(getClass.getResourceAsStream(imgPath)),
        width,
        height
      )
  /* Utility object for images */
  object Text:
    import GameElements.*

    /** The style of the font. Can be Plain, Bold or Italic.
      * @param style
      *   the Swing keycode for the style
      */
    enum TextStyle(val style: Int):
      case Plain extends TextStyle(Font.PLAIN)
      case Bold extends TextStyle(Font.BOLD)
      case Italic extends TextStyle(Font.ITALIC)

    /** The font family name
      */
    type FontName = String

    /** Basic trait for manipulating and drawing texts using Swing. The main
      * properties of the element (content, size, style, font) are mutable.
      * Dimensions are represented in game units.
      */
    trait SwingText extends SwingGameElement:
      /** The content of the text.
        * @return
        *   the text
        */
      def textContent: String

      /** Set the content of the text.
        * @param text
        *   the new text
        */
      def textContent_=(text: String): Unit

      /** The style of the text.
        * @return
        *   the style
        */
      def textStyle: TextStyle

      /** Set the style of the text.
        * @param style
        *   the new style
        */
      def textStyle_=(style: TextStyle): Unit

      /** The font family of the text.
        * @return
        *   the font family
        */
      def textFont: FontName

      /** Set the font family of the text.
        * @param font
        *   the new font family
        */
      def textFont_=(font: FontName): Unit

      /** The color of the text.
        * @return
        *   the color
        */
      def textColor: Color

      /** Set the color of the text.
        * @param color
        *   the new color
        */
      def textColor_=(color: Color): Unit

      override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
        g2d =>
          (posX, posY, w, h) =>
            g2d.setFont(Font(textFont, textStyle.style, h))
            g2d.setPaint(textColor)
            g2d.drawString(textContent, posX, posY + h)

    /** Simple implementation of SwingText, that represents a one-line text. Its
      * width cannot be modified, and is automatically computed given the font
      * size and the text.
      * @param text
      *   the content
      * @param size
      *   the size in game-units
      * @param color
      *   the color
      * @param font
      *   the font family
      * @param style
      *   the font style
      */
    private class OneLineSwingText(
        private var text: String,
        private var size: Double,
        private var color: Color,
        private var font: FontName,
        private var style: TextStyle
    ) extends BaseSwingGameElement(size, size)
        with SwingText:
      import java.awt.font.FontRenderContext
      textContent = text
      textColor = color
      override def textContent: String = text
      override def textContent_=(text: String): Unit =
        require(text != null, "text content can't be null")
        this.text = text
      override def textStyle: TextStyle = style
      override def textStyle_=(style: TextStyle): Unit = this.style = style
      override def textFont: FontName = font
      override def textFont_=(font: FontName): Unit = this.font = font
      override def textColor: Color = color
      override def textColor_=(color: Color): Unit =
        require(color != null, "text color can't be null")
        this.color = color
      override def elementWidth: Double =
        val dummyFont = Font(textFont, textStyle.style, 64)
        val fontRenderContext =
          FontRenderContext(dummyFont.getTransform, true, true)
        val ratio: Double = dummyFont
          .getStringBounds(textContent, fontRenderContext)
          .getWidth / 64
        elementHeight * ratio

    /** Create a one-line text game element.
      * @param text
      *   the content
      * @param size
      *   the size in game-units
      * @param color
      *   the color
      * @param font
      *   the font family
      * @param style
      *   the font style
      * @return
      *   the SwingText
      */
    def oneLineText(
        text: String,
        size: Double,
        color: Color,
        font: FontName = "Arial",
        style: TextStyle = TextStyle.Plain
    ): SwingText =
      OneLineSwingText(text, size, color, font, style)

  import Angle.*
  import GameElements.*
  import Shapes.*
  import Images.*
  import Text.*

  /** Behaviour for rendering an object on a SwingIO.
    */
  trait SwingRenderer extends Behaviour:
    /** The function defining the operation to apply on the graphic context of
      * the Swing GUI. It accepts in input a Swing IO, and the graphic context
      * of the window.
      */
    def renderer: SwingIO => Graphics2D => Unit

    /** The rendering priority of this renderer. Higher priority means that the
      * renderer is drawn above the others. It can be modified.
      */
    var renderingPriority: Int = 0

    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        val io: SwingIO = engine.io.asInstanceOf[SwingIO]
        io.draw(renderer(io), renderingPriority)

  /** Behaviour for rendering a generic swing game element on a SwingIO
    */
  trait SwingGameElementRenderer
      extends SwingRenderer
      with Positionable
      with ScalableElement:
    /** The offset of the element. It is used to translate the element starting
      * from the position of the Positionable. Translation is applied before
      * rotation.
      */
    var renderOffset: Vector = (0, 0)

    /** The angle of rotation of the element. It is used to rotate the element
      * around its position. Rotation is applied after translation.
      */
    var renderRotation: Angle = 0

    /** The element to draw
      */
    protected def element: SwingGameElement
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
  trait SwingShapeRenderer extends SwingGameElementRenderer:
    protected val element: SwingShape

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
    * rectangle is centered at the position of the behaviour, then moved by
    * offset units and then rotated by rotation angle.
    */
  trait SwingRectRenderer(
      width: Double,
      height: Double,
      color: Color,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      priority: Int = 0
  ) extends SwingShapeRenderer:
    override protected val element: SwingRect =
      Shapes.rect(width, height, color)
    this.renderOffset = offset
    this.renderRotation = rotation
    this.renderingPriority = priority

  /** Behaviour for rendering a square on a SwingIO. Size must be > 0. The
    * square is centered at the position of the behaviour, then moved by offset
    * units and then rotated by rotation angle.
    */
  trait SwingSquareRenderer(
      size: Double,
      color: Color,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      priority: Int = 0
  ) extends SwingShapeRenderer:
    override protected val element: SwingSquare = Shapes.square(size, color)
    this.renderOffset = offset
    this.renderRotation = rotation
    this.renderingPriority = priority

  /** Behaviour for rendering an oval on a SwingIO. Sizes must be > 0. The oval
    * is centered at the position of the behaviour, then moved by offset units
    * and then rotated by rotation angle.
    */
  trait SwingOvalRenderer(
      width: Double,
      height: Double,
      color: Color,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      priority: Int = 0
  ) extends SwingShapeRenderer:
    override protected val element: SwingOval =
      Shapes.oval(width, height, color)
    this.renderOffset = offset
    this.renderRotation = rotation
    this.renderingPriority = priority

  /** Behaviour for rendering a circle on a SwingIO. Radius must be > 0. The
    * circle is centered at the position of the behaviour, then moved by offset
    * units and then rotated by rotation angle.
    */
  trait SwingCircleRenderer(
      radius: Double,
      color: Color,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      priority: Int = 0
  ) extends SwingShapeRenderer:
    override protected val element: SwingCircle = Shapes.circle(radius, color)
    export element.{shapeRadius, shapeRadius_=}
    this.renderOffset = offset
    this.renderRotation = rotation
    this.renderingPriority = priority

  /** Behaviour for rendering an image on a SwingIO. Sizes must be > 0, and the
    * image must be located in a resource folder. The image is centered at the
    * position of the behaviour, then moved by offset units and then rotated by
    * rotation angle.
    */
  trait SwingImageRenderer(
      imagePath: String,
      width: Double,
      height: Double,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      priority: Int = 0
  ) extends SwingGameElementRenderer:
    protected val element: SwingImage =
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
  trait SwingTextRenderer(
      text: String,
      size: Double,
      color: Color,
      fontFamily: FontName = "Arial",
      fontStyle: TextStyle = TextStyle.Plain,
      offset: Vector = (0, 0),
      rotation: Angle = 0.degrees,
      priority: Int = 0
  ) extends SwingGameElementRenderer:
    protected val element: SwingText =
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

  /** The anchor position used to compute the true screen position of a UI
    * element. It represents the starting point on the screen for calculating
    * the relative UI element position.
    */
  enum UIAnchor:
    case TopLeft
    case TopCenter
    case TopRight
    case CenterLeft
    case Center
    case CenterRight
    case BottomLeft
    case BottomCenter
    case BottomRight

  /** Behaviour for rendering an text on a SwingIO. The text is positioned on
    * the screen based on its anchor point and its offset. By default the anchor
    * is in the top-left corner.
    */
  trait SwingUITextRenderer(
      private var text: String,
      private var font: Font,
      private var color: Color,
      var textAnchor: UIAnchor = UIAnchor.TopLeft,
      var textOffset: (Int, Int) = (0, 0)
  ) extends SwingRenderer:
    require(font != null, "text font can't be null")
    textContent = text
    textColor = color

    import UIAnchor.*

    def textContent: String = text
    def textContent_=(txt: String): Unit =
      require(txt != null, "text content can't be null")
      text = txt
    def textSize: Int = font.getSize
    def textSize_=(s: Int): Unit =
      require(s > 0, "text size must be positive")
      font = Font(font.getFontName, font.getStyle, s)
    def textColor: Color = color
    def textColor_=(c: Color): Unit =
      require(c != null, "text color can't be null")
      color = c

    /** Compute the screen position relative to the anchor point.
      *
      * @param io
      *   the SwingIO
      * @param g2d
      *   the graphic context
      * @return
      *   the screen position of this UI element
      */
    private def anchoredPosition(io: SwingIO)(g2d: Graphics2D): (Int, Int) =
      val width: Int = g2d.getFontMetrics(font).stringWidth(textContent)
      val height: Int = textSize
      val screenWidth: Int = io.size._1
      val screenHeight: Int = io.size._2
      textAnchor match
        case TopLeft    => (0, height)
        case TopCenter  => ((screenWidth - width) / 2, height)
        case TopRight   => (screenWidth - width, height)
        case CenterLeft => (0, (screenHeight + height) / 2)
        case Center => ((screenWidth - width) / 2, (screenHeight + height) / 2)
        case CenterRight  => (screenWidth - width, (screenHeight + height) / 2)
        case BottomLeft   => (0, screenHeight)
        case BottomCenter => ((screenWidth - width) / 2, screenHeight)
        case BottomRight  => (screenWidth - width, screenHeight)

    override def renderer: SwingIO => Graphics2D => Unit = io =>
      g2d =>
        g2d.setFont(font)
        g2d.setPaint(color)
        val position = anchoredPosition(io)(g2d)
        g2d.drawString(
          text,
          position._1 + textOffset._1,
          position._2 + textOffset._2
        )
