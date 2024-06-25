import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingIO.Key
import SwingIO.Key.*
import SwingInputHandler.*
import java.awt.Color
import java.awt.Graphics2D
import Behaviours.Identifiable
import TestUtils.*

class SwingInputHandlerTests extends AnyFlatSpec:

  "SwingInputHandler" should "accept a mapping between key events and handlers" in:
    val f: Handler = (key: Key) => {}
    val h: Handler = (key: Key) => {}
    val initialKeyHandlers = Map(
      N_0 -> f,
      N_1 -> h
    )

    val handler = new Behaviour with SwingInputHandler {
      var keyHandlers: Map[Key, Handler] = initialKeyHandlers
    }
    handler.keyHandlers should contain theSameElementsAs initialKeyHandlers

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

    var keyHandlers: Map[Key, Handler] = Map(
      N_0 -> onJump,
      N_1 -> onFire
    )

    private def onJump(key: Key): Unit =
      jumped = true
    private def onFire(key: Key): Unit =
      fired = true

  private class InputIOMock extends SwingIO:
    override def keyWasPressed(key: Key): Boolean = return key == N_0

    override def title: String = ???
    override def pixelsPerUnit: Int = ???
    override def pixelsPerUnit_=(p: Int): Unit = ???
    override def draw(renderer: Graphics2D => Unit): Unit = ???
    override def backgroundColor: Color = ???
    override def size: (Int, Int) = ???
    override def center_=(pos: (Double, Double)): Unit = ???
    override def center: (Double, Double) = ???
    override def show(): Unit = ???
