import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.PositionB

class EngineTest extends AnyFlatSpec:
  private val gameObject1 = new GameObjectMockWithBehaviour(0, 0, false)
  private val gameObject2 = new GameObjectMockWithBehaviour(3, 10, false)

  private val gameObjects = Iterable(
    gameObject1,
    gameObject2
  )

  def getEngine(gameObjects: Iterable[GameObject[?]]): Engine = Engine(io = new IO() {}, storage = new StorageMock(), gameObjects)

  it should "init all gameObjects behaviour when it is started" in:
    getEngine(gameObjects).run()

    gameObject1.behaviour.x shouldBe -10.5
    gameObject1.behaviour.y shouldBe 5

    gameObject2.behaviour.x shouldBe -10.5
    gameObject2.behaviour.y shouldBe 15

  it should "call onEnabled on enabledGameObjects after init" in:
    val gameObject3 = new GameObjectMockWithBehaviour(0, 0)
    val gameObject4 = new GameObjectMockWithBehaviour(3, 10)

    gameObject1.behaviour.x = 0
    gameObject1.behaviour.y = 0

    getEngine(gameObjects ++ Iterable(gameObject3) ++ Iterable(gameObject4)).run()

    gameObject1.behaviour.x shouldBe -10.5
    gameObject1.behaviour.y shouldBe 5

    gameObject3.behaviour.x shouldBe -10.5 * 2
    gameObject3.behaviour.y shouldBe 5 * 3

    gameObject4.behaviour.x shouldBe -10.5 * 2
    gameObject4.behaviour.y shouldBe 15 * 3
  
  private class GameObjectMockWithBehaviour(x: Double, y: Double, var enabled: Boolean = true) extends GameObject[MockB]:
    override val id: Option[String] = Option.empty
    override val behaviour: MockB = new Behaviour() with MockB() with PositionB(x = x, y = y)

  private trait MockB extends PositionB:
    override def onInit: Context => Unit =
      context =>
        x = -10.5
        y = y + 5

    override def onEnabled: Context => Unit =
      context =>
        x = x * 2
        y = y * 3

  private class StorageMock extends Storage:
    override def set[T](key: String, value: T): Unit = ???

    override def get[T](key: String): T = ???

    override def getOption[T](key: String): Option[T] = ???

    override def unset(key: String): Unit = ???
