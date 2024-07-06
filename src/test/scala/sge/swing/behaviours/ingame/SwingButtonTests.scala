package sge.swing.behaviours.ingame

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterEach
import sge.core.*
import behaviours.dimension2d.*
import metrics.Vector2D.*
import sge.testing.TestUtils.*
import java.awt.{Graphics2D, Color}
import sge.swing.input.*
import sge.swing.SwingIO
import InputButton.*

class SwingButtonTests extends AnyFlatSpec with BeforeAndAfterEach:
  var testButton: TestButton = newTestButton()
  def newTestButton(): TestButton =
    TestButton("Button", Set(MouseButton1), position = (0, 0))
  def newDefaultButton(): SwingButton = new Behaviour
    with SwingButton
    with Positionable
    with SwingRectRenderer(1, 1, Color.gray)

  var engine: Engine = newEngine()
  def newEngine(io: MockSwingIO = CenterPressReleaseMockSwingIO()): Engine =
    Engine(io, Storage())

  def testScene: Scene = () => Seq(testButton)

  override protected def beforeEach(): Unit =
    testButton = newTestButton()
    engine = newEngine()

  "SwingButton" should "have a rectangular shape" in:
    testButton.isInstanceOf[SwingRectRenderer] shouldBe true

  it should "have a mutable text value" in:
    testButton.buttonText shouldBe "Button"
    testButton.buttonText = "hello"
    testButton.buttonText shouldBe "hello"

  it should "have a text default value of an empty string" in:
    newDefaultButton().buttonText shouldBe ""

  it should "be triggered on InputButton release and not on press" in:
    test(engine) on testScene runningFor 2 frames so that:
      _.onUpdate:
        engine.mockIO.frameCount match
          case 1 => testButton.buttonPresses shouldBe 0
          case 2 => testButton.buttonPresses shouldBe 1

  it should "be triggered during EarlyUpdate" in:
    test(engine) on testScene runningFor 2 frames so that:
      _.onUpdate:
        engine.mockIO.frameCount match
          case 1 => testButton.buttonPresses shouldBe 0
          case 2 => testButton.buttonPresses shouldBe 1
      .onLateUpdate:
        engine.mockIO.frameCount match
          case 1 => testButton.buttonPresses shouldBe 0
          case 2 => testButton.buttonPresses shouldBe 1

  it should "accept a set of InputButtons from which it can be triggered" in:
    val triggers = Set(MouseButton1, MouseButton2)
    val b = TestButton("", inputButtonTriggers = triggers)
    b.inputButtonTriggers should contain theSameElementsAs triggers

  it should "be triggered only by the specified InputButtons" in:
    val triggers = Set(MouseButton1, MouseButton3)
    val b = TestButton("", inputButtonTriggers = triggers)

    test(engine) on (() => Seq(b)) runningFor 2 frames so that:
      _.onUpdate:
        engine.mockIO.frameCount match
          case 1 => b.buttonPresses shouldBe 0
          case 2 => b.buttonPresses shouldBe 1

  it should "be triggered by all the specified InputButtons" in:
    val triggers = Set(MouseButton1, MouseButton2)
    val b = TestButton("", inputButtonTriggers = triggers)

    test(engine) on (() => Seq(b)) runningFor 2 frames so that:
      _.onUpdate:
        engine.mockIO.frameCount match
          case 1 => b.buttonPresses shouldBe 0
          case 2 => b.buttonPresses shouldBe 2

  it should "have MouseButton1 as default inputButtonTriggers" in:
    newDefaultButton().inputButtonTriggers should contain only MouseButton1

  it should "not trigger if the pointer is not the button area when releasing InputButton" in:
    testButton.position = testButton.position + Versor2D.up * 100
    test(engine) on testScene runningFor 2 frames so that:
      _.onUpdate:
        engine.mockIO.frameCount match
          case 1 => ()
          case 2 => testButton.buttonPresses shouldBe 0

  it should "not trigger if InputButton is released onto the button but the press was not" in:
    val engine = newEngine(CornerPressCenterReleaseMockSwingIO())

    test(engine) on testScene runningFor 2 frames so that:
      _.onUpdate:
        engine.mockIO.frameCount match
          case 1 => ()
          case 2 => testButton.buttonPresses shouldBe 0

  it should "not trigger if InputButton is pressed onto the button but the release is not" in:
    val engine = newEngine(CenterPressCornerReleaseMockSwingIO())

    test(engine) on testScene runningFor 2 frames so that:
      _.onUpdate:
        engine.mockIO.frameCount match
          case 1 => ()
          case 2 => testButton.buttonPresses shouldBe 0

  class TestButton(
      _buttonText: String,
      inputButtonTriggers: Set[InputButton],
      position: Vector2D = (0, 0),
      dimension: Vector2D = (100, 20)
  ) extends Behaviour
      with SwingButton(
        _buttonText = _buttonText,
        _inputButtonTriggers = inputButtonTriggers
      )
      with Positionable(position)
      with SwingRectRenderer(dimension.x, dimension.y, Color.gray):
    var buttonPresses = 0
    override def onButtonPressed: Engine => Unit = _ => buttonPresses += 1

  /** A SwingIO where:
    *   - Frame 1:
    *     - The pointer is in the center of the screen
    *     - The MouseButton1 and MouseButton2 are pressed
    *   - Frame 2:
    *     - The pointer is in the center of the screen
    *     - The MouseButton1 and MouseButton2 are not pressed
    */
  class CenterPressReleaseMockSwingIO extends MockSwingIO:
    override def scenePointerPosition(): Vector2D = (0, 0)
    override def inputButtonWasPressed(inputButton: InputButton): Boolean =
      frameCount == 1 && Set(MouseButton1, MouseButton2).contains(inputButton)

  /** A SwingIO where:
    *   - Frame 1:
    *     - The pointer is in the center of the screen
    *     - The MouseButton1 and MouseButton2 are pressed
    *   - Frame 2:
    *     - The pointer is in the top-right corner of the screen
    *     - The MouseButton1 and MouseButton2 are not pressed
    */
  class CenterPressCornerReleaseMockSwingIO extends MockSwingIO:
    override def scenePointerPosition(): Vector2D =
      if frameCount == 1 then (0, 0) else (1000, 1000)
    override def inputButtonWasPressed(inputButton: InputButton): Boolean =
      frameCount == 1 && Set(MouseButton1, MouseButton2).contains(inputButton)

  /** A SwingIO where:
    *   - Frame 1:
    *     - The pointer is in the top-right corner of the screen
    *     - The MouseButton1 and MouseButton2 are pressed
    *   - Frame 2:
    *     - The pointer is in the center of the screen
    *     - The MouseButton1 and MouseButton2 are not pressed
    */
  class CornerPressCenterReleaseMockSwingIO extends MockSwingIO:
    override def scenePointerPosition(): Vector2D =
      if frameCount == 1 then (1000, 1000) else (0, 0)
    override def inputButtonWasPressed(inputButton: InputButton): Boolean =
      frameCount == 1 && Set(MouseButton1, MouseButton2).contains(inputButton)

  class MockSwingIO extends SwingIO:
    var frameCount = 1
    override def onFrameEnd: Engine => Unit = _ => frameCount += 1

    override def scenePointerPosition(): Vector2D = ???
    override def inputButtonWasPressed(inputButton: InputButton): Boolean = ???
    override def size: (Int, Int) = ???
    override def center_=(pos: Vector2D): Unit = ???
    override def backgroundColor: Color = ???
    override def draw(renderer: Graphics2D => Unit, priority: Int): Unit = ()
    override def pixelsPerUnit_=(p: Int): Unit = ???
    override def show(): Unit = ()
    override def pixelsPerUnit: Int = ???
    override def title: String = "???"
    override def center: Vector2D = ???

  extension (e: Engine) def mockIO: MockSwingIO = e.io.asInstanceOf[MockSwingIO]
