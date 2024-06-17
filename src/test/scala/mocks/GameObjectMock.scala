private case class GameObjectMock(
    id: Option[String] = Option.empty,
    enabled: Boolean = true,
    behaviour: Behaviour = new Behaviour {}
) extends GameObject[Behaviour]