package util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
class TimerTests extends AnyFlatSpec:

  "Timer" should "wrap a state" in:
    val timer = Timer(10)
    timer.state shouldBe 10

  it should "be updatable"

  it should "be triggered eventually"

  it should "change the state when the timer is triggered"

