import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import TestUtils.*
import org.scalatest.BeforeAndAfterEach
import LifecycleTester.*
import LifecycleEvent.*
import TestUtils.Testers.*

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
    storage = new StorageMock()
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
      // Approximately between the expected value and it's double
      testingContext.engine.deltaTimeNanos should be >= expectedElapsedTimeNanos

    // Testing on an empty scene to be more accurate
    engine.testWithTesterObject():
      new Behaviour
        with DeinitTester(deinitTesterFunction)
        with NFrameStopper(1)
        with SlowEarlyUpdater(slowDownDurationMillis)
        with SlowUpdater(slowDownDurationMillis)
        with SlowLateUpdater(slowDownDurationMillis)
