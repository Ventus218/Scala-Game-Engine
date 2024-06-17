import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class ContextTests extends AnyFlatSpec:
  val engine =
    Engine(new IO {}, StorageMock(), null, 0, deltaTimeNanos = 1_000_000_000L)
  val gameObject = GameObjectMock()
  def context: Context = Context(engine, gameObject)

  "Context" should "provide a direct reference to the engine IO" in:
    context.io shouldBe engine.io

  it should "provide a direct reference to the engine Storage" in:
    context.storage shouldBe engine.storage

  it should "provide a direct reference to the engine deltaTimeNanos" in:
    context.deltaTimeNanos shouldBe engine.deltaTimeNanos

  it should "provide utility for converting deltaTimeNanos into seconds" in:
    context.deltaTimeSeconds shouldBe 1

    val engine2 =
      Engine(new IO {}, StorageMock(), null, 0, deltaTimeNanos = 500_000_000L)
    Context(engine2, gameObject).deltaTimeSeconds shouldBe 0.5