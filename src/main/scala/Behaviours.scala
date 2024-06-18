object Behaviours:
  trait Identifiable(val id: String) extends Behaviour
  trait Positionable(var x: Double = 0, var y: Double = 0) extends Behaviour
