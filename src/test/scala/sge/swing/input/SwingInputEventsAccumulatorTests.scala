package sge.swing.input

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import javax.swing.JButton
import java.awt.Component
import java.awt.event.{KeyListener, MouseListener, KeyEvent as SwingKeyEvent}
import sge.swing.*
import SwingIO.*
import InputButton.*
import InputEvent.*

class SwingInputEventsAccumulatorTests extends AnyFlatSpec:

  val mockComponent = new Component() {}
  val testInputButton = N_0
  val testEvents = Seq(Pressed, Released, Pressed)
  def newKeyEvent(
      inputButton: InputButton = testInputButton,
      component: Component = mockComponent,
      event: InputEvent = Pressed
  ): SwingKeyEvent =
    SwingKeyEvent(
      component,
      if event == Pressed then SwingKeyEvent.KEY_PRESSED
      else SwingKeyEvent.KEY_RELEASED,
      System.currentTimeMillis(),
      0,
      inputButton.id
    )

  extension (event: SwingKeyEvent)
    def fireOn(accumulator: SwingInputEventsAccumulator): Unit =
      event.getID() match
        case SwingKeyEvent.KEY_PRESSED  => accumulator.keyPressed(event)
        case SwingKeyEvent.KEY_RELEASED => accumulator.keyReleased(event)
        case _                          => {}

  "SwingInputEventsAccumulator" should "implement KeyListener interface" in:
    SwingInputEventsAccumulator().isInstanceOf[KeyListener] shouldBe true

  it should "implement MouseListener interface" in:
    SwingInputEventsAccumulator().isInstanceOf[MouseListener] shouldBe true

  it should "have last frame input events empty when initialized" in:
    SwingInputEventsAccumulator().lastFrameInputEvents.isEmpty shouldBe true

  it should "have last input event before last frame empty when initialized" in:
    SwingInputEventsAccumulator().lastInputEventBeforeLastFrame.isEmpty shouldBe true

  it should "move current input events into last frame input events when a frame ends" in:
    val accumulator = SwingInputEventsAccumulator()
    testEvents.foreach(e => newKeyEvent(event = e).fireOn(accumulator))
    accumulator.onFrameEnd()
    accumulator.lastFrameInputEvents(
      testInputButton
    ) should contain theSameElementsInOrderAs testEvents

  it should "update last input event before last frame when a frame ends" in:
    val accumulator = SwingInputEventsAccumulator()
    newKeyEvent(event = Pressed).fireOn(accumulator)
    accumulator.onFrameEnd()
    accumulator.lastInputEventBeforeLastFrame.get(testInputButton) shouldBe None

    newKeyEvent(event = Released).fireOn(accumulator)
    accumulator.onFrameEnd()
    accumulator.lastInputEventBeforeLastFrame(testInputButton) shouldBe Pressed

    accumulator.onFrameEnd()
    accumulator.lastInputEventBeforeLastFrame(testInputButton) shouldBe Released

    accumulator.onFrameEnd()
    accumulator.lastInputEventBeforeLastFrame(testInputButton) shouldBe Released
