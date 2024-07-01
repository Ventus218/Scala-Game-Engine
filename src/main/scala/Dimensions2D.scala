import Physics2D.RectCollider
import Dimensions2D.Positionable
import Dimensions2D.Scalable
object Dimensions2D:
  /** Add 2D position to a Behaviour
    *
    * @param x
    *   position of the Behaviour on the X axis
    * @param y
    *   position of the Behaviour on the Y axis
    */
  trait Positionable(var x: Double = 0, var y: Double = 0) extends Behaviour

  trait IsNotValid[T]:
    def apply(scale: T): Boolean

  given IsNotValid[Double] with
    override def apply(scale: Double): Boolean = scale <= 0

  given IsNotValid[(Double, Double)] with
    override def apply(scale: (Double, Double)): Boolean = scale._1 <= 0 || scale._2 <= 0

  trait Scalable[T](private var _scale: T)(using isNotValid: IsNotValid[T]) extends Behaviour:
    require(!isNotValid(_scale))

    def scale: T = _scale
    def scale_=(s: T) = if !isNotValid(s) then _scale = s
