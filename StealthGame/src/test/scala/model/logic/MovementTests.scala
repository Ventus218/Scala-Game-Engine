package model.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Direction.*
import Movement.*
import Action.*

class MovementTests extends AnyFlatSpec:
  import Privates.*

  def initialState = Movement.initialState(_actualDirection)

  "MovementState" should "have the initial direction after being created" in:
    val nextState: NextState[MovementState, Direction] =
      for d <- direction
      yield d

    nextState.run(initialState) shouldBe (initialState, RIGHT)

  it should "stop, move and sprint" in:
    val nextStopState =
      for s <- stop()
      yield s

    val nextMoveState =
      for m <- move()
      yield m

    val nextSprintState =
      for s <- sprint()
      yield s

    Action.values.foreach(action =>
      Direction.values.foreach(direction =>
        _actualDirection = direction
        nextStopState(initialState) shouldBe ((IDLE, direction), ())
        nextMoveState(initialState) shouldBe ((MOVE, direction), ())
        nextSprintState(initialState) shouldBe ((SPRINT, direction), ())
      )
    )

  it should "turn direction" in:
    val nextState =
      for d <- turnLeft()
      yield d

    Direction.values.foreach(direction =>
      _actualDirection = direction
      nextState(initialState) shouldBe ((IDLE, getLeftDirection(direction)), ())
    )

  private object Privates:
    var _actualDirection: Direction = RIGHT
    def getLeftDirection(direction: Direction): Direction =
      direction match
        case RIGHT  => TOP
        case TOP    => LEFT
        case LEFT   => BOTTOM
        case BOTTOM => RIGHT
