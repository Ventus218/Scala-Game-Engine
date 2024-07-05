package sge.swing.behaviours

import sge.core.*
import sge.swing.*
import java.awt.Graphics2D

/** Behaviour for rendering an object on a SwingIO. */
trait SwingRenderer extends Behaviour:
  /** The function defining the operation to apply on the graphic context of the
    * Swing GUI. It accepts in input a Swing IO, and the graphic context of the
    * window.
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
