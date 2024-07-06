package sge.swing.behaviours.ingame

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterEach
import sge.core.*
import behaviours.*
import EngineUtils.*
import metrics.Vector2D.*
import sge.swing.*
import input.*
import SwingIO.*
import InputButton.*
import InputHandler.*
import sge.testing.TestUtils.*
import java.awt.{Color, Graphics2D}

class InputHandlerTests extends AnyFlatSpec with BeforeAndAfterEach:
  var engine = newEngine
  val objId = "1"

  def newEngine = Engine(InputIOMock(), Storage())
  override protected def beforeEach(): Unit =
    engine = newEngine

  "InputHandler" should "accept a mapping between input events and handlers" in:
    val f: Handler = (_: InputButton) => (_: Engine) => {}
    val h: Handler = (_: InputButton) => (_: Engine) => {}
    val initialInputHandlers = Map(
      N_0 -> f,
      N_1 -> h
    )

    val handler = new Behaviour with InputHandler {
      var inputHandlers: Map[InputButton, Handler] = initialInputHandlers
    }
    handler.inputHandlers should contain theSameElementsAs initialInputHandlers

  it should "fire handlers on early update" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> a
          )
      )
    test(engine) on testScene soThat:
      _.onStart:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 0
      .onUpdate:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 1

  it should "allow to set multiple handlers for the same input events" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b)
          )
      )
    test(engine) on testScene soThat:
      _.onStart:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 0
        obj.bRuns shouldBe 0
      .onUpdate:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 1
        obj.bRuns shouldBe 1

  it should "allow to set a handler which fires only when input is pressed even if it's held down for more frames" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b.onlyWhenPressed)
          )
      )
    test(engine) on testScene runningFor 3 frames so that:
      _.onDeinit:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 3
        obj.bRuns shouldBe 1

  it should "allow to set a handler which fires only when input is held down for more than one frame" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b.onlyWhenHeld)
          )
      )
    test(engine) on testScene runningFor 3 frames so that:
      _.onDeinit:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 3
        obj.bRuns shouldBe 2

  it should "allow to set a handler which fires only when input is released" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> a,
            N_1 -> b.onlyWhenReleased
          )
      )

    var frame = 0
    test(engine) on testScene runningFor 3 frames so that:
      _.onUpdate:
        val obj = engine.find[InputCounterObject](objId).get
        if frame == 0 then obj.bRuns shouldBe 0
        else obj.bRuns shouldBe 1
        frame += 1
      .onDeinit:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 3
        obj.bRuns shouldBe 1

  it should "fire the same handler multiple times if it is merged with itself" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and a)
          )
      )
    test(engine) on testScene soThat:
      _.onDeinit:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 2

  "Handler modifiers" should "be applied to every handler if applied on multiple once at the same time" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b).onlyWhenPressed
          )
      )
    test(engine) on testScene runningFor 3 frames so that:
      _.onDeinit:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 1
        obj.bRuns shouldBe 1

  "Handler" should "by default fire on input pressed and input held" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            // a should be fired exactly as b
            N_0 -> (a and (b.onlyWhenPressed and b.onlyWhenHeld))
          )
      )

    test(engine) on testScene runningFor 3 frames so that:
      _.onDeinit:
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe obj.bRuns

  private abstract class InputCounterObject(id: String)
      extends Behaviour
      with Identifiable(id)
      with InputHandler:
    var aRuns = 0
    var bRuns = 0
    def a(input: InputButton)(engine: Engine): Unit = aRuns += 1
    def b(input: InputButton)(engine: Engine): Unit = bRuns += 1

  /** A mock SwingIO which fakes a continuous press on N_0 and a press on N_1
    * just for the first frame
    */
  private class InputIOMock extends SwingIO:
    var isFirstFrame = true
    override def onFrameEnd: Engine => Unit = (engine) =>
      isFirstFrame = false
      super.onFrameEnd(engine)
    override def inputButtonWasPressed(inputButton: InputButton): Boolean =
      return inputButton == N_0 || (isFirstFrame && inputButton == N_1)
    override def scenePointerPosition(): Vector2D = ???
    override def title: String = ???
    override def pixelsPerUnit: Int = ???
    override def pixelsPerUnit_=(p: Int): Unit = ???
    override def draw(renderer: Graphics2D => Unit, priority: Int): Unit = ???
    override def backgroundColor: Color = ???
    override def size: (Int, Int) = ???
    override def center_=(pos: Vector2D): Unit = ???
    override def center: Vector2D = ???
    override def show(): Unit = ???

/** A complex input test, move with WASD and teleport with left mouse click
  */
@main def main: Unit =
  import dimension2d.Positionable

  val io = SwingIO
    .withTitle("Test")
    .withSize((400, 400))
    .withPixelsPerUnitRatio(5)
    .build()
  val engine = Engine(
    io,
    Storage(),
    fpsLimit = 20 // low fps to test onlyWhenHeld modifier
  )

  engine.run: () =>
    Seq(GameObject())

  class GameObject
      extends Behaviour
      with Positionable
      with SquareRenderer(2, Color.blue)
      with InputHandler:
    var inputHandlers: Map[InputButton, Handler] = Map(
      D -> onMoveRight,
      A -> onMoveLeft,
      W -> onMoveUp,
      S -> onMoveDown,
      E -> (onMoveRight and onMoveUp),
      MouseButton1 -> onTeleport, // same as (onTeleport.onlyWhenPressed and onTeleport.onlyWhenHeld)
      MouseButton3 -> onTeleport.onlyWhenPressed,
      MouseButton2 -> onTeleport.onlyWhenHeld,
      Space -> onTeleport.onlyWhenReleased
    )

    val v = 20

    private def onTeleport(input: InputButton)(engine: Engine): Unit =
      val pointer = engine.io.asInstanceOf[SwingIO].scenePointerPosition()
      position = pointer
      println(pointer)

    private def onMoveRight(input: InputButton)(engine: Engine): Unit =
      position = position + Versor2D.right * v * engine.deltaTimeSeconds

    private def onMoveLeft(input: InputButton)(engine: Engine): Unit =
      position = position + Versor2D.left * v * engine.deltaTimeSeconds

    private def onMoveUp(input: InputButton)(engine: Engine): Unit =
      position = position + Versor2D.up * v * engine.deltaTimeSeconds

    private def onMoveDown(input: InputButton)(engine: Engine): Unit =
      position = position + Versor2D.down * v * engine.deltaTimeSeconds
