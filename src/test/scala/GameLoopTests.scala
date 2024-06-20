import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import TestUtils.*
import org.scalatest.BeforeAndAfterEach

class GameLoopTests extends AnyFlatSpec with BeforeAndAfterEach:
  private def getMockB(enable: Boolean = true): MockB = new Behaviour(enable)
    with MockB

  private def getSequenceOfActions(): Seq[String] =
    Seq("init", "enable", "start")

  private def getUpdatesSequenceOfActions(): Seq[String] =
    Seq("earlyUpdate", "update", "lateUpdate")

  private val gameObject1 = getMockB()
  private val gameObject2 = getMockB()
  private val gameObject3 = getMockB(false)
  private val gameObject4 = getMockB(false)
  private val deltaTime = new Behaviour with DeltaTimeMockB

  val numSteps = 3

  private def testScene: Scene = () =>
    Iterable(
      getMockB(),
      getMockB(),
      getMockB(false),
      getMockB(false),
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

    // TODO: works only because of the order in which objects are kept inside engine
    engine.testOnDeinit(testScene, nFramesToRun = numSteps):
      engine
        .find[MockB]()
        .filter(_.enabled)
        .foreach(
          _.list should contain theSameElementsInOrderAs sequenceOfActions :+ "deinit"
        )
      engine
        .find[MockB]()
        .filter(!_.enabled)
        .foreach(
          _.list should contain theSameElementsInOrderAs Seq("init", "deinit")
        )

  it should "stop when engine.stop() is called" in:
    val oneFrameScene = testScene.joined: () =>
      Seq(new Behaviour with NFrameStopper(1))

    var sequenceOfActions =
      getSequenceOfActions() ++ getUpdatesSequenceOfActions()

    // TODO: works only because of the order in which objects are kept inside engine
    // The idea is that the test should run the engine for 5 frames but since a NFrameStopper(1) has been added it should stop only after one frame
    engine.testOnDeinit(oneFrameScene, nFramesToRun = 5):
      engine
        .find[MockB]()
        .filter(_.enabled)
        .foreach(
          _.list should contain theSameElementsInOrderAs sequenceOfActions :+ "deinit"
        )

  it should "do the loop again if called run after being stopped" in:
    engine.testOnUpdate(testScene):
      {}
    engine.stop()

    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 until numSteps do
      sequenceOfActions = sequenceOfActions ++ getUpdatesSequenceOfActions()

    // TODO: works only because of the order in which objects are kept inside engine
    engine.testOnDeinit(testScene, nFramesToRun = numSteps):
      engine
        .find[MockB]()
        .filter(_.enabled)
        .foreach(
          _.list should contain theSameElementsInOrderAs sequenceOfActions :+ "deinit"
        )
      engine
        .find[MockB]()
        .filter(!_.enabled)
        .foreach(
          _.list should contain theSameElementsInOrderAs Seq("init", "deinit")
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

  private trait DeltaTimeMockB extends MockB:
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

  private trait MockB extends Behaviour:
    var list: Seq[String] = Seq()
    override def onInit: Engine => Unit =
      _ => list = Seq() :+ "init"

    override def onEnabled: Engine => Unit =
      _ => list = list :+ "enable"

    override def onStart: Engine => Unit =
      _ => list = list :+ "start"

    override def onEarlyUpdate: Engine => Unit =
      _ => list = list :+ "earlyUpdate"

    override def onUpdate: Engine => Unit =
      _ => list = list :+ "update"

    override def onLateUpdate: Engine => Unit =
      _ => list = list :+ "lateUpdate"

    override def onDeinit: Engine => Unit =
      _ => list = list :+ "deinit"
