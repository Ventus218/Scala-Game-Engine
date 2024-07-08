package sge.testing.behaviours

import sge.core.*

/** A game object mixed with this behaviour will stop the engine after the given
  * amount of frame has been run
  *
  * @param nFramesToRun
  */
trait NFrameStopper(val nFramesToRun: Int) extends Behaviour:
  require(nFramesToRun >= 0)

  private var frameCounter: Int = 0

  private def stopEngineIfNeeded(engine: Engine): Unit =
    if frameCounter > nFramesToRun then engine.stop()

  override def onStart: Engine => Unit = (engine) =>
    frameCounter += 1
    stopEngineIfNeeded(engine)
    super.onStart(engine)

  override def onLateUpdate: Engine => Unit = (engine) =>
    frameCounter += 1
    stopEngineIfNeeded(engine)
    super.onLateUpdate(engine)
