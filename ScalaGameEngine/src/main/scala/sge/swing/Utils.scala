package sge.swing

import sge.core.*

/** Utility object for SwingIO */
object Utils:
  extension (io: SwingIO)
    /** Converts game-coordinates positions to screen-coordinates positions
      *
      * @param scenePosition
      *   The game-coordinate position to convert
      * @return
      *   The screen-coordinates position
      */
    def pixelPosition(scenePosition: Vector2D): (Int, Int) =
      (
        io.size._1 / 2 + (io.pixelsPerUnit * (scenePosition.x - io.center.x)).toInt,
        io.size._2 / 2 - (io.pixelsPerUnit * (scenePosition.y - io.center.y)).toInt
      )

    /** Converts screen-coordinates positions to game-coordinates positions
      *
      * @param pixelPosition
      *   The screen-coordinate position to convert
      * @return
      *   The game-coordinates position
      */
    def scenePosition(pixelPosition: (Int, Int)): Vector2D =
      (
        io.center.x + (pixelPosition._1 - io.size._1 / 2) / io.pixelsPerUnit,
        io.center.y - (pixelPosition._2 - io.size._2 / 2) / io.pixelsPerUnit
      )

  extension (engine: Engine)
    /** Utility for casting the engine IO to a SwingIO */
    def swingIO: SwingIO = engine.io.asInstanceOf[SwingIO]
