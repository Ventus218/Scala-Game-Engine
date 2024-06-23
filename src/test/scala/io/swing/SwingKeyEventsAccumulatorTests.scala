import org.scalatest.flatspec.AnyFlatSpec
import java.awt.event.KeyListener
import java.awt.event.{KeyEvent as SwingKeyEvent}
import org.scalatest.matchers.should.Matchers.*
import SwingIO.*
import SwingIO.Key.*
import SwingIO.KeyEvent.*
import javax.swing.JButton
import java.awt.Component

class SwingKeyEventsAccumulatorTests extends AnyFlatSpec:

  val mockComponent = new Component() {}
  val testKey = N_0
  val testEvents = Seq(Pressed, Released, Pressed)
  def newKeyEvent(
      key: Key = testKey,
      component: Component = mockComponent,
      event: KeyEvent = Pressed
  ): SwingKeyEvent =
    SwingKeyEvent(
      component,
      event.eventId,
      System.currentTimeMillis(),
      0,
      key.keyCode
    )

  extension (event: SwingKeyEvent)
    def fireOn(accumulator: SwingKeyEventsAccumulator): Unit =
      event.getID() match
        case Pressed.eventId  => accumulator.keyPressed(event)
        case Released.eventId => accumulator.keyReleased(event)
        case _                => {}

  "SwingKeyEventsAccumulator" should "implement KeyListener interface" in:
    SwingKeyEventsAccumulator().isInstanceOf[KeyListener] shouldBe true

  it should "have last frame key events empty when initialized" in:
    SwingKeyEventsAccumulator().lastFrameKeyEvents.isEmpty shouldBe true

  it should "have last key event before last frame empty when initialized" in:
    SwingKeyEventsAccumulator().lastKeyEventBeforeLastFrame.isEmpty shouldBe true

  it should "move current key events into last frame key events when a frame ends" in:
    val accumulator = SwingKeyEventsAccumulator()
    testEvents.foreach(e => newKeyEvent(event = e).fireOn(accumulator))
    accumulator.onFrameEnd()
    accumulator.lastFrameKeyEvents(
      testKey
    ) should contain theSameElementsInOrderAs testEvents

  it should "update last key event before last frame when a frame ends" in:
    val accumulator = SwingKeyEventsAccumulator()
    newKeyEvent(event = Pressed).fireOn(accumulator)
    accumulator.onFrameEnd()
    accumulator.lastKeyEventBeforeLastFrame.get(testKey) shouldBe None

    newKeyEvent(event = Released).fireOn(accumulator)
    accumulator.onFrameEnd()
    accumulator.lastKeyEventBeforeLastFrame(testKey) shouldBe Pressed

    accumulator.onFrameEnd()
    accumulator.lastKeyEventBeforeLastFrame(testKey) shouldBe Released
    
    accumulator.onFrameEnd()
    accumulator.lastKeyEventBeforeLastFrame(testKey) shouldBe Released
