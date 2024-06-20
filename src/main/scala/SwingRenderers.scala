import Behaviours.Positionable

import java.awt.{Color, Graphics2D}
import scala.util.Try

object SwingRenderers:

  object Shapes:
    trait SwingShape:
      def shapeWidth: Double
      def shapeWidth_=(w: Double): Unit
      def shapeHeight: Double
      def shapeHeight_=(h: Double): Unit
      def shapeColor: Color
      def shapeColor_=(c: Color): Unit
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


    trait SwingRect extends SwingShape:
      override def drawShape: Graphics2D => (Int, Int, Int, Int) => Unit = g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillRect(posX, posY, w, h)
          
    trait SwingSquare extends SwingRect:
      override def shapeHeight: Double = shapeWidth
      override def shapeHeight_=(h: Double): Unit = shapeWidth = h
      

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
    var renderOffset: (Double, Double) = (0, 0)
    def renderer: SwingIO => Graphics2D => Unit
    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        Try(engine.io.asInstanceOf[SwingIO]).foreach(io => io.draw(renderer(io)))

  trait SwingShapeRenderable extends SwingRenderable with Positionable:
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


  trait SwingSquareRenderable(size: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderable:
    override val shape: SwingSquare = Shapes.square(size, color)
    this.renderOffset = offset


  trait SwingOvalRenderable(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingShapeRenderable:
    override val shape: SwingOval = Shapes.oval(width, height, color)
    this.renderOffset = offset
