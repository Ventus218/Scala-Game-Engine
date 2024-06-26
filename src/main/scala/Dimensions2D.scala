object Dimensions2D:
  /** Add 2D position to a Behaviour
    *
    * @param x
    *   position of the Behaviour on the X axis
    * @param y
    *   position of the Behaviour on the Y axis
    */
  trait Positionable(var x: Double = 0, var y: Double = 0) extends Behaviour

  /** Gives to other behaviours a single scale value for their dimension.
    *
    * @param x
    */
  trait SingleScalable(private var x: Double = 1) extends Behaviour:
    def scale = if x > 0 then x else 1
    def scale_=(scale: Double) = if scale > 0 then x = scale

  /** Gives to other behaviours a scale on X and Y of their dimension.
    *
    * @param x
    *   multiplier of the width, must be greater than 0.
    * @param y
    *   multiplier of the height, must be greater than 0.
    */
  trait Scalable(private var x: Double = 1, private var y: Double = 1)
      extends Behaviour:

    private val singleScalable: SingleScalable = new Behaviour
      with SingleScalable(x)

    def scaleY: Double = if y > 0 then y else 1
    def scaleY_=(h: Double): Unit =
      if h > 0 then this.y = h

    export singleScalable.{scale as scaleX, scale_= as scaleX_=}
