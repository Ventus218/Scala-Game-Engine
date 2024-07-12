package model.logic

import MovementStateImpl.*

/** Define some actions in common to all the characters
  */
trait MovementActions:
  /** Current movement of the character
    */
  var movement = initialMovement

  /** Returns the current direction of the character
    */
  def getDirection = direction(movement)._2

  /** Returns the current action of the character
    */
  def getAction = action(movement)._2

  /** Update the movement based on a state
    *
    * @param state
    *   state that will evolve the movement of the character
    */
  def updateState(state: State[Movement, Unit]) =
    movement = state(movement)._1

  /** Stop then turn until facing the wanted direction.
    *
    * @param wantedDirection
    *   direction that the character will face after being stopped.
    * @return
    *   the state of the movement after the stop and turn.
    */
  protected def stopAndTurn(wantedDirection: Direction) =
    for
      _ <- stop()
      _ <- turnTo(wantedDirection)
    yield ()

  /** Move then turn until facing the wanted direction. If the character is
    * already sprinting, then this won't change it's action.
    *
    * @param wantedDirection
    *   direction that the character will face after being moved.
    * @return
    *   the state of the movement after the move and turn.
    */
  protected def moveAndTurn(wantedDirection: Direction) =
    for
      _ <- moveState
      _ <- turnTo(wantedDirection)
    yield ()

  /** Turn to the wanted direction.
    *
    * @param wantedDirection
    *   direction that the character will face.
    * @return
    *   the state of the movement after being turned.
    */
  protected def turnTo(
      wantedDirection: Direction
  ): State[Movement, Unit] =
    for
      d <- direction
      _ <-
        if d == wantedDirection
        then direction
        else
          for
            _ <- turnLeft()
            _ <- turnTo(wantedDirection)
          yield d
    yield ()

  private def moveState =
    for
      a <- action
      _ <- if a == Action.SPRINT then sprint() else move()
    yield ()
