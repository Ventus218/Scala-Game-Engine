import java.awt
import java.awt.{Canvas, Color, Dimension, Graphics, Graphics2D}
import java.util.function.Consumer
import javax.swing
import javax.swing.{JFrame, JPanel, SwingUtilities, WindowConstants}
import SwingIO.InputButton
import java.awt.event.MouseEvent
import java.awt.MouseInfo

/** An implementation of IO trait using Java Swing
  */
trait SwingIO extends IO:
  /** The title of the window frame
    */
  def title: String

  /** The size in pixels of the window frame
    */
  def size: (Int, Int)

  /** The ratio of pixels over game unit. Game unit is the unit of the
    * coordinate system of the game scene. Can be modified at runtime
    */
  def pixelsPerUnit: Int

  /** Set the pixel/unit ratio to a new value
    * @param p
    *   the new ratio. It can't be negative or 0
    */
  def pixelsPerUnit_=(p: Int): Unit

  /** The position of the center of the window translated to game coordinates.
    * Can be modified at runtime
    */
  def center: (Double, Double)

  /** Set the center to a new position
    * @param pos
    *   the new center position
    */
  def center_=(pos: (Double, Double)): Unit

  /** The background color of the window frame
    */
  def backgroundColor: Color

  /** Register an operation over the graphic context of the window frame. The
    * operation will be executed when show() is called
    *
    * @param renderer
    *   The operation to apply to the given graphic context
    */
  def draw(renderer: Graphics2D => Unit): Unit

  /** Update the windows, executing all the registered operations over the
    * graphics context
    */
  def show(): Unit

  /** Determines if a inputButton was pressed in the last frame.
    *
    * @param inputButton
    * @return
    */
  def inputButtonWasPressed(inputButton: InputButton): Boolean

  /** Retrieves the current pointer position in the scene coordinate space
    *
    * @return
    */
  def scenePointerPosition(): (Double, Double)

extension (io: SwingIO)
  /** Converts game-coordinates positions to screen-coordinates positions
    *
    * @param scenePosition
    *   The game-coordinate position to convert
    * @return
    *   The screen-coordinates position
    */
  def pixelPosition(scenePosition: (Double, Double)): (Int, Int) =
    (
      io.size._1 / 2 + (io.pixelsPerUnit * (scenePosition._1 - io.center._1)).toInt,
      io.size._2 / 2 - (io.pixelsPerUnit * (scenePosition._2 - io.center._2)).toInt
    )

  /** Converts screen-coordinates positions to game-coordinates positions
    *
    * @param pixelPosition
    *   The screen-coordinate position to convert
    * @return
    *   The game-coordinates position
    */
  def scenePosition(pixelPosition: (Int, Int)): (Double, Double) =
    (
      io.center._1 + (pixelPosition._1 - io.size._1 / 2) / io.pixelsPerUnit,
      io.center._2 - (pixelPosition._2 - io.size._2 / 2) / io.pixelsPerUnit
    )

/** Utility object for SwingIO
  */
