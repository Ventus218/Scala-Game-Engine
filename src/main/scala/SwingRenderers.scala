import Behaviours.Positionable

import java.awt.{Color, Graphics2D}
import scala.util.Try

object SwingRenderers:

  object Shapes:
    /**
     * Basic trait for manipulating and drawing geometric shapes using Swing.
     * The main properties of the shape (width, height, and color) are mutable.
     */
    trait SwingShape:
      /**
       * The width of the shape in game units.
       * @return the width
       */
      def shapeWidth: Double

      /**
       * Set the width of the shape.
       * @param w the new width. It can't be negative or 0.
       */
      def shapeWidth_=(w: Double): Unit

      /**
       * The height of the shape in game units.
       * @return the height
       */
      def shapeHeight: Double

      /**
       * Set the height of the shape.
       * @param h the new height. It can't be negative or 0.
       */
      def shapeHeight_=(h: Double): Unit

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
       * The function defining the operation to apply on the graphic context of the Swing GUI.
       * It accepts in input: the graphic context, and a tuple of composed of
       * the starting x screen position, the starting y screen position,
       * the pixel width of the shape and the pixel height of the shape.
       */
      def drawShape: Graphics2D => (Int, Int, Int, Int) => Unit

    private abstract class BaseSwingShape(private var width: Double, private var height: Double, private var color: Color) extends SwingShape:
      shapeWidth = width
      shapeHeight = height
      shapeColor = color

      override def shapeWidth: Double = width
      override def shapeWidth_=(w: Double): Unit =
        require(w > 0, "width must be positive")
        width = w

      override def shapeHeight: Double = height
      override def shapeHeight_=(h: Double): Unit =
        require(h > 0, "height must be positive")
        height = h

      override def shapeColor: Color = color
      override def shapeColor_=(c: Color): Unit =
        require(c != null, "color can't be null")
        color = c


    /**
     * A rectangular shape.
     */
    trait SwingRect extends SwingShape:
      override def drawShape: Graphics2D => (Int, Int, Int, Int) => Unit = g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillRect(posX, posY, w, h)

    /**
     * A square shape.
     */
    trait SwingSquare extends SwingRect:
      override def shapeHeight: Double = shapeWidth
      override def shapeHeight_=(h: Double): Unit = shapeWidth = h

    /**
     * An oval shape.
     */
    trait SwingOval extends SwingShape:
      override def drawShape: Graphics2D => (Int, Int, Int, Int) => Unit = g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillOval(posX, posY, w, h)

    def rect(width: Double, height: Double, color: Color): SwingRect =
      new BaseSwingShape(width, height, color) with SwingRect
    def square(size: Double, color: Color): SwingSquare =
      new BaseSwingShape(size, size, color) with SwingSquare
    def oval(width: Double, height: Double, color: Color): SwingOval =
      new BaseSwingShape(width, height, color) with SwingOval


  import Shapes.*

  /**
   * Behaviour for rendering an object on a SwingIO.
   */
  trait SwingRenderable extends Behaviour:
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
   * Behaviour for rendering geometric shapes on a SwingIO.
   */
  trait SwingShapeRenderable extends SwingRenderable with Positionable:
    /**
     * The offset of the shape. it is used to translate the shape
     * starting from the position of the Positionable.
     */
    var renderOffset: (Double, Double) = (0, 0)
    /**
     * The shape to draw.
     */
    val shape: SwingShape
    export shape.*
    override def renderer: SwingIO => Graphics2D => Unit = io =>
      g2d =>
        val pos = io.pixelPosition((x + renderOffset._1 - shapeWidth / 2, y + renderOffset._2 + shapeHeight / 2))
        val w = (shapeWidth * io.pixelsPerUnit).toInt
        val h = (shapeHeight * io.pixelsPerUnit).toInt
        drawShape(g2d)(pos._1, pos._2, w, h)


  /**
   * Behaviour for rendering a rectangle on a SwingIO. Sizes must be > 0. The rectangle is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingRectRenderable(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderable:
    override val shape: SwingRect = Shapes.rect(width, height, color)
    this.renderOffset = offset

  /**
   * Behaviour for rendering a square on a SwingIO. Size must be > 0. The square is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingSquareRenderable(size: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderable:
    override val shape: SwingSquare = Shapes.square(size, color)
    this.renderOffset = offset

  /**
   * Behaviour for rendering an oval on a SwingIO. Sizes must be > 0. The oval is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingOvalRenderable(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderable:
    override val shape: SwingOval = Shapes.oval(width, height, color)
    this.renderOffset = offset
