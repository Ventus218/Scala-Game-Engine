object Dimensions2D:

  object Vector:
    type Vector = (Double, Double)

    extension (v: Vector)
      def x: Double = v._1
      def y: Double = v._2
      def setX(x: Double): Vector = (x, v.y)
      def setY(y: Double): Vector = (v.x, y)
      infix def *(scalar: Double): Vector = (v.x * scalar, v.y * scalar)
      infix def /(scalar: Double): Vector = (v.x / scalar, v.y / scalar)
      infix def +(other: Vector): Vector = (v.x + other.x, v.y + other.y)
      infix def -(other: Vector): Vector = (v.x - other.x, v.y - other.y)

    object Vector:
      def identity: Vector = (1, 1)
    object Versor:
      def up: Vector = (0, 1)
      def down: Vector = (0, -1)
      def right: Vector = (1, 0)
      def left: Vector = (-1, 0)
      def x: Vector = right
      def y: Vector = up

  import Vector.*

  /** Add 2D position to a Behaviour
    *
    * @param position
    *   position of the Behaviour on the X and Y axis
    */
  trait Positionable(var position: Vector = (0, 0)) extends Behaviour

  /** Gives the capability to follow another Positionable. The position of this
    * Behaviour is initialized as the follower position in the init and updated
    * on the lateUpdate, according to an offset.
    *
    * @param followed
    * @param offset
    */
  trait PositionFollower(
      followed: Positionable,
      var positionOffset: Vector = (0, 0)
  ) extends Positionable:
    override def onInit: Engine => Unit =
      engine =>
        super.onInit(engine)
        position = followed.position + positionOffset

    override def onLateUpdate: Engine => Unit =
      engine =>
        super.onLateUpdate(engine)
        position = followed.position + positionOffset

  /** Gives the scaling component for width and height
    */
  trait ScalableElement extends Behaviour:
    def scaleWidth: Double = 1
    def scaleHeight: Double = 1

  /** Gives the capability to a Behaviour to scale based on a single value.
    *
    * @param _size
    *   value of the scaling, ScalableElement.scaleWidth and
    *   ScalableElement.scaleHeight will be equal to this. Must be greater than
    *   zero or otherwise throws an IllegalArgumentException.
    */
  trait SingleScalable(private var _size: Double = 1)
      extends ScalableElement:
    require(_size > 0)

    def scale = _size
    def scale_=(s: Double) =
      require(s > 0)
      _size = s

    override def scaleWidth: Double = scale
    override def scaleHeight: Double = scale

  /** Gives the capability to a Behaviour to scale based on two values.
    *
    * @param _scaleWidth
    *   scale value for the width, must be greater than zero or otherwise throws
    *   an IllegalArgumentException.
    * @param _scaleHeight
    *   scale value for the height, must be greater than zero or otherwise
    *   throws an IllegalArgumentException.
    */
  trait Scalable(
      private var _scaleWidth: Double = 1,
      private var _scaleHeight: Double = 1
  ) extends ScalableElement:
    require(_scaleWidth > 0 && _scaleHeight > 0)

    override def scaleWidth: Double = _scaleWidth
    def scaleWidth_=(s: Double) =
      require(s > 0)
      _scaleWidth = s

    override def scaleHeight: Double = _scaleHeight
    def scaleHeight_=(s: Double) =
      require(s > 0)
      _scaleHeight = s
