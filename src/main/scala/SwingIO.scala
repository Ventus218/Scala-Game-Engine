import java.awt
import java.awt.{Canvas, Color, Dimension}
import javax.swing
import javax.swing.{JFrame, WindowConstants}

trait SwingIO extends IO

object SwingIO:
  def apply(title: String, size: (Int, Int), pixelsPerUnit: Int = 100, center: (Int, Int) = (0, 0), background: Color = Color.white): SwingIO =
    new SwingIOImpl(title, size, pixelsPerUnit, center, background)
  
  private class SwingIOImpl(title: String, size: (Int, Int), pixelsPerUnit: Int, center: (Int, Int), background: Color) extends SwingIO:
    
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

  /* builder class for SwingIO, with defaults */
  case class SwingIOBuilder(
                             title: String = "Title",
                             size: (Int, Int) = (0, 0),
                             pixelsPerUnit: Int = 100,
                             center: (Int, Int) = (0, 0),
                             background: Color = Color.white
                           )
  def withTitle(title: String): SwingIOBuilder =
    SwingIOBuilder(title = title)
  def withSize(size: (Int, Int)): SwingIOBuilder =
    SwingIOBuilder(size = size)
  def withPixelsPerUnitRatio(pixelsPerUnit: Int): SwingIOBuilder =
    SwingIOBuilder(pixelsPerUnit = pixelsPerUnit)
  def withCenter(center: (Int, Int)): SwingIOBuilder =
    SwingIOBuilder(center = center)
  def withBackgroundColor(color: Color): SwingIOBuilder =
    SwingIOBuilder(background = color)
    
  extension (builder: SwingIOBuilder)
    def build(): SwingIO = SwingIO(builder.title, builder.size, builder.pixelsPerUnit, builder.center, builder.background)
    def withTitle(title: String): SwingIOBuilder =
      SwingIOBuilder(title, builder.size, builder.pixelsPerUnit, builder.center, builder.background)
    def withSize(size: (Int, Int)): SwingIOBuilder =
      SwingIOBuilder(builder.title, size, builder.pixelsPerUnit, builder.center, builder.background)
    def withPixelsPerUnitRatio(pixelsPerUnit: Int): SwingIOBuilder =
      SwingIOBuilder(builder.title, builder.size, pixelsPerUnit, builder.center, builder.background)
    def withCenter(center: (Int, Int)): SwingIOBuilder =
      SwingIOBuilder(builder.title, builder.size, builder.pixelsPerUnit, center, builder.background)
    def withBackgroundColor(color: Color): SwingIOBuilder =
      SwingIOBuilder(builder.title, builder.size, builder.pixelsPerUnit, builder.center, color)