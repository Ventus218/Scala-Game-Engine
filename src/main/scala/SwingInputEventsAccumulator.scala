import java.awt.event.KeyListener
import java.awt.event.{InputEvent as SwingInputEvent}
import java.awt.event.{KeyEvent as SwingKeyEvent}
import java.awt.event.{MouseEvent as SwingMouseEvent}
import SwingIO.*
import SwingIO.InputButton.*
import SwingIO.InputEvent.*
import java.awt.event.MouseListener

/** Accumulates InputEvents organising them frame by frame.
  *
  * onFrameEnd must be called on every frame to let the accumulator to work
  * properly.
  */
class SwingInputEventsAccumulator extends KeyListener, MouseListener:

  private var _lastInputEventBeforeLastFrame: Map[InputButton, InputEvent] =
    Map()

  /** Records the last InputEvent before the last frame.
    *
    * It can be viewed as the known state of a InputButton before the last frame
    * InputEvents had happened
    *
    * @return
    */
  def lastInputEventBeforeLastFrame: Map[InputButton, InputEvent] =
    _lastInputEventBeforeLastFrame

  /** Records the last InputEvent before the last frame.
    *
    * It can be viewed as the known state of a InputButton before the last frame
    * InputEvents had happened
    *
    * @return
    */
  private def lastInputEventBeforeLastFrame_=(
      newValue: Map[InputButton, InputEvent]
  ) = _lastInputEventBeforeLastFrame = newValue

  private var _lastFrameInputEvents: Map[InputButton, Seq[InputEvent]] = Map()

  /** A queue of all the InputEvents (grouped by InputButton) that happened
    * during the last frame. The first event in the queue is the older one.
    *
    * @return
    */
  def lastFrameInputEvents: Map[InputButton, Seq[InputEvent]] =
    _lastFrameInputEvents

  /** A queue of all the InputEvents (grouped by InputButton) that happened
    * during the last frame. The first event in the queue is the older one.
    *
    * @return
    */
  private def lastFrameInputEvents_=(
      newValue: Map[InputButton, Seq[InputEvent]]
  ) =
    _lastFrameInputEvents = newValue

  /** A queue of all the InputEvents (grouped by InputButton) that happened
    * during the current frame. The first event in the queue is the older one.
    *
    * It's used to concurrently record input events that are firing.
    * @return
    */
  private var currentFrameInputEvents: Map[InputButton, Seq[InputEvent]] = Map()

  /** Should be called on every frame, it updates the internal state moving all
    * the new input into a freezed queue which can be queried safely
    * (`lastFrameInputEvents`).
    */
  def onFrameEnd(): Unit =
    this.synchronized:
      lastFrameInputEvents.toSeq
        .flatMap((k, seq) =>
          seq.lastOption match
            case None        => Seq()
            case Some(value) => Seq((k, value))
        )
        .foreach((k, lastEvent) =>
          lastInputEventBeforeLastFrame =
            lastInputEventBeforeLastFrame.updated(k, lastEvent)
        )
      lastFrameInputEvents = currentFrameInputEvents
      currentFrameInputEvents = currentFrameInputEvents.empty

  override def keyTyped(e: SwingKeyEvent): Unit = {}
  override def keyPressed(e: SwingKeyEvent): Unit = processInputEvent(e)
  override def keyReleased(e: SwingKeyEvent): Unit = processInputEvent(e)

  override def mouseClicked(e: SwingMouseEvent): Unit = {}
  override def mouseExited(e: SwingMouseEvent): Unit = {}
  override def mouseEntered(e: SwingMouseEvent): Unit = {}
  override def mousePressed(e: SwingMouseEvent): Unit = processInputEvent(e)
  override def mouseReleased(e: SwingMouseEvent): Unit = processInputEvent(e)

  /** Queues a new swing input event
    *
    * @param e
    */
  private def processInputEvent(e: SwingInputEvent): Unit =
    this.synchronized:
      val event = e.getID() match
        case SwingKeyEvent.KEY_PRESSED      => Pressed
        case SwingMouseEvent.MOUSE_PRESSED  => Pressed
        case SwingKeyEvent.KEY_RELEASED     => Released
        case SwingMouseEvent.MOUSE_RELEASED => Released

      val id = e match
        case e: SwingKeyEvent   => e.getKeyCode()
        case e: SwingMouseEvent => e.getButton()

      InputButton.values.find(_.id == id) match
        case Some(inputButton) =>
          currentFrameInputEvents =
            currentFrameInputEvents.updatedWith(inputButton)(_ match
              case Some(seq) => Some(seq :+ event)
              case None      => Some(Seq(event))
            )
        case None =>
          System.err.println(
            s"Missing $InputButton enum value for received Swing InputEvent"
          )
