import scala.annotation.targetName

trait Engine:
  val io: IO
  val storage: Storage
  def loadScene(scene: Scene): Unit
  def enable(gameObject: GameObject[?]): Unit
  def disable(gameObject: GameObject[?]): Unit
  def create(gameObject: GameObject[?]): Unit
  def destroy(gameObject: GameObject[?]): Unit
  def run(): Unit
  def stop(): Unit
  def deltaTimeNanos: Long

  import scala.reflect.TypeTest

  @targetName("find_object")
  def find[G <: GameObject[?]](using
      tt: TypeTest[GameObject[?], G]
  )(): Iterable[G]

  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[GameObject[B]]

  @targetName("find_object_by_id")
  def findById[G <: GameObject[?]](using
      tt: TypeTest[GameObject[?], G]
  )(id: String): Option[G]

  def findById[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(id: String): Option[GameObject[B]]
