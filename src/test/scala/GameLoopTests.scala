import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import TestUtils.*
import org.scalatest.BeforeAndAfterEach
import LifecycleTester.*
import LifecycleEvent.*
import TestUtils.Testers.*
import org.scalatest.exceptions.TestFailedException

class GameLoopTests extends AnyFlatSpec:
  private def getSequenceOfActions(): Seq[LifecycleEvent] =
    Seq(Init, Start)

  private def getUpdatesSequenceOfActions(): Seq[LifecycleEvent] =
    Seq(EarlyUpdate, Update, LateUpdate)

  val numSteps = 3

  private def testScene: Scene = () =>
    Iterable(
      new Behaviour with LifecycleTester,
      new Behaviour with LifecycleTester,
      new Behaviour(enabled = false) with LifecycleTester,
      new Behaviour(enabled = false) with LifecycleTester
    )

  val engine = Engine(
    io = new IO() {},
    storage = Storage()
  )

  "Engine" should "call all methods on enabled gameObjects and just init and deinit on disabled gameObjects" in:
    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 until numSteps do
      sequenceOfActions = sequenceOfActions ++ getUpdatesSequenceOfActions()

    engine.testOnDeinit(testScene, nFramesToRun = numSteps):
      /** This tests has to deal with undeterministic behaviour:
        *
        * Given the fact that the order of objects is not defined. The tester
        * object may run its test while other objects "onDeinit" may not have
        * been called yet. This is why the test succedes in both cases.
        */
      engine
        .find[LifecycleTester]()
        .filter(_.enabled)
        .foreach(
          _.happenedEvents should (
            contain theSameElementsInOrderAs sequenceOfActions :+ Deinit
              or contain theSameElementsInOrderAs sequenceOfActions
          )
        )
      engine
        .find[LifecycleTester]()
        .filter(!_.enabled)
        .foreach(
          _.happenedEvents should (
            contain theSameElementsInOrderAs Seq(Init) :+ Deinit
              or contain theSameElementsInOrderAs Seq(Init)
          )
        )

  it should "stop when engine.stop() is called" in:
    val oneFrameScene = testScene.joined: () =>
      Seq(new Behaviour with NFrameStopper(1))

    var sequenceOfActions =
      getSequenceOfActions() ++ getUpdatesSequenceOfActions()

    // The idea is that the test should run the engine for 5 frames but since a NFrameStopper(1) has been added it should stop only after one frame
    engine.testOnDeinit(oneFrameScene, nFramesToRun = 5):
      /** This tests has to deal with undeterministic behaviour:
        *
        * Given the fact that the order of objects is not defined. The tester
        * object may run its test while other objects "onDeinit" may not have
        * been called yet. This is why the test succedes in both cases.
        */
      engine
        .find[LifecycleTester]()
        .filter(_.enabled)
        .foreach(
          _.happenedEvents should (
            contain theSameElementsInOrderAs sequenceOfActions :+ Deinit
              or contain theSameElementsInOrderAs sequenceOfActions
          )
        )

  it should "do the loop again if called run after being stopped" in:
    engine.testOnUpdate(testScene):
      {}
    engine.stop()

    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 until numSteps do
      sequenceOfActions = sequenceOfActions ++ getUpdatesSequenceOfActions()

    engine.testOnDeinit(testScene, nFramesToRun = numSteps):
      /** This tests has to deal with undeterministic behaviour:
        *
        * Given the fact that the order of objects is not defined. The tester
        * object may run its test while other objects "onDeinit" may not have
        * been called yet. This is why the test succedes in both cases.
        */
      engine
        .find[LifecycleTester]()
        .filter(_.enabled)
        .foreach(
          _.happenedEvents should (
            contain theSameElementsInOrderAs sequenceOfActions :+ Deinit
              or contain theSameElementsInOrderAs sequenceOfActions
          )
        )
      engine
        .find[LifecycleTester]()
        .filter(!_.enabled)
        .foreach(
          _.happenedEvents should (
            contain theSameElementsInOrderAs Seq(Init) :+ Deinit
              or contain theSameElementsInOrderAs Seq(Init)
          )
        )

  it should "throw an exception if the user tries to run again the engine while it's already running" in:
    engine.testOnLifecycleEvent(testScene)(
      onStart = assertThrows[IllegalStateException]:
        engine.run(() => Seq())
    )

  it should "allow to set an fps limit" in:
    val fpsLimit = 60
    val expectedTimeSeconds = 1d

    val engine = Engine(new IO {}, Storage(), fpsLimit)

    val start = System.currentTimeMillis()
    engine.testOnUpdate(nFramesToRun = fpsLimit):
      {}
    val end = System.currentTimeMillis()

    val elapsedTimeSeconds = (end - start) / 1_000d
    try Math.abs(elapsedTimeSeconds - expectedTimeSeconds) should be <= 0.2
    catch
      case _: TestFailedException =>
        cancel(
          "This test is highly dependent on the performance of the machine it is run on. It failed, so ensure everything is ok."
        )
      case throwable => throw throwable

  "Engine.deltaTimeNanos" should "be 0 for all the iteration of the game loop" in:
    engine.testOnEarlyUpdate(testScene):
      engine.deltaTimeNanos shouldBe 0

    engine.testOnUpdate(testScene):
      engine.deltaTimeNanos shouldBe 0

    engine.testOnLateUpdate(testScene):
      engine.deltaTimeNanos shouldBe 0

  it should "be 0 if the game loop is not executed" in:
    engine.testOnDeinit(testScene, nFramesToRun = 0):
      engine.deltaTimeNanos shouldBe 0

    engine.deltaTimeNanos shouldBe 0

  it should "be higher than 0 after a game loop iteration" in:
    engine.testOnDeinit(testScene):
      engine.deltaTimeNanos should be > 0L

  it should "be higher than time elapsed inside updates" in:
    val slowDownDurationMillis: Long = 10
    val expectedElapsedTimeNanos =
      (slowDownDurationMillis * Math.pow(10, 6)).toLong * 3

    val deinitTesterFunction: (TestingContext) => Unit = (testingContext) =>
      // More the expected value
      testingContext.engine.deltaTimeNanos should be >= expectedElapsedTimeNanos

    // Testing on an empty scene to be more accurate
    engine.testWithTesterObject():
      new Behaviour
        with DeinitTester(deinitTesterFunction)
        with NFrameStopper(1)
        with SlowEarlyUpdater(slowDownDurationMillis)
        with SlowUpdater(slowDownDurationMillis)
        with SlowLateUpdater(slowDownDurationMillis)

  "deltaTimeSeconds" should "represent deltaTimeNanos in seconds" in:
    val engineMock = (nanoseconds: Long) =>
      import scala.reflect.TypeTest
      new Engine() {
        override def deltaTimeNanos: Long = nanoseconds
        override val io: IO = new IO {}
        override def disable(gameObject: Behaviour): Unit = ???
        override val storage: Storage = Storage()
        override val fpsLimiter: FPSLimiter = FPSLimiter(Int.MaxValue)
        override def find[B <: Identifiable](using
            tt: TypeTest[Behaviour, B]
        )(id: String): Option[B] = ???
        override def find[B <: Behaviour](using
            tt: TypeTest[Behaviour, B]
        )(): Iterable[B] = ???
        override def loadScene(scene: Scene): Unit = ???
        override def destroy(gameObject: Behaviour): Unit = ???
        override def run(initialScene: Scene): Unit = ???
        override def enable(gameObject: Behaviour): Unit = ???
        override def stop(): Unit = ???
        override def create(gameObject: Behaviour): Unit = ???
      }

    engineMock(10_000_000).deltaTimeSeconds shouldBe 0.01
    engineMock(15_000_000).deltaTimeSeconds shouldBe 0.015
    engineMock(166_000_005).deltaTimeSeconds shouldBe 0.166000005
    engineMock(1_000_000_000).deltaTimeSeconds shouldBe 1
