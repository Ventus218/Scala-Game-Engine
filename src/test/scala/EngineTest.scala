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
  private val gameObject3 = getMockB(false)
  private val gameObject4 = getMockB(false)
  private val gameObjectStop = new Behaviour with StopMockB(step = 5)

  val numSteps = 3

  private val gameObjects = Iterable(
    gameObject1,
    gameObject2,
    gameObject3,
    gameObject4,
    gameObjectStop
  )

  Engine.intantiate(
    io = new IO() {},
    storage = new StorageMock(),
    gameObjects,
    numSteps
  )

  val engine = Engine()

  "Engine" should "be instantiated only one time" in:
    assertThrows[IllegalStateException](
      Engine.intantiate(
        io = new IO() {},
        storage = new StorageMock(),
        null,
        1
      )
    ) 
  
  it should "be instantiated before getting the instance" in:
    assertThrows[IllegalStateException](
      Engine.engine = null,
      Engine()
    )

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
