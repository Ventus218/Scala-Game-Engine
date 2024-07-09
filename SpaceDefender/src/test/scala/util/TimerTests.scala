package util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
abstract class TimerTests extends AnyFlatSpec:

  val defaultState: Int = 10
  def name: String
  def timer: Timer[Int]
  def enabledTimers: Seq[Timer[Int]]
  def disabledTimers: Seq[Timer[Int]]

  name should "wrap a state" in :
    timer.state shouldBe defaultState

  it should "be triggered when the trigger condition is met" in :
    enabledTimers.foreach(t =>
      var testValue: Int = 20
      t.foreach(testValue += _)
      testValue shouldBe (20 + t.state)
    )

  it should "not be triggered if the trigger condition is not met" in :
    disabledTimers.foreach(t =>
      var testValue: Int = 20
      t.foreach(testValue += _)
      testValue shouldBe 20
    )

  it should "change the state when the timer is triggered" in :
    enabledTimers.foreach(
      _.map(_ + 1).state shouldBe (defaultState + 1)
    )

  it should "not change the state when the timer is not triggered" in :
    disabledTimers.foreach(
      _.map(_ + 1).state shouldBe defaultState
    )