object SwingIO:
  import java.awt.event.KeyEvent.*

  /** The type of input event (like Pressed or Released)
    */
  enum InputEvent:
    case Pressed
    case Released

  /** The buttons that can generate an input.
    *
    * @param id
    *   The Java AWT enum int value that identifies the button
    */
  enum InputButton(val id: Int):
    case N_0 extends InputButton(VK_0)
    case N_1 extends InputButton(VK_1)
    case N_2 extends InputButton(VK_2)
    case N_3 extends InputButton(VK_3)
    case N_4 extends InputButton(VK_4)
    case N_5 extends InputButton(VK_5)
    case N_6 extends InputButton(VK_6)
    case N_7 extends InputButton(VK_7)
    case N_8 extends InputButton(VK_8)
    case N_9 extends InputButton(VK_9)
    case Q extends InputButton(VK_Q)
    case W extends InputButton(VK_W)
    case E extends InputButton(VK_E)
    case R extends InputButton(VK_R)
    case T extends InputButton(VK_T)
    case Y extends InputButton(VK_Y)
    case U extends InputButton(VK_U)
    case I extends InputButton(VK_I)
    case O extends InputButton(VK_O)
    case P extends InputButton(VK_P)
    case A extends InputButton(VK_A)
    case S extends InputButton(VK_S)
    case D extends InputButton(VK_D)
    case F extends InputButton(VK_F)
    case G extends InputButton(VK_G)
    case H extends InputButton(VK_H)
    case J extends InputButton(VK_J)
    case K extends InputButton(VK_K)
    case L extends InputButton(VK_L)
    case Z extends InputButton(VK_Z)
    case X extends InputButton(VK_X)
    case C extends InputButton(VK_C)
    case V extends InputButton(VK_V)
    case B extends InputButton(VK_B)
    case N extends InputButton(VK_N)
    case M extends InputButton(VK_M)
    case Space extends InputButton(VK_SPACE)

    /** Usually the left mouse button
      */
    case MouseButton1 extends InputButton(MouseEvent.BUTTON1)

    /** Usually the right mouse button
      */
    case MouseButton2 extends InputButton(MouseEvent.BUTTON2)

    /** Usually the scrollwheel button
      */
    case MouseButton3 extends InputButton(MouseEvent.BUTTON3)

  /** Create a new SwingIO class.
    * @param title
    *   the title of the window frame
    * @param size
    *   the pixel size of the window frame
    * @param pixelsPerUnit
    *   the ratio of pixels over game unit
    * @param center
    *   the position of the center of the window translated to game coordinates
    * @param background
    *   the background color of the window
    * @return
    *   a new SwingIO
    */
  def apply(
      title: String,
      size: (Int, Int),
      pixelsPerUnit: Int = 100,
      center: (Double, Double) = (0, 0),
      background: Color = Color.white
  ): SwingIO =
    new SwingIOImpl(title, size, pixelsPerUnit, center, background)

  /** private implementation of the SwingIO trait. It uses a DrawableCanvas to
    * paint the window.
    */
  private class SwingIOImpl(
      val title: String,
      val size: (Int, Int),
      private var _pixelsPerUnit: Int,
      var center: (Double, Double),
      val backgroundColor: Color
  ) extends SwingIO:
    require(size._1 > 0 && size._2 > 0, "size must be positive")
    require(pixelsPerUnit > 0, "pixels/unit ratio must be positive")

    private var initialized: Boolean = false
    private lazy val frame: JFrame = createFrame()
    private lazy val canvas: DrawableCanvas = createCanvas()
    private val inputEventsAccumulator = SwingInputEventsAccumulator()

    override def pixelsPerUnit: Int = _pixelsPerUnit
    override def pixelsPerUnit_=(p: Int): Unit =
      require(p > 0, "pixels/unit ratio must be positive")
      _pixelsPerUnit = p

    override def onFrameEnd: Engine => Unit =
      engine =>
        super.onFrameEnd(engine)
        show()
        inputEventsAccumulator.onFrameEnd()

    private def initCanvas(): Unit =
      SwingUtilities.invokeAndWait(() => {
        frame.add(canvas)
        frame.pack()
        frame.addKeyListener(inputEventsAccumulator)
        frame.addMouseListener(inputEventsAccumulator)
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
      if !initialized then
        initialized = true
        initCanvas()
      if !frame.isVisible then
        SwingUtilities.invokeAndWait(() => frame.setVisible(true))
      canvas.showRenderers()

    override def inputButtonWasPressed(inputButton: InputButton): Boolean =
      inputEventsAccumulator.lastFrameInputEvents.get(inputButton) match
        case Some(events) => events.contains(InputEvent.Pressed)
        case None =>
          inputEventsAccumulator.lastInputEventBeforeLastFrame.get(
            inputButton
          ) == Some(
            InputEvent.Pressed
          )

    override def scenePointerPosition(): (Double, Double) =
      if !initialized then
        throw IllegalStateException:
          "pointerPosition cannot be queried before the GUI is initialized"
      val absolutePointerPosition = MouseInfo.getPointerInfo().getLocation()
      SwingUtilities.convertPointFromScreen(absolutePointerPosition, canvas)
      this.scenePosition((absolutePointerPosition.x, absolutePointerPosition.y))

  /** Class used as canvas for SwingIOImpl
    * @param size
    * @param color
    */
  private class DrawableCanvas(size: (Int, Int), color: Color) extends JPanel:
    private var renderers: Seq[Graphics2D => Unit] = Seq.empty
    private var show: Boolean = false

    setPreferredSize(new Dimension(size._1, size._2))
    setBackground(color)

    def add(renderer: Graphics2D => Unit): Unit = renderers =
      renderers :+ renderer
    def showRenderers(): Unit = SwingUtilities.invokeLater(() => {
      show = true; repaint()
    })
    override def paintComponent(g: Graphics): Unit =
      super.paintComponent(g)
      if !show then return
      renderers.foreach(_(g.asInstanceOf[Graphics2D]))
      renderers = Seq.empty
      show = false

  /* builder class for SwingIO, with defaults */

  /** Builder for a SwingIO class
    * @param title
    *   The window title
    * @param size
    *   The screen size
    * @param pixelsPerUnit
    *   The pixels/unit ratio
    * @param center
    *   The game-wise center position
    * @param background
    *   The background color
    */
  case class SwingIOBuilder(
      title: String = "Title",
      size: (Int, Int) = (0, 0),
      pixelsPerUnit: Int = 100,
      center: (Double, Double) = (0, 0),
      background: Color = Color.white
  )

  /** Build a SwingIO with a new title
    *
    * @param title
    *   The new title
    * @return
    *   a new builder
    */
  def withTitle(title: String): SwingIOBuilder =
    SwingIOBuilder(title = title)

  /** Build a SwingIO with a new size
    *
    * @param size
    *   The new size
    * @return
    *   a new builder
    */
  def withSize(size: (Int, Int)): SwingIOBuilder =
    SwingIOBuilder(size = size)

  /** Build a SwingIO with a new pixels/unit ratio
    *
    * @param pixelsPerUnit
    *   The new pixels/unit ratio
    * @return
    *   a new builder
    */
  def withPixelsPerUnitRatio(pixelsPerUnit: Int): SwingIOBuilder =
    SwingIOBuilder(pixelsPerUnit = pixelsPerUnit)

  /** Build a SwingIO with a new center position
    *
    * @param center
    *   The new position
    * @return
    *   a new builder
    */
  def withCenter(center: (Double, Double)): SwingIOBuilder =
    SwingIOBuilder(center = center)

  /** Build a SwingIO with a new background color
    *
    * @param color
    *   The new background color
    * @return
    *   a new builder
    */
  def withBackgroundColor(color: Color): SwingIOBuilder =
    SwingIOBuilder(background = color)

  extension (builder: SwingIOBuilder)
    /** Create a new SwingIO class from this builder configuration
      *
      * @return
      *   a SwingIO implementation
      */
    def build(): SwingIO = SwingIO(
      builder.title,
      builder.size,
      builder.pixelsPerUnit,
      builder.center,
      builder.background
    )

    /** Build a SwingIO with a new title
      *
      * @param title
      *   The new title
      * @return
      *   a new builder
      */
    def withTitle(title: String): SwingIOBuilder =
      SwingIOBuilder(
        title,
        builder.size,
        builder.pixelsPerUnit,
        builder.center,
        builder.background
      )

    /** Build a SwingIO with a new size
      *
      * @param size
      *   The new size
      * @return
      *   a new builder
      */
    def withSize(size: (Int, Int)): SwingIOBuilder =
      SwingIOBuilder(
        builder.title,
        size,
        builder.pixelsPerUnit,
        builder.center,
        builder.background
      )

    /** Build a SwingIO with a new pixels/unit ratio
      *
      * @param pixelsPerUnit
      *   The new pixels/unit ratio
      * @return
      *   a new builder
      */
    def withPixelsPerUnitRatio(pixelsPerUnit: Int): SwingIOBuilder =
      SwingIOBuilder(
        builder.title,
        builder.size,
        pixelsPerUnit,
        builder.center,
        builder.background
      )

    /** Build a SwingIO with a new center position
      *
      * @param center
      *   The new position
      * @return
      *   a new builder
      */
    def withCenter(center: (Double, Double)): SwingIOBuilder =
      SwingIOBuilder(
        builder.title,
        builder.size,
        builder.pixelsPerUnit,
        center,
        builder.background
      )

    /** Build a SwingIO with a new background color
      *
      * @param color
      *   The new background color
      * @return
      *   a new builder
      */
    def withBackgroundColor(color: Color): SwingIOBuilder =
      SwingIOBuilder(
        builder.title,
        builder.size,
        builder.pixelsPerUnit,
        builder.center,
        color
      )
