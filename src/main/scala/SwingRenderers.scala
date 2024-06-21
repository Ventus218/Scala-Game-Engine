import Behaviours.Positionable

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D, Image}
import java.io.File
import javax.imageio.ImageIO
import scala.util.Try

object SwingRenderers:

  object GameElements:
    /**
     * Basic trait for manipulating and drawing game entities using Swing.
     * The main properties of the element (width, height) are mutable, and are represented in game units.
     */
    trait SwingGameElement:
      /**
       * The width of the element in game units.
       *
       * @return the width
       */
      def elementWidth: Double
  
      /**
       * Set the width of the element.
       *
       * @param w the new width. It can't be negative or 0.
       */
      def elementWidth_=(w: Double): Unit
  
      /**
       * The height of the element in game units.
       *
       * @return the height
       */
      def elementHeight: Double
  
      /**
       * Set the height of the element.
       *
       * @param h the new height. It can't be negative or 0.
       */
      def elementHeight_=(h: Double): Unit
  
      /**
       * The function defining the operation to apply on the graphic context of the Swing GUI.
       * It accepts in input: the graphic context, and a tuple of composed of
       * the starting x screen position, the starting y screen position,
       * the pixel width of the element and the pixel height of the element.
       */
      def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit

    /**
     * Base implementation of SwingGameElement.
     * @param width the width. It can't be negative or 0.
     * @param height the height. It can't be negative or 0.
     */
    abstract class BaseSwingGameElement(private var width: Double, private var height: Double) extends SwingGameElement:
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
    /**
     * Basic trait for manipulating and drawing geometric shapes using Swing.
     * The main properties of the shape (width, height, and color) are mutable.
     */
    trait SwingShape extends SwingGameElement:
      /**
       * The color of the shape.
       * @return the color
       */
      def shapeColor: Color

      /**
       * Set the color of the shape.
       * @param c the new color. It can't be null.
       */
      def shapeColor_=(c: Color): Unit

    /**
     * Base implementation of SwingShape.
     * @param width the width. It can't be negative or 0.
     * @param height the height. It can't be negative or 0.
     * @param color the color. It can't be null.
     */
    private abstract class BaseSwingShape(width: Double, height: Double, private var color: Color) extends BaseSwingGameElement(width, height) with SwingShape:
      shapeColor = color

      override def shapeColor: Color = color
      override def shapeColor_=(c: Color): Unit =
        require(c != null, "color can't be null")
        color = c


    /**
     * A rectangular shape.
     */
    trait SwingRect extends SwingShape:
      override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit = g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillRect(posX, posY, w, h)

    /**
     * A square shape.
     */
    trait SwingSquare extends SwingRect:
      override def elementHeight: Double = elementWidth
      override def elementHeight_=(h: Double): Unit = elementWidth = h

    /**
     * An oval shape.
     */
    trait SwingOval extends SwingShape:
      override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit = g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillOval(posX, posY, w, h)

    /**
     * A circular shape.
     */
    trait SwingCircle extends SwingOval:
      /**
       * The radius of the circle in game units.
       * @return the radius
       */
      def shapeRadius: Double = elementWidth/2
      /**
       * Set the radius of the circle.
       * @param r the new radius. It can't be negative or 0.
       */
      def shapeRadius_=(r: Double): Unit =
        require(r > 0, "radius must be positive")
        elementWidth = r*2
      override def elementHeight: Double = elementWidth
      override def elementHeight_=(h: Double): Unit = elementWidth = h

    /**
     * Create a rectangular SwingShape.
     * @param width the width in game units
     * @param height the height in game units
     * @param color the color of the shape
     * @return a new SwingRect
     */
    def rect(width: Double, height: Double, color: Color): SwingRect =
      new BaseSwingShape(width, height, color) with SwingRect

    /**
     * Create a square SwingShape.
     * @param size the size in game units
     * @param color  the color of the shape
     * @return a new SwingSquare
     */
    def square(size: Double, color: Color): SwingSquare =
      new BaseSwingShape(size, size, color) with SwingSquare

    /**
     * Create an oval SwingShape.
     * @param width  the width in game units
     * @param height the height in game units
     * @param color  the color of the shape
     * @return a new SwingOval
     */
    def oval(width: Double, height: Double, color: Color): SwingOval =
      new BaseSwingShape(width, height, color) with SwingOval

    /**
     * Create a circular SwingShape.
     * @param radius  the radius in game units
     * @param color the color of the shape
     * @return a new SwingCircle
     */
    def circle(radius: Double, color: Color): SwingCircle =
      new BaseSwingShape(radius*2, radius*2, color) with SwingCircle

  /* Utility object for images */
  object Images:
    import GameElements.*

    /**
     * Basic trait for manipulating and drawing images using Swing.
     * The main properties of the element (width, height) are mutable, and are represented in game units.
     */
    trait SwingImage extends SwingGameElement:
      /**
       * The image to draw
       */
      val image: Image
      override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
        g2d => (posX, posY, w, h) =>
          val img = image.getScaledInstance(w, h, Image.SCALE_DEFAULT)
          g2d.drawImage(img, posX, posY, null)

    private class SingleSwingImage(override val image: Image, width: Double, height: Double) extends BaseSwingGameElement(width, height) with SwingImage

    /**
     * Create a SwingImage from an image path
     * @param imgPath the path of the image file. Must be in a resource folder
     * @param width the wanted width of the image in game units
     * @param height the wanted height of the image in game units
     * @return a new SwingImage
     */
    def simpleImage(imgPath: String, width: Double, height: Double): SwingImage =
      SingleSwingImage(ImageIO.read(getClass.getResourceAsStream(imgPath)), width, height)


  import GameElements.*
  import Shapes.*
  import Images.*


  /**
   * Behaviour for rendering an object on a SwingIO.
   */
  trait SwingRenderer extends Behaviour:
    /**
     * The function defining the operation to apply on the graphic context of the Swing GUI.
     * It accepts in input a Swing IO, and the graphic context of the window.
     */
    def renderer: SwingIO => Graphics2D => Unit

    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        Try(engine.io.asInstanceOf[SwingIO]).foreach(io => io.draw(renderer(io)))

  /**
   * Behaviour for rendering a generic swing game element on a SwingIO
   */
  trait SwingGameElementRenderer extends SwingRenderer with Positionable:
    /**
     * The offset of the element. it is used to translate the element
     * starting from the position of the Positionable.
     */
    var renderOffset: (Double, Double) = (0, 0)
    /**
     * The element to draw
     */
    protected def element: SwingGameElement
    override def renderer: SwingIO => Graphics2D => Unit = io =>
      g2d =>
        val pos = io.pixelPosition((x + renderOffset._1 - element.elementWidth / 2, y + renderOffset._2 + element.elementHeight / 2))
        val w = (element.elementWidth * io.pixelsPerUnit).toInt
        val h = (element.elementHeight * io.pixelsPerUnit).toInt
        element.drawElement(g2d)(pos._1, pos._2, w, h)

  /**
   * Behaviour for rendering geometric shapes on a SwingIO.
   */
  trait SwingShapeRenderer extends SwingGameElementRenderer:
    protected val element: SwingShape
    export element.{
      elementWidth as shapeWidth, elementWidth_= as shapeWidth_=,
      elementHeight as shapeHeight, elementHeight_= as shapeHeight_=,
      shapeColor, shapeColor_=
    }

  /**
   * Behaviour for rendering a rectangle on a SwingIO. Sizes must be > 0. The rectangle is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingRectRenderer(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderer:
    override protected val element: SwingRect = Shapes.rect(width, height, color)
    this.renderOffset = offset

  /**
   * Behaviour for rendering a square on a SwingIO. Size must be > 0. The square is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingSquareRenderer(size: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderer:
    override protected val element: SwingSquare = Shapes.square(size, color)
    this.renderOffset = offset

  /**
   * Behaviour for rendering an oval on a SwingIO. Sizes must be > 0. The oval is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingOvalRenderer(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderer:
    override protected val element: SwingOval = Shapes.oval(width, height, color)
    this.renderOffset = offset

  /**
   * Behaviour for rendering a circle on a SwingIO. Radius must be > 0. The circle is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingCircleRenderer(radius: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderer:
    override protected val element: SwingCircle = Shapes.circle(radius, color)
    export element.{shapeRadius, shapeRadius_=}
    this.renderOffset = offset

  /**
   * Behaviour for rendering an image on a SwingIO. Sizes must be > 0, and the image must be located in a resource folder.
   * The image is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingImageRenderer(imagePath: String, width: Double, height: Double, offset: (Double, Double) = (0, 0)) extends SwingGameElementRenderer:
    protected val element: SwingImage = Images.simpleImage(imagePath, width, height)
    export element.{
      elementWidth as imageWidth, elementWidth_= as imageWidth_=,
      elementHeight as imageHeight, elementHeight_= as imageHeight_=,
      image
    }
    this.renderOffset = offset