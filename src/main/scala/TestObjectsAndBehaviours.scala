trait InputB extends Behaviour:
  var inputs: List[Int] = List()
  override def onUpdate: Context => Unit = (context) =>
    super.onUpdate(context)
    inputs = List(inputs.lastOption.getOrElse(0) + 1)

trait TranformB(var x: Double = 0, var y: Double = 0) extends Behaviour

class PallaB(val rimbalzo: Boolean = true)
    extends Behaviour
    with InputB
    with TranformB:

  def hit(): Unit = println("I was hit")

  override def onUpdate: Context => Unit = (context) =>
    super.onUpdate(context)
    // TODO: far notare nella relazione come questo approccio sia molto più comodo
    // e semplice rispetto a cercare un component di tipo Transform nel proprio oggetto
    // e utilizzarlo.
    // Inoltre questo è controllato dal compilatore.
    x = x + 1
    // println(s"transform x ${this.x}")
    println(context.deltaTimeNanos)
    inputs.foreach(println(_))

class PallaGameObject(r: Boolean, var enabled: Boolean)
    extends GameObject[PallaB]:
  val id = Some("1")
  override val behaviour: PallaB = PallaB(rimbalzo = r)
  export behaviour.*
