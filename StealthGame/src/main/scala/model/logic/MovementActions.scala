package model.logic

import MovementStateImpl.*

private trait MovementActions:
  var movement = initialMovement

  def getDirection =
    val directionState =
      for d <- direction
      yield d
    directionState.run(movement)._2

  def getAction =
    val actionState =
      for a <- action
      yield a
    actionState.run(movement)._2

  protected def updateState(state: State[Movement, Unit]) =
    movement = state(movement)._1

  protected def moveAndTurn(wantedDirection: Direction) =
    for
      _ <- moveState
      _ <- turnTo(wantedDirection)
    yield ()

  private def moveState =
    for
      a <- action
      _ <- if a == Action.SPRINT then sprint() else move()
    yield ()

  private def turnTo(wantedDirection: Direction): State[Movement, Unit] =
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
