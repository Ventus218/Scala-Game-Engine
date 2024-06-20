import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import LifecycleTester.*
import LifecycleEvent.*

// TestUtils cannot be used here as they exploit NFrameStopper themselves
class NFrameStopperTests extends AnyFlatSpec:
  val engine = Engine(new IO() {}, Storage())

  val loopLifecycleEvents = Seq(EarlyUpdate, Update, LateUpdate)

  "NFrameStopper" should "stops the engine before starting the game loop if nFramesToRun is 0" in:
    val lifecycleTester = new Behaviour with LifecycleTester
    val testScene = () =>
      Seq(new Behaviour with NFrameStopper(nFramesToRun = 0), lifecycleTester)

    engine.run(testScene)

    val correctEvents = Seq(Init, Enable, Start)
    val correctEventsWithDeinit = correctEvents ++ Seq(Deinit)

    lifecycleTester.happenedEvents should (
      contain theSameElementsInOrderAs correctEventsWithDeinit
        or contain theSameElementsInOrderAs correctEvents
    )

  it should "stops the engine running the game loop just once if nFramesToRun is 1" in:
    val lifecycleTester = new Behaviour with LifecycleTester
    val testScene = () =>
      Seq(new Behaviour with NFrameStopper(nFramesToRun = 1), lifecycleTester)

    engine.run(testScene)

    val correctEvents = Seq(Init, Enable, Start) ++ loopLifecycleEvents
    val correctEventsWithDeinit = correctEvents ++ Seq(Deinit)

    lifecycleTester.happenedEvents should (
      contain theSameElementsInOrderAs correctEventsWithDeinit
        or contain theSameElementsInOrderAs correctEvents
    )

  it should "stops the engine running the game loop nFramesToRun times" in:
    val nFramesToRun = 3
    val lifecycleTester = new Behaviour with LifecycleTester
    val testScene =
      () => Seq(new Behaviour with NFrameStopper(nFramesToRun), lifecycleTester)

    engine.run(testScene)

    val correctEvents = Seq(Init, Enable, Start) ++ Iterator
      .continually(loopLifecycleEvents)
      .take(nFramesToRun)
      .flatten
    val correctEventsWithDeinit = correctEvents ++ Seq(Deinit)

    lifecycleTester.happenedEvents should (
      contain theSameElementsInOrderAs correctEventsWithDeinit
        or contain theSameElementsInOrderAs correctEvents
    )

  it should "throw if initialized with a negative amount of frames to run" in:
    assertThrows[IllegalArgumentException]:
      new Behaviour with NFrameStopper(-1)
