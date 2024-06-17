import org.scalatest.flatspec.AnyFlatSpec
import scala.reflect.TypeTest

class ContextTests extends AnyFlatSpec:
  "Context" should "have a default implementation" in:
    val engine = EngineMock(new IO {}, StorageMock())
    val gameObject = GameObjectMock(true)
    val _ = Context(engine, gameObject)

// Mocks
import scala.annotation.targetName
private case class EngineMock(io: IO, storage: Storage) extends Engine {
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
  def deltaTimeNanos: Long = ???
}

private case class StorageMock() extends Storage:
  def set[T](key: String, value: T): Unit = ???
  def get[T](key: String): T = ???
  def getOption[T](key: String): Option[T] = ???
  def unset(key: String): Unit = ???

private case class GameObjectMock(enabled: Boolean)
    extends GameObject[Behaviour]:
  val id: Option[String] = Option.empty
  val behaviour: Behaviour = new Behaviour {}
