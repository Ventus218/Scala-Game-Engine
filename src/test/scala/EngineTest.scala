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

    gameObject1.behaviour.x shouldBe 1
    gameObject1.behaviour.y shouldBe -1

    gameObject2.behaviour.x shouldBe 1
    gameObject2.behaviour.y shouldBe -1

  it should "call onEnabled on enabledGameObjects after init" in:
    val gameObject3 = new GameObjectMockWithBehaviour(0, 0)
    val gameObject4 = new GameObjectMockWithBehaviour(0, 0)

    getEngine(gameObjects ++ Iterable(gameObject3) ++ Iterable(gameObject4)).run()

    gameObject1.behaviour.x shouldBe 1
    gameObject1.behaviour.y shouldBe -1

    gameObject3.behaviour.x shouldBe 2
    gameObject3.behaviour.y shouldBe -2

    gameObject4.behaviour.x shouldBe 2
    gameObject4.behaviour.y shouldBe -2
  
  private class GameObjectMockWithBehaviour(x: Double, y: Double, var enabled: Boolean = true) extends GameObject[OnInitOnEnabledMockB]:
    override val id: Option[String] = Option.empty
    override val behaviour: OnInitOnEnabledMockB = new Behaviour() with OnInitOnEnabledMockB() with PositionB(x = x, y = y)

  private trait OnInitOnEnabledMockB extends PositionB:
    override def onInit: Context => Unit =
      context =>
        x = 1
        y = -1

    override def onEnabled: Context => Unit =
      context =>
        x = 2
        y = -2

  private trait OnStartMockB extends PositionB:
    override def onStart: Context => Unit =
      context =>
        x = 3
        y = -3

  private class StorageMock extends Storage:
    override def set[T](key: String, value: T): Unit = ???

    override def get[T](key: String): T = ???

    override def getOption[T](key: String): Option[T] = ???

    override def unset(key: String): Unit = ???
