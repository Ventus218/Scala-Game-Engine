import scala.reflect.TypeTest
import Behaviours.Identifiable

class EngineMock(val io: IO, val storage: Storage) extends Engine:

  override def disable(gameObject: Behaviour): Unit = ???

  override def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[B] = ???

  override def find[B <: Identifiable](using tt: TypeTest[Behaviour, B])(
      id: String
  ): Option[B] = ???

  override def destroy(gameObject: Behaviour): Unit = ???

  override def enable(gameObject: Behaviour): Unit = ???

  override def loadScene(scene: Scene): Unit = ???

  override def run(initialScene: Scene): Unit = ???

  override def deltaTimeNanos: Long = ???

  override def stop(): Unit = ???

  override def create(gameObject: Behaviour): Unit = ???
