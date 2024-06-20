import Behaviours.Positionable

import java.awt.{Color, Graphics2D}
import scala.util.Try

object SwingRenderers:

  object Shapes:
    trait SwingShape:
      def shapeWidth: Double
      def shapeHeight: Double
      def shapeColor: Color
      def drawShape: Graphics2D => (Int, Int, Int, Int) => Unit

    trait SwingRect extends SwingShape:
      override def drawShape: Graphics2D => (Int, Int, Int, Int) => Unit = g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillRect(posX, posY, w, h)

    trait SwingOval extends SwingShape:
      override def drawShape: Graphics2D => (Int, Int, Int, Int) => Unit = g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillOval(posX, posY, w, h)


  import Shapes.*

  /**
   * Behaviour for rendering an object on a SwingIO.
   */
  trait SwingRenderable extends Behaviour:
    def renderOffset: (Double, Double) = (0, 0)
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
  trait SwingRectRenderable(private var width: Double, private var height: Double, private var color: Color, private var offset: (Double, Double) = (0, 0))
    extends SwingShapeRenderable with Positionable:
    require(width > 0 && height > 0, "sizes must be positive")

    def rectWidth: Double = width
    def rectHeight: Double = height
    def rectColor: Color = color
    def rectOffset: (Double, Double) = offset

    def rectWidth_=(w: Double): Unit = {require(w > 0, "width must be positive"); width = w}
    def rectHeight_=(h: Double): Unit = {require(h > 0, "height must be positive"); height = h}
    def rectColor_=(c: Color): Unit = color = c
    def rectOffset_=(o: (Double, Double)): Unit = offset = o

    override def renderer: SwingIO => Graphics2D => Unit = io =>
      g2d =>
        g2d.setColor(rectColor)
        val pos = io.pixelPosition((x + rectOffset._1 - rectWidth/2, y + rectOffset._2 + rectHeight/2))
        val w = (rectWidth*io.pixelsPerUnit).toInt
        val h = (rectHeight*io.pixelsPerUnit).toInt
        g2d.fillRect(pos._1, pos._2, w, h)

  trait SwingSquareRenderable(private var size: Double, private var color: Color, private var offset: (Double, Double) = (0, 0)) extends SwingRectRenderable:
    require(size > 0, "size must be positive")

    var squareSize: Double = size


  trait SwingOvalRenderable(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingRenderable with Positionable:
    require(width > 0 && height > 0, "sizes must be positive")

    var ovalWidth: Double = width
    var ovalHeight: Double = height
    var ovalColor: Color = color
    var ovalOffset: (Double, Double) = offset

    override def renderer: SwingIO => Graphics2D => Unit = io =>
      g2d =>
        g2d.setColor(ovalColor)
        val pos = io.pixelPosition((x + ovalOffset._1 - ovalWidth/2, y + ovalOffset._2 + ovalHeight/2))
        val w = (ovalWidth * io.pixelsPerUnit).toInt
        val h = (ovalHeight * io.pixelsPerUnit).toInt
        g2d.fillOval(pos._1, pos._2, w, h)
