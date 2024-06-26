import java.awt.event.KeyListener
import java.awt.event.{InputEvent as SwingInputEvent}
import java.awt.event.{KeyEvent as SwingKeyEvent}
import java.awt.event.{MouseEvent as SwingMouseEvent}
import SwingIO.*
import SwingIO.InputButton.*
import SwingIO.InputEvent.*
import java.awt.event.MouseListener

class SwingInputEventsAccumulator extends KeyListener, MouseListener:

  private var _lastInputEventBeforeLastFrame: Map[InputButton, InputEvent] =
    Map()
  def lastInputEventBeforeLastFrame: Map[InputButton, InputEvent] =
    _lastInputEventBeforeLastFrame
  private def lastInputEventBeforeLastFrame_=(
      newValue: Map[InputButton, InputEvent]
  ) = _lastInputEventBeforeLastFrame = newValue

  private var _lastFrameInputEvents: Map[InputButton, Seq[InputEvent]] = Map()
  def lastFrameInputEvents: Map[InputButton, Seq[InputEvent]] =
    _lastFrameInputEvents
  private def lastFrameInputEvents_=(
      newValue: Map[InputButton, Seq[InputEvent]]
  ) =
    _lastFrameInputEvents = newValue

  private var currentFrameInputEvents: Map[InputButton, Seq[InputEvent]] = Map()

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
          // Logging instead of throwing may be a better choice
          throw Exception(
            s"Missing $InputButton enum value for received Swing KeyEvent: $e"
          )
