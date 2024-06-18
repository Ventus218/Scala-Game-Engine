private case class StorageMock() extends Storage:
  def set[T](key: String, value: T): Unit = ???
  def get[T](key: String): T = ???
  def getOption[T](key: String): Option[T] = ???
  def unset(key: String): Unit = ???
  
import Behaviours.Identifiable
private case class GameObjectMock() extends Behaviour(enabled = true)
