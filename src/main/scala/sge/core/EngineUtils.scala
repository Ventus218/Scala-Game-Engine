package sge.core

object EngineUtils:
  extension (e: Engine)
    /** The amount of time elapsed between the last frame and the current one in
      * seconds
      */
    def deltaTimeSeconds: Double = e.deltaTimeNanos / Math.pow(10, 9)
