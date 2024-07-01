object Dimensions2D:
  /** Add 2D position to a Behaviour
    *
    * @param x
    *   position of the Behaviour on the X axis
    * @param y
    *   position of the Behaviour on the Y axis
    */
  trait Positionable(var x: Double = 0, var y: Double = 0) extends Behaviour

  /** Gives the capability to follow another Positionable. The position of this
    * Behaviour is initialized as the follower position in the init and updated
    * on the lateUpdate, according to an offset.
    *
    * @param followed
    * @param offset
    */
  trait PositionFollower(
      followed: Positionable,
      var offset: (Double, Double) = (0, 0)
  ) extends Positionable:
    override def onInit: Engine => Unit =
      engine =>
        super.onInit(engine)
        x = followed.x + offset._1
        y = followed.y + offset._2

    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        x = followed.x + offset._1
        y = followed.y + offset._2

  /** Tells if a generic T scale is valid
    */
  trait IsValid[T]:
    /** Returns true if the scale is valid, false otherwise
      *
      * @param scale
      */
    def apply(scale: T): Boolean

  given IsValid[Double] with
    override def apply(scale: Double): Boolean = scale > 0

  given IsValid[(Double, Double)] with
    override def apply(scale: (Double, Double)): Boolean =
      scale._1 > 0 && scale._2 > 0

  /** Add a scale to a behaviour in order to change its dimension. T is the type
    * of the dimension to scale (e.g. if it's radius, it has one dimension so it
    * will use a Double as T, if two dimension are needed, it is possible to use
    * (Double, Double), ecc.)
    *
    * @param _scale
    *   init value of the scale of the dimension, must be valid or otherwise
    *   will throw an IllegalArgumentException
    * @param isValid
    *   tell the trait how to decide if the value of the scale is valid
    */
  trait Scalable[T](private var _scale: T)(using isValid: IsValid[T])
      extends Behaviour:
    require(isValid(_scale))

    def scale: T = _scale
    def scale_=(s: T) = if isValid(s) then _scale = s
