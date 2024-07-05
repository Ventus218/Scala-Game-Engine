package sge.core.metrics

object Angle:
  /** Basic type for manipulating angles in a simpler way.
    */
  type Angle = Double
  extension [N <: Int | Double](n: N)
    /** Convert the angle from radians to Angle
      * @return
      *   the corresponding angle
      */
    def radians: Angle = n match
      case i: Int    => i.toDouble
      case d: Double => d

    /** Convert the angle from degrees to Angle
      * @return
      *   the corresponding angle
      */
    def degrees: Angle = n.radians.toRadians
