package model.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Movement.*
import Direction.*

class MovementTests extends AnyFlatSpec:
  "Movement" should "have IDLE case class with direction" in:
    val state: State = IDLE(TOP)
    state.direction shouldBe TOP
    IDLE(LEFT).direction shouldBe LEFT
    IDLE(BOTTOM).direction shouldBe BOTTOM
    IDLE(RIGHT).direction shouldBe RIGHT

  it should "have MOVE case class with direction" in:
    val state: State = MOVE(TOP)
    state.direction shouldBe TOP
    MOVE(LEFT).direction shouldBe LEFT
    MOVE(BOTTOM).direction shouldBe BOTTOM
    MOVE(RIGHT).direction shouldBe RIGHT

  it should "have SPRINT case class with direction" in:
    val state: State = SPRINT(TOP)
    state.direction shouldBe TOP
    SPRINT(LEFT).direction shouldBe LEFT
    SPRINT(BOTTOM).direction shouldBe BOTTOM
    SPRINT(RIGHT).direction shouldBe RIGHT