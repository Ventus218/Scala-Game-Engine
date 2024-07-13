package util

import scala.concurrent.duration._

class EnableEveryTimerTests extends TimerTests:
  override def name: String = "EnableEveryTimer"

  override def timer: Timer[Int] = Timer.runEvery(2.seconds, state = defaultState)

  override def enabledTimers: Seq[Timer[Int]] = Seq(
    timer,
    timer.updated(2.seconds),
    timer.updated(5.seconds),
    timer.updated(2.seconds).updated(2.seconds),
    timer.updated(1.second).updated(1.second).updated(1.second).updated(1.second)
  )

  override def disabledTimers: Seq[Timer[Int]] =
    Seq(
      timer.updated(0.seconds),
      timer.updated(1.5.seconds),
      timer.updated(0.5.seconds).updated(0.5.seconds),
      timer.updated(1.second).updated(2.seconds).updated(1.second)
    )
