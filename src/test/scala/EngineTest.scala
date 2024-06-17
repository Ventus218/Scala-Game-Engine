import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.PositionB

class EngineTest extends AnyFlatSpec:
  "Engine" should "be created with IO and Storage" in:
    Engine(io = new IO() {}, storage = new StorageMock(), Iterable())

  it should "init all gameObjects behaviour when it is started" in:
    val gameObject1 = new GameObjectMockWithBehaviour(0, 0)

    val gameObject2 = new GameObjectMockWithBehaviour(3, 10)

    val gameObjects = Iterable(
      gameObject1,
      gameObject2
    )

    Engine(
      io = new IO() {},
      storage = new StorageMock(),
      gameObjects = gameObjects
    ).run()

    gameObject1.behaviour.x shouldBe -10.5
    gameObject1.behaviour.y shouldBe 5

    gameObject2.behaviour.x shouldBe -10.5
    gameObject2.behaviour.y shouldBe 15

  it should "call onEnabled on enabledGameObjects after init" in:
    ()
  
  private class GameObjectMockWithBehaviour(x: Double, y: Double, var enabled: Boolean = true) extends GameObject[MockB]:
    override val id: Option[String] = Option.empty
    override val behaviour: MockB = new Behaviour() with MockB() with PositionB(x = x, y = y)

  private trait MockB extends PositionB:
    override def onInit: Context => Unit =
        context =>
            x = -10.5
            y = y + 5

  private class StorageMock extends Storage:
    override def set[T](key: String, value: T): Unit = ???

    override def get[T](key: String): T = ???

    override def getOption[T](key: String): Option[T] = ???

    override def unset(key: String): Unit = ???
