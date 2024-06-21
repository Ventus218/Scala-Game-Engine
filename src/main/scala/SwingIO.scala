import java.awt
import java.awt.{Canvas, Color, Dimension, Graphics, Graphics2D}
import java.util.function.Consumer
import javax.swing
import javax.swing.{JFrame, JPanel, SwingUtilities, WindowConstants}

/**
 * An implementation of IO trait using Java Swing
 */
trait SwingIO extends IO:
  /**
   * The title of the window frame
   */
  def title: String
  /**
   * The size in pixels of the window frame
   */
  def size: (Int, Int)
  /**
   * The ratio of pixels over game unit. Game unit is the unit of the coordinate system of the game scene.
   * Can be modified at runtime
   */
  def pixelsPerUnit: Int

  /**
   * Set the pixel/unit ratio to a new value
   * @param p the new ratio. It can't be negative or 0
   */
  def pixelsPerUnit_=(p: Int): Unit
  /**
   * The position of the center of the window translated to game coordinates.
   * Can be modified at runtime
   */
  def center: (Double, Double)

  /**
   * Set the center to a new position
   * @param pos the new center position
   */
  def center_=(pos: (Double, Double)): Unit
  /**
   * The background color of the window frame
   */
  def backgroundColor: Color

  /**
   * Register an operation over the graphic context of the window frame. The operation will be executed when show() is called
   *
   * @param renderer The operation to apply to the given graphic context
   */
  def draw(renderer: Graphics2D => Unit): Unit

  /**
   * Update the windows, executing all the registered operations over the graphics context
   */
  def show(): Unit

  /**
   * Converts game-coordinates positions to screen-coordinates positions
   *
   * @param scenePosition The game-coordinate position to convert
   * @return The screen-coordinates position
   */
  def pixelPosition(scenePosition: (Double, Double)): (Int, Int) =
    (size._1/2 + (pixelsPerUnit*(scenePosition._1 - center._1)).toInt, size._2/2 - (pixelsPerUnit*(scenePosition._2 - center._2)).toInt)

  /**
   * Converts screen-coordinates positions to game-coordinates positions
   *
   * @param pixelPosition The screen-coordinate position to convert
   * @return The game-coordinates position
   */
  def scenePosition(pixelPosition: (Int, Int)): (Double, Double) =
    (center._1 + (pixelPosition._1 - size._1/2)/pixelsPerUnit, center._2 - (pixelPosition._2 - size._2/2)/pixelsPerUnit)


/**
 * Utility object for SwingIO
 */
