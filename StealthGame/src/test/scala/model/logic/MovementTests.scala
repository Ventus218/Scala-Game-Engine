package model.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Movement.*
import Direction.*

class MovementTests extends AnyFlatSpec:
  "Movement" should "have IDLE case class with direction" in:
    val state: Directionable = IDLE(TOP)
    state.direction shouldBe TOP
    IDLE(LEFT).direction shouldBe LEFT
    IDLE(BOTTOM).direction shouldBe BOTTOM
    IDLE(RIGHT).direction shouldBe RIGHT

  it should "have MOVE case class with direction" in:
    MOVE(TOP).direction shouldBe TOP
    MOVE(LEFT).direction shouldBe LEFT
    MOVE(BOTTOM).direction shouldBe BOTTOM
    MOVE(RIGHT).direction shouldBe RIGHT

  it should "have SPRINT case class with direction" in:
    SPRINT(TOP).direction shouldBe TOP
    SPRINT(LEFT).direction shouldBe LEFT
    SPRINT(BOTTOM).direction shouldBe BOTTOM
    SPRINT(RIGHT).direction shouldBe RIGHT

  it should "turn left IDLE" in:
    val state: State = IDLE(TOP)
    state.turnLeft() shouldBe IDLE(LEFT)
    IDLE(LEFT).turnLeft() shouldBe IDLE(BOTTOM)
    IDLE(BOTTOM).turnLeft() shouldBe IDLE(RIGHT)
    IDLE(RIGHT).turnLeft() shouldBe state

  it should "turn left MOVE" in:
    MOVE(TOP).turnLeft() shouldBe MOVE(LEFT)
    MOVE(LEFT).turnLeft() shouldBe MOVE(BOTTOM)
    MOVE(BOTTOM).turnLeft() shouldBe MOVE(RIGHT)
    MOVE(RIGHT).turnLeft() shouldBe MOVE(TOP)

  it should "turn left SPRINT" in:
    SPRINT(TOP).turnLeft() shouldBe SPRINT(LEFT)
    SPRINT(LEFT).turnLeft() shouldBe SPRINT(BOTTOM)
    SPRINT(BOTTOM).turnLeft() shouldBe SPRINT(RIGHT)
    SPRINT(RIGHT).turnLeft() shouldBe SPRINT(TOP)
  