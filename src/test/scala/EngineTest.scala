import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*

class EngineTest extends AnyFlatSpec:
  private def getMockB(enable: Boolean = true): MockB = new Behaviour(enable)
    with MockB

  private def getSequenceOfActions(): Seq[String] =
    Seq("init", "enable", "start")

  private def getUpdatesSequenceOfActions(): Seq[String] =
    Seq("earlyUpdate", "update", "lateUpdate")

  private val gameObject1 = getMockB()
  private val gameObject2 = getMockB()

  val numSteps = 3

  private val gameObjects = Iterable(
    gameObject1,
    gameObject2
  )

  def getEngine(gameObjects: Iterable[Behaviour]): Engine = Engine(
    io = new IO() {},
    storage = new StorageMock(),
    gameObjects,
    numSteps,
    0
  )

  it should "init all disabled gameObjects behaviour when it is started and deinit them at the end" in:
    val gameObject1 = getMockB(false)
    val gameObject2 = getMockB(false)

    val gameObjects = Iterable(gameObject1, gameObject2)

    getEngine(gameObjects).run()

    gameObjects.foreach(gameObject =>
      gameObject.list shouldBe Seq("init", "deinit")
    )

  it should "call all methods once on enabled GameObjects" in:
    val gameObject3 = getMockB(false)

    getEngine(gameObjects ++ Iterable(gameObject3)).run()

    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 until numSteps do
      sequenceOfActions = sequenceOfActions ++ getUpdatesSequenceOfActions()

    gameObjects.foreach(gameObject =>
      gameObject.list shouldBe sequenceOfActions :+ "deinit"
    )

    gameObject3.list shouldBe Seq("init", "deinit")

  it should "stop when engine.stop() is called and context have references to engine and gameObject" in:
    val stepToStop = 1
    val gameObject = new Behaviour with StopMockB(step = stepToStop)

    gameObject.state shouldBe "Active"

    getEngine(gameObjects ++ Iterable(gameObject)).run()

    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 to stepToStop do
      sequenceOfActions =
        sequenceOfActions ++ getUpdatesSequenceOfActions()

    gameObjects.foreach(gameObject =>
      gameObject.list shouldBe sequenceOfActions :+ "deinit"
    )

    gameObject.state shouldBe "Stopped"

  private trait StopMockB(step: Int) extends Behaviour:
    var state: String = "Active"
    override def onUpdate: Engine => Unit =
      engine =>
        if engine.getCurrentNumSteps() == step then
          engine.stop()
          state = "Stopped"

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
