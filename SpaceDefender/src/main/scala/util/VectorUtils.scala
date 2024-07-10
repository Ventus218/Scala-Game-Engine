package util

import sge.core.*
object VectorUtils:
    /** Calculate the linear interpolation between two points, given the factor.
      * Factor is a number between 0 and 1: 0 maps to start, 1 maps to end,
      * and every number in between maps to a point between start and end.
      * @param start
      *   the starting point
      * @param end
      *   the ending point
      * @param factor
      *   the interpolation factor. It will be clamped between 0 and 1 if less or greater
      * @return
      *   the interpolated point
      */
    def lerp(start: Vector2D, end: Vector2D, factor: Double): Vector2D =
      val trueFactor = Math.clamp(factor, 0, 1)
      start + ((end - start) * trueFactor)

    /** Construct a point with the coordinates clamped between the coordinates of the min and max points.
      * @param vector
      *   the point to clamp
      * @param minVector
      *   the minimum coordinates
      * @param maxVector
      *   the maximum coordinates
      * @return
      *   the clamped point 
     */
    def clamp(vector: Vector2D, minVector: Vector2D, maxVector: Vector2D): Vector2D =
      (
        Math.clamp(vector.x, minVector.x, maxVector.x),
        Math.clamp(vector.y, minVector.y, maxVector.y)
      )
