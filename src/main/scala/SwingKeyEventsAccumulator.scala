import java.awt.event.KeyListener
import java.awt.event.{KeyEvent as SwingKeyEvent}
import java.awt.event.{MouseEvent as SwingMouseEvent}
import SwingIO.*
import SwingIO.Key.*
import SwingIO.KeyEvent.*

class SwingKeyEventsAccumulator extends KeyListener:

  private var _lastKeyEventBeforeLastFrame: Map[Key, KeyEvent] = Map()
  def lastKeyEventBeforeLastFrame: Map[Key, KeyEvent] =
    _lastKeyEventBeforeLastFrame
  private def lastKeyEventBeforeLastFrame_=(
      newValue: Map[Key, KeyEvent]
  ) = _lastKeyEventBeforeLastFrame = newValue

  private var _lastFrameKeyEvents: Map[Key, Seq[KeyEvent]] = Map()
  def lastFrameKeyEvents: Map[Key, Seq[KeyEvent]] = _lastFrameKeyEvents
  private def lastFrameKeyEvents_=(newValue: Map[Key, Seq[KeyEvent]]) =
    _lastFrameKeyEvents = newValue

  private var currentFrameKeyEvents: Map[Key, Seq[KeyEvent]] = Map()

  def onFrameEnd(): Unit =
    lastFrameKeyEvents.toSeq
      .flatMap((k, seq) =>
        seq.lastOption match
          case None        => Seq()
          case Some(value) => Seq((k, value))
      )
      .foreach((k, lastEvent) =>
        lastKeyEventBeforeLastFrame =
          lastKeyEventBeforeLastFrame.updated(k, lastEvent)
      )
    lastFrameKeyEvents = currentFrameKeyEvents
    currentFrameKeyEvents = currentFrameKeyEvents.empty

  override def keyTyped(e: SwingKeyEvent): Unit = {}
  override def keyPressed(e: SwingKeyEvent): Unit = processKeyEvent(e)
  override def keyReleased(e: SwingKeyEvent): Unit = processKeyEvent(e)

  private def processKeyEvent(e: SwingKeyEvent): Unit =
    val event = e.getID() match
      case SwingKeyEvent.KEY_PRESSED      => Pressed
      case SwingMouseEvent.MOUSE_PRESSED  => Pressed
      case SwingKeyEvent.KEY_RELEASED     => Released
      case SwingMouseEvent.MOUSE_RELEASED => Released

    Key.values.find(_.id == e.getKeyCode()) match
      case Some(key) =>
        currentFrameKeyEvents = currentFrameKeyEvents.updatedWith(key)(_ match
          case Some(seq) => Some(seq :+ event)
          case None      => Some(Seq(event))
        )
      case None =>
        // Logging instead of throwing may be a better choice
        throw Exception(
          s"Missing $Key enum value for received Swing KeyEvent: ${e.getKeyChar()}"
        )
