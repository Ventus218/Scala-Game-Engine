import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.PositionB

class EngineTest extends AnyFlatSpec:
  private val gameObject1 = new GameObjectMockWithBehaviour()
  private val gameObject2 = new GameObjectMockWithBehaviour()

  val numSteps = 3

  private val gameObjects = Iterable(
    gameObject1,
    gameObject2
  )

  def getEngine(gameObjects: Iterable[GameObject[?]]): Engine = Engine(io = new IO() {}, storage = new StorageMock(), gameObjects, numSteps, 0)

  it should "init all disabled gameObjects behaviour when it is started and deinit them at the end" in:
    val gameObject1 = new GameObjectMockWithBehaviour(false)
    val gameObject2 = new GameObjectMockWithBehaviour(false)

    val gameObjects = Iterable(gameObject1, gameObject2)
    getEngine(gameObjects).run()

    gameObjects.foreach( gameObject =>
      gameObject.behaviour.list shouldBe Seq("init", "deinit")
    )

  it should "call all methods once on enabled GameObjects" in:
    val gameObject3 = new GameObjectMockWithBehaviour(false)

    getEngine(gameObjects ++ Iterable(gameObject3)).run()

    var sequenceOfActions = Seq("init", "enable", "start")

    for i <- 0 until numSteps do
      sequenceOfActions = sequenceOfActions ++ Seq("earlyUpdate", "update", "lateUpdate") 

    gameObjects.foreach(gameObject =>
      gameObject.behaviour.list shouldBe sequenceOfActions :+ "deinit"
    )

    gameObject3.behaviour.list shouldBe Seq("init", "deinit")

  it should "stop when engine.stop() is called and context have references to engine and gameObject" in:
    val stepToStop = 1
    val gameObject = new GameObject[StopMockB] {
      override val id: Option[String] = Option("Stopped")
      override val behaviour: StopMockB = new Behaviour with StopMockB(step = stepToStop)
      var enabled: Boolean = true
    }

    gameObject.behaviour.id shouldBe Option.empty

    getEngine(gameObjects ++ Iterable(gameObject)).run()

    var sequenceOfActions = Seq("init", "enable", "start")

    for i <- 0 to stepToStop do
      sequenceOfActions = sequenceOfActions ++ Seq("earlyUpdate", "update", "lateUpdate")

    gameObjects.foreach(gameObject =>
      gameObject.behaviour.list shouldBe sequenceOfActions :+ "deinit"
    )

    gameObject.behaviour.id shouldBe Option("Stopped")
  
  private class GameObjectMockWithBehaviour(var enabled: Boolean = true) extends GameObject[MockB]:
    override val id: Option[String] = Option.empty
    override val behaviour: MockB = new Behaviour() with MockB()

  private trait StopMockB(step: Int) extends Behaviour:
    var id: Option[String] = Option.empty
    override def onUpdate: Context => Unit = 
      context => 
        if context.engine.getCurrentNumSteps() == step then
          context.engine.stop()
          id = context.gameObject.id

  private trait MockB extends Behaviour:
    var list: Seq[String] = Seq()
    override def onInit: Context => Unit =
      context =>
        list = Seq() :+ "init"

    override def onEnabled: Context => Unit =
      context =>
        list = list :+ "enable"

    override def onStart: Context => Unit =
      context =>
        list = list :+ "start"

    override def onEarlyUpdate: Context => Unit =
      context =>
        list = list :+ "earlyUpdate"

    override def onUpdate: Context => Unit =
      context =>
        list = list :+ "update"

    override def onLateUpdate: Context => Unit =
      context =>
        list = list :+ "lateUpdate"

    override def onDeinit: Context => Unit =
      context =>
        list = list :+ "deinit"
