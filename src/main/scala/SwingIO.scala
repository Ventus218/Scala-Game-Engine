import java.awt
import java.awt.{Canvas, Dimension}
import javax.swing
import javax.swing.{JFrame, WindowConstants}

object SwingIO:
  def apply(title: String, size: (Int, Int), pixelsPerUnit: Int = 100, center: (Int, Int) = (0, 0)): IO =
    new SwingIOImpl(title, size, pixelsPerUnit, center)

  private class SwingIOImpl(title: String, size: (Int, Int), pixelsPerUnit: Int, center: (Int, Int)) extends IO:

    val canvas: Canvas = initCanvas(title, size)
    override def onFrameEnd: Engine => Unit =
      engine =>
        super.onFrameEnd(engine)

    private def initCanvas(title: String, size: (Int, Int)): Canvas =
      val canvas: Canvas = createCanvas(size)
      val frame: JFrame = createFrame(title, size)
      frame.add(canvas)
      frame.pack()
      canvas
      
    private def createCanvas(size: (Int, Int)): Canvas =
      val canvas: Canvas = new Canvas()
      val dimension: Dimension = new Dimension(size._1, size._2)
      canvas.setPreferredSize(dimension)
      canvas.setMaximumSize(dimension)
      canvas.setMinimumSize(dimension)
      canvas
    private def createFrame(title: String, size: (Int, Int)): JFrame =
      val frame: JFrame = new JFrame(title)
      frame.setSize(size._1, size._2)
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      frame.setResizable(false)
      frame.setVisible(true)
      frame.setLocationRelativeTo(null)
      frame