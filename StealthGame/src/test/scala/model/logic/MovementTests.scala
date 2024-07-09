package model.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Direction.*
import Movement.*
import Action.*
import org.scalatest.compatible.Assertion

class MovementTests extends AnyFlatSpec:
  import Privates.*

  "MovementState" should "have the initial direction after being created" in:
    val nextState: NextState[MovementState, Direction] =
      for d <- direction
      yield d

    nextState.run(initialState) shouldBe (initialState, _actualDirection)

  it should "have the initial action after being created" in:
    val nextState =
      for a <- action
      yield a

    nextState.run(initialState) shouldBe (initialState, IDLE)

  it should "stop, move and sprint" in:
    val nextStopState =
      for _ <- stop()
      yield ()

    val nextMoveState =
      for _ <- move()
      yield ()

    val nextSprintState =
      for _ <- sprint()
      yield ()

    Action.values.foreach(action =>
      Direction.values.foreach(direction =>
        _actualDirection = direction
        checkDirections(
          direction,
          nextStopState,
          nextMoveState,
          nextSprintState
        )
      )
    )

  it should "turn direction to left" in:
    val nextStopState =
      for
        _ <- stop()
        _ <- turnLeft()
      yield ()

    val nextMoveState =
      for
        _ <- move()
        _ <- turnLeft()
      yield ()

    val nextSprintState =
      for
        _ <- sprint()
        _ <- turnLeft()
      yield ()

    Direction.values.foreach(direction =>
      _actualDirection = direction
      val leftDirection = getLeftDirection(direction)
      checkDirections(leftDirection, nextStopState, nextMoveState, nextSprintState)
    )
  
  it should "turn direction to right" in:
    val nextStopState =
      for
        _ <- stop()
        _ <- turnRight()
      yield ()

    val nextMoveState =
      for
        _ <- move()
        _ <- turnRight()
      yield ()

    val nextSprintState =
      for
        _ <- sprint()
        _ <- turnRight()
      yield ()

    Direction.values.foreach(direction =>
      _actualDirection = direction
      val rightDirection = getRightDirection(direction)
      checkDirections(rightDirection, nextStopState, nextMoveState, nextSprintState)
    )

  private object Privates:
    var _actualDirection: Direction = RIGHT
    def initialState = Movement.initialState(_actualDirection)

    def getLeftDirection(direction: Direction): Direction =
      direction match
        case RIGHT  => TOP
        case TOP    => LEFT
        case LEFT   => BOTTOM
        case BOTTOM => RIGHT

    def getRightDirection(direction: Direction): Direction =
      direction match
        case RIGHT  => BOTTOM
        case TOP    => RIGHT
        case LEFT   => TOP
        case BOTTOM => LEFT

    def checkDirections(
        direction: Direction,
        nextStopState: NextState[MovementState, Unit],
        nextMoveState: NextState[MovementState, Unit],
        nextSprintState: NextState[MovementState, Unit]
    ): Assertion =
      nextStopState(initialState)._1 shouldBe (IDLE, direction)
      nextMoveState(initialState)._1 shouldBe (MOVE, direction)
      nextSprintState(initialState)._1 shouldBe (SPRINT, direction)
