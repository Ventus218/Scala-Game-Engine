import scala.reflect.TypeTest

private class StorageMock() extends Storage:
  def set[T](key: String, value: T): Unit = ???
  def get[T](using tt: TypeTest[Any, T])(key: String): T = ???
  def getOption[T](using tt: TypeTest[Any, T])(key: String): Option[T] = ???
  def unset(key: String): Unit = ???

private case class GameObjectMock() extends Behaviour(enabled = true)
