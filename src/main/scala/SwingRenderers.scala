import Dimensions2D.Positionable

import java.awt.image.BufferedImage
import java.awt.{Color, Font, FontMetrics, Graphics2D, Image}
import java.io.File
import javax.imageio.ImageIO
import scala.util.Try

object SwingRenderers:

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

  import GameElements.*
  import Shapes.*
  import Images.*

  /** Behaviour for rendering an object on a SwingIO.
    */
  trait SwingRenderer extends Behaviour:
    /** The function defining the operation to apply on the graphic context of
      * the Swing GUI. It accepts in input a Swing IO, and the graphic context
      * of the window.
      */
    def renderer: SwingIO => Graphics2D => Unit

    /** The rendering priority of this renderer. Higher priority means that 
      * the renderer is drawn above the others. It can be modified.
      */
    var renderingPriority: Int = 0

    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        val io: SwingIO = engine.io.asInstanceOf[SwingIO]
        io.draw(renderer(io), renderingPriority)

  /** Behaviour for rendering a generic swing game element on a SwingIO
    */
  trait SwingGameElementRenderer extends SwingRenderer with Positionable:
    /** The offset of the element. it is used to translate the element starting
      * from the position of the Positionable.
      */
    var renderOffset: (Double, Double) = (0, 0)

    /** The element to draw
      */
    protected def element: SwingGameElement
    override def renderer: SwingIO => Graphics2D => Unit = io =>
      g2d =>
        val pos = io.pixelPosition(
          (
            x + renderOffset._1 - element.elementWidth / 2,
            y + renderOffset._2 + element.elementHeight / 2
          )
        )
        val w = (element.elementWidth * io.pixelsPerUnit).toInt
        val h = (element.elementHeight * io.pixelsPerUnit).toInt
        element.drawElement(g2d)(pos._1, pos._2, w, h)

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
    * offset units.
    */
  trait SwingRectRenderer(
      width: Double,
      height: Double,
      color: Color,
      offset: (Double, Double) = (0, 0),
      priority: Int = 0
  ) extends SwingShapeRenderer:
    override protected val element: SwingRect =
      Shapes.rect(width, height, color)
    this.renderOffset = offset
    this.renderingPriority = priority

  /** Behaviour for rendering a square on a SwingIO. Size must be > 0. The
    * square is centered at the position of the behaviour, then moved by offset
    * units.
    */
  trait SwingSquareRenderer(
      size: Double,
      color: Color,
      offset: (Double, Double) = (0, 0),
      priority: Int = 0
  ) extends SwingShapeRenderer:
    override protected val element: SwingSquare = Shapes.square(size, color)
    this.renderOffset = offset
    this.renderingPriority = priority

  /** Behaviour for rendering an oval on a SwingIO. Sizes must be > 0. The oval
    * is centered at the position of the behaviour, then moved by offset units.
    */
  trait SwingOvalRenderer(
      width: Double,
      height: Double,
      color: Color,
      offset: (Double, Double) = (0, 0),
      priority: Int = 0
  ) extends SwingShapeRenderer:
    override protected val element: SwingOval =
      Shapes.oval(width, height, color)
    this.renderOffset = offset
    this.renderingPriority = priority

  /** Behaviour for rendering a circle on a SwingIO. Radius must be > 0. The
    * circle is centered at the position of the behaviour, then moved by offset
    * units.
    */
  trait SwingCircleRenderer(
      radius: Double,
      color: Color,
      offset: (Double, Double) = (0, 0),
      priority: Int = 0
  ) extends SwingShapeRenderer:
    override protected val element: SwingCircle = Shapes.circle(radius, color)
    export element.{shapeRadius, shapeRadius_=}
    this.renderOffset = offset
    this.renderingPriority = priority

  /** Behaviour for rendering an image on a SwingIO. Sizes must be > 0, and the
    * image must be located in a resource folder. The image is centered at the
    * position of the behaviour, then moved by offset units.
    */
  trait SwingImageRenderer(
      imagePath: String,
      width: Double,
      height: Double,
      offset: (Double, Double) = (0, 0),
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
    this.renderingPriority = priority

  /** The anchor position used to compute the true screen position of a
    * UI element. It represents the starting point on the screen for calculating
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

  /** Behaviour for rendering an text on a SwingIO. The text is positioned on the screen
    * based on its anchor point and its offset. By default the anchor is in the top-left
    * corner.
    */
  trait SwingTextRenderer(
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
      * @param io the SwingIO
      * @param g2d the graphic context
      * @return the screen position of this UI element
      */
    private def anchoredPosition(io: SwingIO)(g2d: Graphics2D): (Int, Int) =
      val width: Int = g2d.getFontMetrics(font).stringWidth(textContent)
      val height: Int = textSize
      val screenWidth: Int = io.size._1
      val screenHeight: Int = io.size._2
      textAnchor match
        case TopLeft => (0, height)
        case TopCenter => ((screenWidth - width)/2, height)
        case TopRight => (screenWidth - width, height)
        case CenterLeft => (0, (screenHeight + height)/2)
        case Center => ((screenWidth - width)/2, (screenHeight + height)/2)
        case CenterRight => (screenWidth - width, (screenHeight + height)/2)
        case BottomLeft => (0, screenHeight)
        case BottomCenter => ((screenWidth - width)/2, screenHeight)
        case BottomRight => (screenWidth - width, screenHeight)

    override def renderer: SwingIO => Graphics2D => Unit = io =>
      g2d =>
        g2d.setFont(font)
        g2d.setPaint(color)
        val position = anchoredPosition(io)(g2d)
        g2d.drawString(text, position._1 + textOffset._1, position._2 + textOffset._2)