package sge.core.behaviours.dimension2d

import sge.core.Behaviour

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
trait SingleScalable(private var _size: Double = 1) extends ScalableElement:
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
  *   scale value for the height, must be greater than zero or otherwise throws
  *   an IllegalArgumentException.
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
