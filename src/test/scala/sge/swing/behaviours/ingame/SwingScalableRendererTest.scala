package sge.swing.behaviours.ingame

import sge.core.*
import metrics.Vector2D.*
import behaviours.dimension2d.*
import sge.swing.*
import sge.swing.behaviours.SwingRenderer
import sge.swing.behaviours.SwingRendererTestUtilities.*
import SwingIO.*
import java.awt.{Color, Graphics2D}

object SwingScalableRendererTest:
  private def oval(scale: Vector2D, color: Color): SwingOvalRenderer =
    new Behaviour
      with SwingOvalRenderer(1, 0.6, color)
      with Positionable
      with Scalable(scale.x, scale.y)

  private def square(scale: Double, color: Color): SwingSquareRenderer =
    new Behaviour
      with SwingSquareRenderer(1, color)
      with Positionable
      with SingleScalable(scale)
  private def multiRenderer(renderers: Seq[SwingRenderer]): SwingRenderer =
    new Behaviour with SwingRenderer:
      override def renderer: SwingIO => Graphics2D => Unit =
        io => g2d => renderers.foreach(_.renderer(io)(g2d))
  @main def testScalableSwingRenderer(): Unit =
    testSwingRenderer:
      multiRenderer(
        Seq(
          oval((3, 3), Color.red),
          oval((2.5, 2), Color.blue),
          oval((1, 2.5), Color.green)
        )
      )

  @main def testSingleScalableSwingRenderer(): Unit =
    testSwingRenderer:
      multiRenderer(
        Seq(
          square(3, Color.red),
          square(2.5, Color.blue),
          square(1, Color.green)
        )
      )
