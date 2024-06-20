import Behaviours.Positionable

import java.awt.{Color, Graphics2D}
import scala.util.Try

object SwingRenderers:
  /**
   * Behaviour for rendering an object on a SwingIO.
   */
  trait SwingRenderable extends Behaviour:
    def renderer: SwingIO => Graphics2D => Unit
    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        Try(engine.io.asInstanceOf[SwingIO]).foreach(io => io.draw(renderer(io)))


  /**
   * Behaviour for rendering a rectangle on a SwingIO. Sizes must be > 0. The rectangle is centered at the position of the behaviour, then moved by offset units.
   */
  trait SwingRectRenderable(width: Double, height: Double, color: Color, offset: (Double, Double) = (0, 0)) extends SwingRenderable with Positionable:
    require(width > 0 && height > 0, "sizes must be positive")
    
    var rectWidth: Double = width
    var rectHeight: Double = height
    var rectColor: Color = color
    var rectOffset: (Double, Double) = offset

    override def renderer: SwingIO => Graphics2D => Unit = io =>
      g2d =>
        g2d.setColor(rectColor)
        val pos = io.pixelPosition((x + rectOffset._1 - rectWidth/2, y + rectOffset._2 + rectHeight/2))
        val w = (rectWidth*io.pixelsPerUnit).toInt
        val h = (rectHeight*io.pixelsPerUnit).toInt
        g2d.fillRect(pos._1, pos._2, w, h)

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