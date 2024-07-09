package model.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Direction.*
import Movement.*

class MovementTests extends AnyFlatSpec:
  "MovementState" should "be created" in:
    val initialState = Movement.initialState(RIGHT)

  "MovementState" should "have the initial direction after being created" in:
    val initialState = Movement.initialState(RIGHT)
    val getDirection: NextState[MovementState, Direction] =
      for
        d <- direction
      yield
        d
    getDirection.run(initialState) shouldBe (initialState, RIGHT)