package util

import scala.concurrent.duration._
class EnableAfterTimerTests extends TimerTests:
  override def name: String = "EnableAfterTimer"

  override def timer: Timer[Int] = Timer.runAfter(2.seconds, state = defaultState)

  override def enabledTimers: Seq[Timer[Int]] = Seq(
    timer.updated(2.seconds),
    timer.updated(5.seconds)
  )

  override def disabledTimers: Seq[Timer[Int]] =
    Seq(
      timer.updated(0.seconds), 
      timer.updated(1.5.seconds)
    )
  
