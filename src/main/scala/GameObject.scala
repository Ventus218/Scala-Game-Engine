trait GameObject[B <: Behaviour]:
  val id: Option[String]
  val behaviour: B
