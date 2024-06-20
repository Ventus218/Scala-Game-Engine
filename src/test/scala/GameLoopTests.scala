import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import TestUtils.*
import org.scalatest.BeforeAndAfterEach
import LifecycleTester.*
import LifecycleEvent.*

class GameLoopTests extends AnyFlatSpec with BeforeAndAfterEach:
  private def getSequenceOfActions(): Seq[LifecycleEvent] =
    Seq(Init, Enable, Start)

  private def getUpdatesSequenceOfActions(): Seq[LifecycleEvent] =
    Seq(EarlyUpdate, Update, LateUpdate)

  private val deltaTime = new Behaviour with DeltaTimeMockB

  val numSteps = 3

  private def testScene: Scene = () =>
    Iterable(
      new Behaviour with LifecycleTester,
      new Behaviour with LifecycleTester,
      new Behaviour(enabled = false) with LifecycleTester,
      new Behaviour(enabled = false) with LifecycleTester,
      deltaTime
    )

  val engine = Engine(
    io = new IO() {},
    storage = new StorageMock()
  )

  override protected def beforeEach(): Unit =
    deltaTime.toStopBeforeUpdates = false
    deltaTime.toStopUpdates = false

  "Engine" should "start with delta time nanos at 0" in:
    engine.deltaTimeNanos shouldBe 0

  it should "call all methods on enabled gameObjects and just init and deinit on disabled gameObjects" in:
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

  it should "have delta time nanos at 0 before update" in:
    engine.testOnUpdate(testScene):
      {}
    deltaTime.dt shouldBe 0

  it should "have delta time at 0 if the loop of updates is not executed" in:
    deltaTime.toStopBeforeUpdates = true
    engine.testOnUpdate(testScene):
      {}
    engine.deltaTimeNanos shouldBe 0

  it should "have delta time nanos higher than 0 after a run with updates" in:
    engine.testOnUpdate(testScene):
      {}
    engine.deltaTimeNanos > 0 shouldBe true

  it should "have delta time nanos higher than time stopped inside updates" in:
    deltaTime.toStopUpdates = true
    deltaTime.secondsToStop = 0.01

    engine.testOnDeinit(testScene, nFramesToRun = 1):
      {}

    engine.deltaTimeNanos >= (deltaTime.secondsToStop * 3 * deltaTime.secondsToStop) * Math
      .pow(10, 9) shouldBe true

    deltaTime.secondsToStop = 0.02

    engine.testOnDeinit(testScene, nFramesToRun = 1):
      {}
    engine.deltaTimeNanos >= (deltaTime.secondsToStop * 3 * deltaTime.secondsToStop) * Math
      .pow(10, 9) shouldBe true

  private trait DeltaTimeMockB extends LifecycleTester:
    var dt: Long = 0
    var toStopBeforeUpdates: Boolean = false
    var toStopUpdates: Boolean = false
    var secondsToStop: Double = 0

    override def onInit: Engine => Unit =
      engine =>
        super.onInit(engine)
        dt = dt + engine.deltaTimeNanos

    override def onEnabled: Engine => Unit =
      engine =>
        super.onEnabled(engine)
        dt = dt + engine.deltaTimeNanos

    override def onStart: Engine => Unit =
      engine =>
        super.onStart(engine)
        dt = dt + engine.deltaTimeNanos
        if toStopBeforeUpdates then engine.stop()

    override def onEarlyUpdate: Engine => Unit =
      engine =>
        super.onEarlyUpdate(engine)
        if toStopUpdates then Thread.sleep((secondsToStop * 1000).toInt)

    override def onUpdate: Engine => Unit =
      engine =>
        super.onUpdate(engine)
        if toStopUpdates then Thread.sleep((secondsToStop * 1000).toInt)

    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        if toStopUpdates then Thread.sleep((secondsToStop * 1000).toInt)
