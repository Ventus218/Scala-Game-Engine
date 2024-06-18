object Behaviours:
  trait Identifiable(val id: String) extends Behaviour
  trait PositionB(var x: Double = 0, var y: Double = 0) extends Behaviour
