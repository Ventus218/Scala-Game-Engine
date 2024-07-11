package model.logic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Direction.*
import MovementStateImpl.*
import Action.*
import org.scalatest.compatible.Assertion

class MovementTests extends AnyFlatSpec:
  import Privates.*

  "Movement" should "have the initial direction after being created" in:
    val nextState =
      for d <- direction
      yield d

    nextState.run(initialState) shouldBe (initialState, _actualDirection)

  it should "have the initial action after being created" in:
    val nextState =
      for a <- action
      yield a

    nextState.run(initialState) shouldBe (initialState, IDLE)

  it should "stop, move and sprint" in:
    Action.values.foreach(action =>
      checkDirections(
        d => d,
        nextStopState,
        nextMoveState,
        nextSprintState
      )
    )

  it should "turn direction to left" in:
    val nextStopStateLeft =
      for
        _ <- nextStopState
        _ <- turnLeft()
      yield ()

    val nextMoveStateLeft =
      for
        _ <- nextMoveState
        _ <- turnLeft()
      yield ()

    val nextSprintStateLeft =
      for
        _ <- nextSprintState
        _ <- turnLeft()
      yield ()

    checkDirections(
      getLeftDirection,
      nextStopStateLeft,
      nextMoveStateLeft,
      nextSprintStateLeft
    )

  it should "turn direction to right" in:
    val nextStopStateRight =
      for
        _ <- nextStopState
        _ <- turnRight()
      yield ()

    val nextMoveStateRight =
      for
        _ <- nextMoveState
        _ <- turnRight()
      yield ()

    val nextSprintStateRight =
      for
        _ <- nextSprintState
        _ <- turnRight()
      yield ()

    checkDirections(
      getRightDirection,
      nextStopStateRight,
      nextMoveStateRight,
      nextSprintStateRight
    )

  it should "set Action to IDLE if filter not possible" in:
    val nextMoveStateFalse =
      for
        _ <- nextMoveState
        d <- direction
        _ <- movementState
        if d == RIGHT && d == LEFT
      yield ()

    val nextSprintStateTrue =
      for
        _ <- nextSprintState
        a <- action
        _ <- movementState
        if a == SPRINT
      yield ()

    _actualDirection = direction(initialState)._2
    nextMoveStateFalse(initialState)._1 shouldBe (IDLE, _actualDirection)

    nextSprintStateTrue(initialState)._1 shouldBe (SPRINT, _actualDirection)

  private object Privates:
    var _actualDirection: Direction = direction(initialState)._2
    def initialState = initialMovement

    val nextStopState =
      for _ <- stop()
      yield ()

    val nextMoveState =
      for _ <- move()
      yield ()

    val nextSprintState =
      for _ <- sprint()
      yield ()

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
        getNextDirection: Direction => Direction,
        nextStopState: State[Movement, Unit],
        nextMoveState: State[Movement, Unit],
        nextSprintState: State[Movement, Unit]
    ): Assertion =
      val nextDirection = getNextDirection(direction(initialState)._2)
      nextStopState(initialState)._1 shouldBe (IDLE, nextDirection)
      nextMoveState(initialState)._1 shouldBe (MOVE, nextDirection)
      nextSprintState(initialState)._1 shouldBe (SPRINT, nextDirection)
