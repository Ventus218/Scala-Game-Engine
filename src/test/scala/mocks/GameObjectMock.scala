private case class GameObjectMock(
    id: Option[String] = Option.empty,
    enabled: Boolean = true,
    behaviour: Behaviour = new Behaviour {}
) extends GameObject[Behaviour]

private case class StorageMock() extends Storage:
  def set[T](key: String, value: T): Unit = ???
  def get[T](key: String): T = ???
  def getOption[T](key: String): Option[T] = ???
  def unset(key: String): Unit = ???