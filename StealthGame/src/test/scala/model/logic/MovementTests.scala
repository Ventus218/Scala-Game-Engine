package model.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Movement.*
import Direction.*

class MovementTests extends AnyFlatSpec:
  "Movement" should "have case classes with direction" in:
    Direction.values.foreach(direction =>
      IDLE(direction).direction shouldBe direction
      MOVE(direction).direction shouldBe direction
      SPRINT(direction).direction shouldBe direction
    )

  private def leftDirection(direction: Direction): Direction =
    direction match
      case TOP    => LEFT
      case BOTTOM => RIGHT
      case LEFT   => BOTTOM
      case RIGHT  => TOP

  it should "turn left" in:
    Direction.values.foreach(direction =>
      IDLE(direction).turnLeft() shouldBe IDLE(leftDirection(direction))
      MOVE(direction).turnLeft() shouldBe MOVE(leftDirection(direction))
      SPRINT(direction).turnLeft() shouldBe SPRINT(leftDirection(direction))
    )

  private def rightDirection(direction: Direction): Direction =
    direction match
      case TOP    => RIGHT
      case BOTTOM => LEFT
      case LEFT   => TOP
      case RIGHT  => BOTTOM

  it should "turn right" in:
    Direction.values.foreach(direction =>
      IDLE(direction).turnRight() shouldBe IDLE(rightDirection(direction))
      MOVE(direction).turnRight() shouldBe MOVE(rightDirection(direction))
      SPRINT(direction).turnRight() shouldBe SPRINT(rightDirection(direction))
    )

  it should "stop" in:
    Direction.values.foreach(direction =>
      val idle = IDLE(direction)
      MOVE(direction).stop() shouldBe idle
      SPRINT(direction).stop() shouldBe idle
      IDLE(direction).stop() shouldBe idle
    )
  
  it should "move" in:
    Direction.values.foreach(direction =>
      val move = MOVE(direction)
      IDLE(direction).move() shouldBe move
      SPRINT(direction).move() shouldBe move
      MOVE(direction).move() shouldBe move
    )

  it should "sprint" in:
    Direction.values.foreach(direction =>
      val sprint = SPRINT(direction)
      IDLE(direction).sprint() shouldBe sprint
      MOVE(direction).sprint() shouldBe sprint
      SPRINT(direction).sprint() shouldBe sprint
    )