package util

import scala.concurrent.duration._

class EnableOnceAfterTimerTests extends TimerTests:
  override def name: String = "EnableOnceAfterTimer"

  override def timer: Timer[Int] = Timer.runOnceAfter(2.seconds, state = defaultState)

  override def enabledTimers: Seq[Timer[Int]] = Seq(
    timer.updated(2.seconds),
    timer.updated(5.seconds)
  )

  override def disabledTimers: Seq[Timer[Int]] =
    Seq(
      timer.updated(0.seconds),
      timer.updated(1.5.seconds),
      timer.updated(0.5.seconds).updated(0.5.seconds),
      timer.updated(3.seconds).updated(2.seconds)
    )
    
