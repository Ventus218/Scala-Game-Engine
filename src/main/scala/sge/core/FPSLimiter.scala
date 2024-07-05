package sge.core

/** Provides the utilities to wait for the right amount of time to implement an
  * fps limit logic
  */
private[core] trait FPSLimiter:
  /** The current fps limit.
    *
    * If changed it will be applied only after the current frame is ended (call
    * on `onFrameEnd`)
    */
  def fpsLimit: Int

  /** The current fps limit.
    *
    * If changed it will be applied only after the current frame is ended (call
    * on `onFrameEnd`)
    */
  def fpsLimit_=(newValue: Int): Unit

  /** Make the thread sleep for the right amount of time to implement a fps
    * limit logic.
    *
    * @param frameStartNanos
    *   the `System.nanoTime()` of when the frame started.
    */
  def sleepToRespectFPSLimit(frameStartNanos: Long): Unit

  /** Must be called after `sleepToRespectFPSLimit` on every frame
    */
  def onFrameEnd(): Unit

object FPSLimiter:
  private class FPSLimiterImpl(private var limit: Int) extends FPSLimiter:
    private var newFPSLimit: Option[Int] = Option.empty

    def fpsLimit: Int = limit
    def fpsLimit_=(newValue: Int): Unit =
      newFPSLimit = Option(newValue)

    def sleepToRespectFPSLimit(frameStartNanos: Long): Unit =
      val frameDuration = System.nanoTime() - frameStartNanos

      val sleepDurationNanos = expectedFrameDurationNanos - frameDuration
      val millis = sleepDurationNanos / Math.pow(10, 6)
      if (millis > 0)
        Thread.sleep(millis.toInt)

    private def expectedFrameDurationNanos: Long =
      val expectedFrameDurationSeconds = (1d / limit)
      (expectedFrameDurationSeconds * Math.pow(10, 9)).toLong

    def onFrameEnd(): Unit =
      newFPSLimit match
        case None => {}
        case Some(value) =>
          limit = value
          newFPSLimit = None

  def apply(fpsLimit: Int): FPSLimiter = FPSLimiterImpl(fpsLimit)
