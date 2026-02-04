package sge.crossdomain

import sge.audio.*
import sge.core.*
import sge.swing.SwingIO

import java.awt.{Color, Graphics2D}

/** A combined IO that includes both graphics (SwingIO) and audio (SwingSoundIO) capabilities.
  * This trait allows behaviours to access both rendering and sound functionalities
  * through a single IO instance.
  */
trait SwingWithSoundIO extends SwingIO with SoundIO

/** Companion object for creating combined SwingIO + SoundIO instances.
  */
object SwingWithSoundIO:

  /** Creates a new SwingWithSoundIO that combines graphics and audio IO.
    * @param swingIO the SwingIO instance for graphics
    * @param soundIO the SwingSoundIO instance for audio
    * @return a combined IO instance
    */
  def apply(swingIO: SwingIO, soundIO: SoundIO): SwingWithSoundIO =
    new SwingWithSoundIOImpl(swingIO, soundIO)

  /** Creates a new SwingWithSoundIO using builders.
    * @param title the window title
    * @param size the window size in pixels
    * @param pixelsPerUnit the pixels per game unit ratio
    * @param center the center position in game coordinates
    * @param background the background color
    * @param frameIconPath the path to the frame icon
    * @param masterVolume the master volume for audio (0.0 to 1.0)
    * @return a combined IO instance
    */
  def apply(
             title: String,
             size: (Int, Int),
             pixelsPerUnit: Int = 100,
             center: Vector2D = (0, 0),
             background: Color = Color.white,
             frameIconPath: String = "icon.png",
             masterVolume: Double = 1.0
  ): SwingWithSoundIO =
    new SwingWithSoundIOImpl(
      SwingIO(title, size, pixelsPerUnit, center, background, frameIconPath),
      SoundIO(masterVolume)
    )

  /** Private implementation combining SwingIO and SwingSoundIO functionality.
    */
  private class SwingWithSoundIOImpl(
      private val graphics: SwingIO,
      private val audio: SoundIO
  ) extends SwingWithSoundIO:
    
    // Delegate SwingIO methods to graphics
    export graphics.{
      title,
      size,
      pixelsPerUnit,
      pixelsPerUnit_=,
      center,
      center_=,
      backgroundColor,
      frameIconPath,
      draw,
      show,
      inputButtonWasPressed,
      scenePointerPosition
    }

    // Delegate SoundIO methods to audio
    export audio.{
      play,
      stop,
      pause,
      resume,
      setVolume,
      isPlaying,
      stopAll,
      masterVolume,
      masterVolume_=
    }

    // Override IO lifecycle methods to call both
    override def onFrameEnd: Engine => Unit =
      engine =>
        graphics.onFrameEnd(engine)
        audio.onFrameEnd(engine)

    override def onEngineStop(): Unit =
      graphics.onEngineStop()
      audio.onEngineStop()

  /** Builder for SwingWithSoundIO instances.
    */
  case class SwingWithSoundIOBuilder(
                                      title: String = "Title",
                                      size: (Int, Int) = (0, 0),
                                      pixelsPerUnit: Int = 100,
                                      center: Vector2D = (0, 0),
                                      background: Color = Color.white,
                                      frameIconPath: String = "icon.png",
                                      masterVolume: Double = 1.0
  )

  /** Build a SwingWithSoundIO with a new title.
    * @param title the new title
    * @return a new builder
    */
  def withTitle(title: String): SwingWithSoundIOBuilder =
    SwingWithSoundIOBuilder(title = title)

  /** Build a SwingWithSoundIO with a new size.
    * @param size the new size
    * @return a new builder
    */
  def withSize(size: (Int, Int)): SwingWithSoundIOBuilder =
    SwingWithSoundIOBuilder(size = size)

  extension (builder: SwingWithSoundIOBuilder)
    /** Create a new SwingWithSoundIO from this builder configuration.
      * @return a SwingWithSoundIO implementation
      */
    def build(): SwingWithSoundIO = SwingWithSoundIO(
      builder.title,
      builder.size,
      builder.pixelsPerUnit,
      builder.center,
      builder.background,
      builder.frameIconPath,
      builder.masterVolume
    )

    /** Build with a new title.
      * @param title the new title
      * @return a new builder
      */
    def withTitle(title: String): SwingWithSoundIOBuilder =
      builder.copy(title = title)

    /** Build with a new size.
      * @param size the new size
      * @return a new builder
      */
    def withSize(size: (Int, Int)): SwingWithSoundIOBuilder =
      builder.copy(size = size)

    /** Build with a new pixels/unit ratio.
      * @param pixelsPerUnit the new pixels/unit ratio
      * @return a new builder
      */
    def withPixelsPerUnitRatio(pixelsPerUnit: Int): SwingWithSoundIOBuilder =
      builder.copy(pixelsPerUnit = pixelsPerUnit)

    /** Build with a new center position.
      * @param center the new position
      * @return a new builder
      */
    def withCenter(center: Vector2D): SwingWithSoundIOBuilder =
      builder.copy(center = center)

    /** Build with a new background color.
      * @param color the new background color
      * @return a new builder
      */
    def withBackgroundColor(color: Color): SwingWithSoundIOBuilder =
      builder.copy(background = color)

    /** Build with a new frame icon path.
      * @param frameIconPath the new frame icon path
      * @return a new builder
      */
    def withFrameIconPath(frameIconPath: String): SwingWithSoundIOBuilder =
      builder.copy(frameIconPath = frameIconPath)

    /** Build with a new master volume.
      * @param volume the new master volume (0.0 to 1.0)
      * @return a new builder
      */
    def withMasterVolume(volume: Double): SwingWithSoundIOBuilder =
      builder.copy(masterVolume = volume)
