object Behaviours:
  /** Add an id to a Behaviour so that the Behaviour can be found by that id
    *
    * @param id id of the Behaviour
    */
  trait Identifiable(val id: String) extends Behaviour

  /** Add 2D position to a Behaviour
    *
    * @param x position of the Behaviour on the X axis
    * @param y position of the Behaviour on the Y axis
    */
  trait Positionable(var x: Double = 0, var y: Double = 0) extends Behaviour

  /** Add 2D dimension to a Behaviour.
    * Does not work with negative values.
    *
    * @param w width of the Behaviour. If w < 0 when created, width will be 0
    * @param h height of the Behaviour. If h < 0 when created, height will be 0
    */
  trait Dimensionable(private var w: Double, private var h: Double)
      extends Behaviour:

    def width: Double = if w >=0 then w else 0
    def height: Double = if h >= 0 then h else 0

    def width_=(w: Double): Unit =
      if w >= 0 then this.w = w

    def height_=(h: Double): Unit = 
      if h >= 0 then this.h = h

  trait Collider(width: Double = -1, height: Double = -1) extends Dimensionable with Positionable:
    def cWidth: Double = if width < 0 then super.width else width
    def cHeight: Double = if height < 0 then super.height else height
