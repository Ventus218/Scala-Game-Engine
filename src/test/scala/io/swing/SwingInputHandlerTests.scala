import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import SwingIO.InputButton
import SwingIO.InputButton.*
import SwingInputHandler.{*, given}
import java.awt.Color
import java.awt.Graphics2D
import Behaviours.Identifiable
import TestUtils.*

class SwingInputHandlerTests extends AnyFlatSpec:
  val engine = Engine(InputIOMock(), Storage())
  val objId = "1"

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
    engine.testOnStart(testScene):
      val obj = engine.find[InputCounterObject](objId).get
      obj.aRuns shouldBe 0
      obj.bRuns shouldBe 0

    engine.testOnUpdate(testScene):
      val obj = engine.find[InputCounterObject](objId).get
      obj.aRuns shouldBe 1
      obj.bRuns shouldBe 0

  it should "allow to set multiple handlers for the same input events" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b)
          )
      )
    engine.testOnStart(testScene):
      val obj = engine.find[InputCounterObject](objId).get
      obj.aRuns shouldBe 0
      obj.bRuns shouldBe 0
    engine.testOnUpdate(testScene):
      val obj = engine.find[InputCounterObject](objId).get
      obj.aRuns shouldBe 1
      obj.bRuns shouldBe 1

  it should "allow to set a handler which fires only once while the input button is held pressed down" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b.fireJustOnceIfHeld)
          )
      )
    engine.testOnDeinit(testScene, nFramesToRun = 3):
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
    engine.testOnDeinit(testScene):
      val obj = engine.find[InputCounterObject](objId).get
      obj.aRuns shouldBe 2

  "fireJustOnceIfHeld" should "make apply to every handler if applied on multiple once at the same time" in:
    val testScene = () =>
      Seq(
        new InputCounterObject(objId):
          var inputHandlers: Map[InputButton, Handler] = Map(
            N_0 -> (a and b).fireJustOnceIfHeld
          )
      )
    engine.testOnDeinit(testScene, nFramesToRun = 3):
      val obj = engine.find[InputCounterObject](objId).get
      obj.aRuns shouldBe 1
      obj.bRuns shouldBe 1

  private abstract class InputCounterObject(id: String)
      extends Behaviour
      with Identifiable(id)
      with SwingInputHandler:
    var aRuns = 0
    var bRuns = 0
    def a(input: InputButton)(engine: Engine): Unit = aRuns += 1
    def b(input: InputButton)(engine: Engine): Unit = bRuns += 1

  private class InputIOMock extends SwingIO:
    override def inputButtonWasPressed(inputButton: InputButton): Boolean =
      return inputButton == N_0
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
  val engine = Engine(io, Storage())

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
      MouseButton3 -> (onMoveRight and onMoveUp),
      MouseButton1 -> onTeleport.fireJustOnceIfHeld,
      MouseButton2 -> (onTeleport.fireJustOnceIfHeld and onMoveDown)
    )

    val v = 20
    var shouldTeleport = false
    var moveRight = false
    var moveLeft = false
    var moveUp = false
    var moveDown = false

    private def onTeleport(input: InputButton)(engine: Engine): Unit =
      shouldTeleport = true
    private def onMoveRight(input: InputButton)(engine: Engine): Unit =
      moveRight = true
    private def onMoveLeft(input: InputButton)(engine: Engine): Unit =
      moveLeft = true
    private def onMoveUp(input: InputButton)(engine: Engine): Unit =
      moveUp = true
    private def onMoveDown(input: InputButton)(engine: Engine): Unit =
      moveDown = true

    override def onUpdate: Engine => Unit = (engine) =>
      if moveRight then x += v * engine.deltaTimeNanos * Math.pow(10, -9)
      if moveLeft then x -= v * engine.deltaTimeNanos * Math.pow(10, -9)
      if moveUp then y += v * engine.deltaTimeNanos * Math.pow(10, -9)
      if moveDown then y -= v * engine.deltaTimeNanos * Math.pow(10, -9)
      if shouldTeleport then
        shouldTeleport = false
        val pointer = engine.io.asInstanceOf[SwingIO].scenePointerPosition()
        x = pointer._1
        y = pointer._2
        println(pointer)

      moveRight = false
      moveLeft = false
      moveUp = false
      moveDown = false