object SwingIO:
  /**
   * Create a new SwingIO class.
   * @param title the title of the window frame
   * @param size the pixel size of the window frame
   * @param pixelsPerUnit the ratio of pixels over game unit
   * @param center the position of the center of the window translated to game coordinates
   * @param background the background color of the window
   * @return a new SwingIO
   */
  def apply(title: String, size: (Int, Int), pixelsPerUnit: Int = 100, center: (Double, Double) = (0, 0), background: Color = Color.white): SwingIO =
    new SwingIOImpl(title, size, pixelsPerUnit, center, background)

  /**
   * private implementation of the SwingIO trait. It uses a DrawableCanvas to paint the window.
   */
  private class SwingIOImpl(val title: String, val size: (Int, Int), private var _pixelsPerUnit: Int, var center: (Double, Double), val backgroundColor: Color) extends SwingIO:
    require(size._1 > 0 && size._2 > 0, "size must be positive")
    require(pixelsPerUnit > 0, "pixels/unit ratio must be positive")

    private val canvas: DrawableCanvas = createCanvas()

    initCanvas()

    override def pixelsPerUnit: Int = _pixelsPerUnit
    override def pixelsPerUnit_=(p: Int): Unit =
      require(p > 0, "pixels/unit ratio must be positive")
      _pixelsPerUnit = p

    override def onFrameEnd: Engine => Unit =
      engine =>
        super.onFrameEnd(engine)
        show()

    private def initCanvas(): Unit =
      SwingUtilities.invokeAndWait(() => {
        val frame: JFrame = createFrame()
        frame.add(canvas)
        frame.pack()
        frame.setVisible(true)
      })

    private def createCanvas(): DrawableCanvas =
      new DrawableCanvas(size, backgroundColor)

    private def createFrame(): JFrame =
      val frame: JFrame = new JFrame(title)
      frame.setSize(size._1, size._2)
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      frame.setResizable(false)
      frame.setLocationRelativeTo(null)
      frame

    override def draw(renderer: Graphics2D => Unit): Unit =
      canvas.add(renderer)

    override def show(): Unit =
      canvas.showRenderers()


  /**
   * Class used as canvas for SwingIOImpl
   * @param size
   * @param color
   */
  private class DrawableCanvas(size: (Int, Int), color: Color) extends JPanel:
    private var renderers: Seq[Graphics2D => Unit] = Seq.empty
    private var show: Boolean = false
    
    setPreferredSize(new Dimension(size._1, size._2))
    setBackground(color)

    def add(renderer: Graphics2D => Unit): Unit = renderers = renderers :+ renderer
    def showRenderers(): Unit = SwingUtilities.invokeLater(() => {show = true; repaint()})
    override def paintComponent(g: Graphics): Unit =
      super.paintComponent(g)
      if !show then return
      renderers.foreach(_(g.asInstanceOf[Graphics2D]))
      renderers = Seq.empty
      show = false



  /* builder class for SwingIO, with defaults */

  /**
   * Builder for a SwingIO class
   * @param title The window title
   * @param size The screen size
   * @param pixelsPerUnit The pixels/unit ratio
   * @param center The game-wise center position
   * @param background The background color
   */
  case class SwingIOBuilder(
                             title: String = "Title",
                             size: (Int, Int) = (0, 0),
                             pixelsPerUnit: Int = 100,
                             center: (Double, Double) = (0, 0),
                             background: Color = Color.white
                           )

  /**
   * Build a SwingIO with a new title
   *
   * @param title The new title
   * @return a new builder
   */
  def withTitle(title: String): SwingIOBuilder =
    SwingIOBuilder(title = title)

  /**
   * Build a SwingIO with a new size
   *
   * @param size The new size
   * @return a new builder
   */
  def withSize(size: (Int, Int)): SwingIOBuilder =
    SwingIOBuilder(size = size)

  /**
   * Build a SwingIO with a new pixels/unit ratio
   *
   * @param pixelsPerUnit The new pixels/unit ratio
   * @return a new builder
   */
  def withPixelsPerUnitRatio(pixelsPerUnit: Int): SwingIOBuilder =
    SwingIOBuilder(pixelsPerUnit = pixelsPerUnit)

  /**
   * Build a SwingIO with a new center position
   *
   * @param center The new position
   * @return a new builder
   */
  def withCenter(center: (Double, Double)): SwingIOBuilder =
    SwingIOBuilder(center = center)

  /**
   * Build a SwingIO with a new background color
   *
   * @param color The new background color
   * @return a new builder
   */
  def withBackgroundColor(color: Color): SwingIOBuilder =
    SwingIOBuilder(background = color)

  extension (builder: SwingIOBuilder)
    /**
     * Create a new SwingIO class from this builder configuration
     *
     * @return a SwingIO implementation
     */
    def build(): SwingIO = SwingIO(builder.title, builder.size, builder.pixelsPerUnit, builder.center, builder.background)

    /**
     * Build a SwingIO with a new title
     *
     * @param title The new title
     * @return a new builder
     */
    def withTitle(title: String): SwingIOBuilder =
      SwingIOBuilder(title, builder.size, builder.pixelsPerUnit, builder.center, builder.background)

    /**
     * Build a SwingIO with a new size
     *
     * @param size The new size
     * @return a new builder
     */
    def withSize(size: (Int, Int)): SwingIOBuilder =
      SwingIOBuilder(builder.title, size, builder.pixelsPerUnit, builder.center, builder.background)

    /**
     * Build a SwingIO with a new pixels/unit ratio
     *
     * @param pixelsPerUnit The new pixels/unit ratio
     * @return a new builder
     */
    def withPixelsPerUnitRatio(pixelsPerUnit: Int): SwingIOBuilder =
      SwingIOBuilder(builder.title, builder.size, pixelsPerUnit, builder.center, builder.background)

    /**
     * Build a SwingIO with a new center position
     *
     * @param center The new position
     * @return a new builder
     */
    def withCenter(center: (Double, Double)): SwingIOBuilder =
      SwingIOBuilder(builder.title, builder.size, builder.pixelsPerUnit, center, builder.background)

    /**
     * Build a SwingIO with a new background color
     *
     * @param color The new background color
     * @return a new builder
     */
    def withBackgroundColor(color: Color): SwingIOBuilder =
      SwingIOBuilder(builder.title, builder.size, builder.pixelsPerUnit, builder.center, color)