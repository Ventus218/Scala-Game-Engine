import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class ContextTests extends AnyFlatSpec:
  val engine =
    EngineMock(new IO {}, StorageMock(), deltaTimeNanosInit = 1_000_000_000L)
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
      EngineMock(new IO {}, StorageMock(), deltaTimeNanosInit = 500_000_000L)
    Context(engine2, gameObject).deltaTimeSeconds shouldBe 0.5

// Mocks
import scala.annotation.targetName
import scala.reflect.TypeTest
private case class EngineMock(
    io: IO,
    storage: Storage,
    deltaTimeNanosInit: Long
) extends Engine {
  def stop(): Unit = ???
  def enable(gameObject: GameObject[?]): Unit = ???
  @targetName("find_object")
  def find[G <: GameObject[?]](using
      tt: TypeTest[GameObject[?], G]
  )(): Iterable[G] = ???
  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[GameObject[B]] = ???
  @targetName("find_object_by_id")
  def findById[G <: GameObject[?]](using
      tt: TypeTest[GameObject[?], G]
  )(id: String): Option[G] = ???
  def findById[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(id: String): Option[GameObject[B]] = ???
  def run(): Unit = ???
  def destroy(gameObject: GameObject[?]): Unit = ???
  def disable(gameObject: GameObject[?]): Unit = ???
  def loadScene(scene: Scene): Unit = ???
  def create(gameObject: GameObject[?]): Unit = ???
  def deltaTimeNanos: Long = deltaTimeNanosInit
}

private case class StorageMock() extends Storage:
  def set[T](key: String, value: T): Unit = ???
  def getOption[T](using
      tt: TypeTest[Any, T]
  )(key: String): Option[T] = ???
  def unset(key: String): Unit = ???
