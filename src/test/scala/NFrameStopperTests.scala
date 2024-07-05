import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import GameloopTester.*
import GameloopEvent.*
import org.scalatest.BeforeAndAfterEach

// TestUtils cannot be used here as they exploit NFrameStopper themselves
class NFrameStopperTests extends AnyFlatSpec with BeforeAndAfterEach:
  var engine = Engine(
    io = new IO() {},
    storage = Storage()
  )

  override protected def beforeEach(): Unit = 
    engine = Engine(
      io = new IO() {},
      storage = Storage()
    )

  val loopGameloopEvents = Seq(EarlyUpdate, Update, LateUpdate)

  "NFrameStopper" should "stops the engine before starting the game loop if nFramesToRun is 0" in:
    val gameloopTester = new Behaviour with GameloopTester
    val testScene = () =>
      Seq(new Behaviour with NFrameStopper(nFramesToRun = 0), gameloopTester)

    engine.run(testScene)

    val correctEvents = Seq(Init, Start)
    val correctEventsWithDeinit = correctEvents ++ Seq(Deinit)

    gameloopTester.happenedEvents should (
      contain theSameElementsInOrderAs correctEventsWithDeinit
        or contain theSameElementsInOrderAs correctEvents
    )

  it should "stops the engine running the game loop just once if nFramesToRun is 1" in:
    val gameloopTester = new Behaviour with GameloopTester
    val testScene = () =>
      Seq(new Behaviour with NFrameStopper(nFramesToRun = 1), gameloopTester)

    engine.run(testScene)

    val correctEvents = Seq(Init, Start) ++ loopGameloopEvents
    val correctEventsWithDeinit = correctEvents ++ Seq(Deinit)

    gameloopTester.happenedEvents should (
      contain theSameElementsInOrderAs correctEventsWithDeinit
        or contain theSameElementsInOrderAs correctEvents
    )

  it should "stops the engine running the game loop nFramesToRun times" in:
    val nFramesToRun = 3
    val gameloopTester = new Behaviour with GameloopTester
    val testScene =
      () => Seq(new Behaviour with NFrameStopper(nFramesToRun), gameloopTester)

    engine.run(testScene)

    val correctEvents = Seq(Init, Start) ++ Iterator
      .continually(loopGameloopEvents)
      .take(nFramesToRun)
      .flatten
    val correctEventsWithDeinit = correctEvents ++ Seq(Deinit)

    gameloopTester.happenedEvents should (
      contain theSameElementsInOrderAs correctEventsWithDeinit
        or contain theSameElementsInOrderAs correctEvents
    )

  it should "throw if initialized with a negative amount of frames to run" in:
    assertThrows[IllegalArgumentException]:
      new Behaviour with NFrameStopper(-1)
