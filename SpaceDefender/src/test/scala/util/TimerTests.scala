package util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import scala.concurrent.duration._
class TimerTests extends AnyFlatSpec:

  val timer: Timer[Int] = Timer.runAfter(2.seconds, state = 10)

  "Timer" should "wrap a state" in:
    timer.state shouldBe 10

  it should "be triggered when the trigger condition is met" in:
    var testValue: Int = 20
    val triggeredTimer = timer.updated(timer.duration)
    triggeredTimer.foreach(testValue += _)
    testValue shouldBe 30

  it should "not be triggered if the trigger condition is not met" in:
    var testValue: Int = 20
    val triggeredTimer = timer.updated(0.seconds)
    triggeredTimer.foreach(testValue += _)
    testValue shouldBe 20

  it should "change the state when the timer is triggered" in:
    val triggeredTimer = timer.updated(timer.duration)
    triggeredTimer.map(_ + 1).state shouldBe 11

  it should "not change the state when the timer is not triggered" in:
    val triggeredTimer = timer.updated(0.seconds)
    triggeredTimer.map(_ + 1).state shouldBe 10
