import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingIO.InputButton
import SwingIO.InputButton.*
import SwingInputHandler.*
import java.awt.Color
import java.awt.Graphics2D
import Behaviours.Identifiable
import TestUtils.*

class SwingInputHandlerTests extends AnyFlatSpec:

  "SwingInputHandler" should "accept a mapping between input events and handlers" in:
    val f: Handler = (_: InputButton) => {}
    val h: Handler = (_: InputButton) => {}
    val initialInputHandlers = Map(
      N_0 -> f,
      N_1 -> h
    )

    val handler = new Behaviour with SwingInputHandler {
      var inputHandlers: Map[InputButton, Handler] = initialInputHandlers
    }
    handler.inputHandlers should contain theSameElementsAs initialInputHandlers

  it should "fire handlers on early update" in:
    val engine = Engine(InputIOMock(), Storage())
    val objId = "1"
    val testScene = () => Seq(GameObject(objId))
    engine.testOnStart(testScene):
      val obj = engine.find[GameObject](objId).get
      obj.jumped shouldBe false
      obj.fired shouldBe false

    engine.testOnUpdate(testScene):
      val obj = engine.find[GameObject](objId).get
      obj.jumped shouldBe true
      obj.fired shouldBe false

  private class GameObject(id: String)
      extends Behaviour
      with Identifiable(id)
      with SwingInputHandler:
    var jumped = false
    var fired = false

    var inputHandlers: Map[InputButton, Handler] = Map(
      N_0 -> onJump,
      N_1 -> onFire
    )

    private def onJump(inputButton: InputButton): Unit =
      jumped = true
    private def onFire(inputButton: InputButton): Unit =
      fired = true

  private class InputIOMock extends SwingIO:
    override def inputButtonWasPressed(inputButton: InputButton): Boolean =
      return inputButton == N_0
    override def scenePointerPosition(): (Double, Double) = ???
    override def title: String = ???
    override def pixelsPerUnit: Int = ???
    override def pixelsPerUnit_=(p: Int): Unit = ???
    override def draw(renderer: Graphics2D => Unit): Unit = ???
    override def backgroundColor: Color = ???
    override def size: (Int, Int) = ???
    override def center_=(pos: (Double, Double)): Unit = ???
    override def center: (Double, Double) = ???
    override def show(): Unit = ???