import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingIO.InputButton
import SwingIO.InputButton.*
import SwingInputHandler.{*, given}
import java.awt.Color
import java.awt.Graphics2D
import Behaviours.Identifiable
import TestUtils.*
import org.scalatest.BeforeAndAfterEach

class SwingInputHandlerTests extends AnyFlatSpec with BeforeAndAfterEach:
  var engine = newEngine
  val objId = "1"

  def newEngine = Engine(InputIOMock(), Storage())
  override protected def beforeEach(): Unit =
    engine = newEngine

  "SwingInputHandler" should "accept a mapping between input events and handlers" in:
    val f: Handler = (_: InputButton) => (_: Engine) => {}
    val h: Handler = (_: InputButton) => (_: Engine) => {}
    val initialInputHandlers = Map(
      N_0 -> f,
      N_1 -> h
    )

    val handler = new Behaviour with SwingInputHandler {
      var inputHandlers: Map[InputButton, Handler] = initialInputHandlers
    }
    handler.inputHandlers should contain theSameElementsAs initialInputHandlers

  it should "fire handlers on early update" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> a,
            N_1 -> b
          )
      )
    engine.testOnGameloopEvents(testScene)(
      onStart =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 0
        obj.bRuns shouldBe 0
    )

    engine.testOnGameloopEvents(testScene)(
      onUpdate =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 1
        obj.bRuns shouldBe 0
    )

  it should "allow to set multiple handlers for the same input events" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b)
          )
      )
    engine.testOnGameloopEvents(testScene)(
      onStart =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 0
        obj.bRuns shouldBe 0
      ,
      onUpdate =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 1
        obj.bRuns shouldBe 1
    )

  it should "allow to set a handler which fires only when input is pressed even if it's held down for more frames" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b.onlyWhenPressed)
          )
      )
    engine.testOnGameloopEvents(testScene, nFramesToRun = 3)(
      onDeinit =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 3
        obj.bRuns shouldBe 1
    )

  it should "allow to set a handler which fires only when input is held down for more than one frame" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b.onlyWhenHeld)
          )
      )
    engine.testOnGameloopEvents(testScene, nFramesToRun = 3)(
      onDeinit =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 3
        obj.bRuns shouldBe 2
    )

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
    engine.testOnGameloopEvents(testScene, nFramesToRun = 3)(
      onUpdate =
        val obj = engine.find[InputCounterObject](objId).get
        if frame == 0 then obj.bRuns shouldBe 0
        else obj.bRuns shouldBe 1
        frame += 1
      ,
      onDeinit =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 3
        obj.bRuns shouldBe 1
    )

  it should "fire the same handler multiple times if it is merged with itself" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and a)
          )
      )
    engine.testOnGameloopEvents(testScene)(
      onDeinit =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 2
    )

  "Handler modifiers" should "be applied to every handler if applied on multiple once at the same time" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b).onlyWhenPressed
          )
      )
    engine.testOnGameloopEvents(testScene, nFramesToRun = 3)(
      onDeinit =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe 1
        obj.bRuns shouldBe 1
    )

  "Handler" should "by default fire on input pressed and input held" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            // a should be fired exactly as b
            N_0 -> (a and (b.onlyWhenPressed and b.onlyWhenHeld))
          )
      )

    engine.testOnGameloopEvents(testScene, nFramesToRun = 3)(
      onDeinit =
        val obj = engine.find[InputCounterObject](objId).get
        obj.aRuns shouldBe obj.bRuns
    )

  private abstract class InputCounterObject(id: String)
      extends Behaviour
      with Identifiable(id)
      with SwingInputHandler:
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
    override def scenePointerPosition(): (Double, Double) = ???
    override def title: String = ???
    override def pixelsPerUnit: Int = ???
    override def pixelsPerUnit_=(p: Int): Unit = ???
    override def draw(renderer: Graphics2D => Unit, priority: Int): Unit = ???
    override def backgroundColor: Color = ???
    override def size: (Int, Int) = ???
    override def center_=(pos: (Double, Double)): Unit = ???
    override def center: (Double, Double) = ???
    override def show(): Unit = ???

/** A complex input test, move with WASD and teleport with left mouse click
  */
@main def main: Unit =
  import Dimensions2D.*
  import SwingRenderers.SwingSquareRenderer

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
      with SwingSquareRenderer(2, Color.blue)
      with SwingInputHandler:
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
      x = pointer._1
      y = pointer._2
      println(pointer)

    private def onMoveRight(input: InputButton)(engine: Engine): Unit =
      x += v * engine.deltaTimeNanos * Math.pow(10, -9)

    private def onMoveLeft(input: InputButton)(engine: Engine): Unit =
      x -= v * engine.deltaTimeNanos * Math.pow(10, -9)

    private def onMoveUp(input: InputButton)(engine: Engine): Unit =
      y += v * engine.deltaTimeNanos * Math.pow(10, -9)

    private def onMoveDown(input: InputButton)(engine: Engine): Unit =
      y -= v * engine.deltaTimeNanos * Math.pow(10, -9)
