import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.PositionB

class EngineTest extends AnyFlatSpec:
  private val gameObject1 = new GameObjectMockWithBehaviour(0, 0, false)
  private val gameObject2 = new GameObjectMockWithBehaviour(0, 0, false)

  private val gameObjects = Iterable(
    gameObject1,
    gameObject2
  )

  def getEngine(gameObjects: Iterable[GameObject[?]]): Engine = Engine(io = new IO() {}, storage = new StorageMock(), gameObjects)

  it should "init all gameObjects behaviour when it is started" in:
    getEngine(gameObjects).run()

    gameObjects.foreach( gameObject =>
      gameObject.behaviour.list shouldBe Seq("init")
    )

  it should "call onEnabled on enabledGameObjects after init" in:
    val gameObject3 = new GameObjectMockWithBehaviour(0, 0)
    val gameObject4 = new GameObjectMockWithBehaviour(0, 0)

    getEngine(gameObjects ++ Iterable(gameObject3) ++ Iterable(gameObject4)).run()

    gameObjects.foreach(gameObject =>
      gameObject.behaviour.list shouldBe Seq("init")
    )

    gameObject3.behaviour.list shouldBe Seq("init", "enable")
    gameObject4.behaviour.list shouldBe Seq("init", "enable")

  it should "call onStart on enabledGameObjects after enable" in:
    ()
  
  private class GameObjectMockWithBehaviour(x: Double, y: Double, var enabled: Boolean = true) extends GameObject[MockB]:
    override val id: Option[String] = Option.empty
    override val behaviour: MockB = new Behaviour() with MockB()


  private trait MockB extends Behaviour:
    var list: Seq[String] = Seq()
    override def onInit: Context => Unit =
      context =>
        list = list :+ "init"

    override def onEnabled: Context => Unit =
      context =>
        list = list :+ "enable"

    override def onStart: Context => Unit =
      context =>
        list = list :+ "start"

  private class StorageMock extends Storage:
    override def set[T](key: String, value: T): Unit = ???

    override def get[T](key: String): T = ???

    override def getOption[T](key: String): Option[T] = ???

    override def unset(key: String): Unit = ???
