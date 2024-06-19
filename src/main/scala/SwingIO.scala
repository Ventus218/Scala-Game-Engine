import java.awt
import java.awt.{Canvas, Color, Dimension, Graphics, Graphics2D}
import javax.swing
import javax.swing.{JFrame, JPanel, SwingUtilities, WindowConstants}

trait SwingIO extends IO:
  val title: String
  val size: (Int, Int)
  val pixelsPerUnit: Int
  val center: (Double, Double)
  val backgroundColor: Color
  def draw(renderer: Graphics2D => Unit): Unit
  def show(): Unit
  def pixelPosition(scenePosition: (Double, Double)): (Int, Int) =
    (size._1/2 + (pixelsPerUnit*(scenePosition._1 - center._1)).toInt, size._2/2 - (pixelsPerUnit*(scenePosition._2 - center._2)).toInt)
  def scenePosition(pixelPosition: (Int, Int)): (Double, Double) =
    (center._1 + (pixelPosition._1 - size._1/2)/pixelsPerUnit, center._2 - (pixelPosition._2 - size._2/2)/pixelsPerUnit)
    

object SwingIO:
  def apply(title: String, size: (Int, Int), pixelsPerUnit: Int = 100, center: (Double, Double) = (0, 0), background: Color = Color.white): SwingIO =
    new SwingIOImpl(title, size, pixelsPerUnit, center, background)

  private class SwingIOImpl(val title: String, val size: (Int, Int), val pixelsPerUnit: Int, val center: (Double, Double), val backgroundColor: Color) extends SwingIO:

    val canvas: DrawableCanvas = initCanvas()

    override def onFrameEnd: Engine => Unit =
      engine =>
        super.onFrameEnd(engine)

    private def initCanvas(): DrawableCanvas =
      val canvas: DrawableCanvas = createCanvas()
      val frame: JFrame = createFrame()
      frame.add(canvas)
      frame.validate()
      canvas

    private def createCanvas(): DrawableCanvas =
      new DrawableCanvas(size, backgroundColor)

    private def createFrame(): JFrame =
      val frame: JFrame = new JFrame(title)
      frame.setSize(size._1, size._2)
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      frame.setResizable(false)
      frame.setVisible(true)
      frame.setLocationRelativeTo(null)
      frame

    override def draw(renderer: Graphics2D => Unit): Unit =
      SwingUtilities.invokeLater( () => canvas.add(renderer) )

    override def show(): Unit =
      SwingUtilities.invokeLater( () => canvas.drawRenderers() )


  private class DrawableCanvas(size: (Int, Int), color: Color) extends JPanel:
    private var renderers: Seq[Graphics2D => Unit] = Seq.empty
    
    setBounds(0, 0, size._1, size._2)
    setBackground(color)
    
    def add(renderer: Graphics2D => Unit): Unit = renderers = renderers :+ renderer
    def drawRenderers(): Unit = repaint()
    protected override def paintComponent(g: Graphics): Unit =
      super.paintComponent(g)
      renderers.foreach(_(g.asInstanceOf[Graphics2D]))
      renderers = Seq.empty
      


  /* builder class for SwingIO, with defaults */
  case class SwingIOBuilder(
                             title: String = "Title",
                             size: (Int, Int) = (0, 0),
                             pixelsPerUnit: Int = 100,
                             center: (Double, Double) = (0, 0),
                             background: Color = Color.white
                           )
  def withTitle(title: String): SwingIOBuilder =
    SwingIOBuilder(title = title)
  def withSize(size: (Int, Int)): SwingIOBuilder =
    SwingIOBuilder(size = size)
  def withPixelsPerUnitRatio(pixelsPerUnit: Int): SwingIOBuilder =
    SwingIOBuilder(pixelsPerUnit = pixelsPerUnit)
  def withCenter(center: (Double, Double)): SwingIOBuilder =
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
    def withCenter(center: (Double, Double)): SwingIOBuilder =
      SwingIOBuilder(builder.title, builder.size, builder.pixelsPerUnit, center, builder.background)
    def withBackgroundColor(color: Color): SwingIOBuilder =
      SwingIOBuilder(builder.title, builder.size, builder.pixelsPerUnit, builder.center, color)