package sge.swing.behaviours.ingame

import sge.core.*
import behaviours.dimension2d.*
import sge.swing.*
import sge.swing.behaviours.Renderer
import sge.swing.behaviours.RendererTestUtilities.*
import java.awt.{Color, Graphics2D}

object ScalableRendererTests:
  private def oval(scale: Vector2D, color: Color): OvalRenderer =
    new Behaviour
      with OvalRenderer(1, 0.6, color)
      with Positionable
      with Scalable(scale.x, scale.y)

  private def square(scale: Double, color: Color): SquareRenderer =
    new Behaviour
      with SquareRenderer(1, color)
      with Positionable
      with SingleScalable(scale)
  private def multiRenderer(renderers: Seq[Renderer]): Renderer =
    new Behaviour with Renderer:
      override def renderer: SwingIO => Graphics2D => Unit =
        io => g2d => renderers.foreach(_.renderer(io)(g2d))
  @main def testScalableSwingRenderer(): Unit =
    testRenderer:
      multiRenderer(
        Seq(
          oval((3, 3), Color.red),
          oval((2.5, 2), Color.blue),
          oval((1, 2.5), Color.green)
        )
      )

  @main def testSingleScalableSwingRenderer(): Unit =
    testRenderer:
      multiRenderer(
        Seq(
          square(3, Color.red),
          square(2.5, Color.blue),
          square(1, Color.green)
        )
      )
