trait GameObject[B <: Behaviour]:
  val id: Option[String]
  val behaviour: B
  var enabled: Boolean
