package util

import sge.core.*
object VectorUtils:
    def lerp(start: Vector2D, end: Vector2D, factor: Double): Vector2D =
      val trueFactor = Math.clamp(factor, 0, 1)
      start + ((end - start) * trueFactor)
