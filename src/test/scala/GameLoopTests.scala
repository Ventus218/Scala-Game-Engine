import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
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
  private val gameObjectStop = new Behaviour with StopMockB(step = numSteps - 1)
  private val deltaTime = new Behaviour with DeltaTimeMockB

  val numSteps = 3

  private val gameObjects = Iterable(
    gameObject1,
    gameObject2,
    gameObject3,
    gameObject4,
    gameObjectStop,
    deltaTime
  )

  val engine = Engine(
    io = new IO() {},
    storage = new StorageMock(),
    gameObjects
  )

  override protected def beforeEach(): Unit = 
    gameObjectStop.step = numSteps - 1
    deltaTime.toStopBeforeUpdates = false
    deltaTime.toStopUpdates = false
    gameObjectStop.state = "Active"

  "Engine" should "start with delta time nanos at 0" in:
    engine.deltaTimeNanos shouldBe 0

  it should "call all methods on enabled gameObjects and just init and deinit on disabled gameObjects" in:
    engine.run()

    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 until numSteps do
      sequenceOfActions = sequenceOfActions ++ getUpdatesSequenceOfActions()

    gameObjects.filter(_.enabled).foreach(gameObject =>
      gameObject.list shouldBe sequenceOfActions :+ "deinit"
    )

    gameObjects.filter(!_.enabled).foreach(gameObject =>
      gameObject.list shouldBe Seq("init", "deinit")
    )

  it should "stop when engine.stop() is called" in:
    val stepToStop = 1
    gameObjectStop.step = stepToStop
    gameObjectStop.state shouldBe "Active"
    
    engine.run()

    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 to stepToStop do
      sequenceOfActions =
        sequenceOfActions ++ getUpdatesSequenceOfActions()

    gameObjects.filter(_.enabled).foreach(gameObject =>
      gameObject.list shouldBe sequenceOfActions :+ "deinit"
    )

    gameObjectStop.state shouldBe "Stopped"
  
  it should "do the loop again if called run after being stopped" in:
    engine.stop()
    engine.run()

    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 until numSteps do
      sequenceOfActions = sequenceOfActions ++ getUpdatesSequenceOfActions()

    gameObjects.filter(_.enabled).foreach(gameObject =>
      gameObject.list shouldBe sequenceOfActions :+ "deinit"
    )

    gameObjects.filter(!_.enabled).foreach(gameObject =>
      gameObject.list shouldBe Seq("init", "deinit")
    )

  it should "have delta time nanos at 0 before update" in:
    engine.run()
    deltaTime.dt shouldBe 0

  it should "have delta time at 0 if the loop of updates is not executed" in:
    deltaTime.toStopBeforeUpdates = true
    engine.run()
    engine.deltaTimeNanos shouldBe 0

  it should "have delta time nanos higher than 0 after a run with updates" in:
    engine.run()
    engine.deltaTimeNanos > 0 shouldBe true

  it should "have delta time nanos higher than time stopped inside updates" in:
    var stepToStop = 0
    gameObjectStop.step = stepToStop
    deltaTime.toStopUpdates = true
    deltaTime.secondsToStop = 0.01

    engine.run()
    engine.deltaTimeNanos >= (deltaTime.secondsToStop * 3 * deltaTime.secondsToStop) * Math.pow(10, 9) shouldBe true

    deltaTime.secondsToStop = 0.02

    engine.run()
    engine.deltaTimeNanos >= (deltaTime.secondsToStop * 3 * deltaTime.secondsToStop) * Math.pow(10, 9) shouldBe true

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
        if toStopUpdates then Thread.sleep((secondsToStop * 1000). toInt)

    override def onUpdate: Engine => Unit =
      engine =>
        super.onUpdate(engine)
        if toStopUpdates then Thread.sleep((secondsToStop * 1000). toInt)

    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        if toStopUpdates then Thread.sleep((secondsToStop * 1000). toInt)

  private trait StopMockB(var step: Int) extends MockB:
    var state: String = "Active"
    var counter: Int = -1

    override def onInit: Engine => Unit = 
      engine =>
        super.onInit(engine)
        counter = 0

    override def onUpdate: Engine => Unit =
      engine =>
        super.onUpdate(engine)
        if counter == step then
          engine.stop()
          state = "Stopped"
        else
          counter = counter + 1

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
