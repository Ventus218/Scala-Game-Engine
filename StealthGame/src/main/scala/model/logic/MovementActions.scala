package model.logic

import MovementStateImpl.*

trait MovementActions:
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

  def updateState(state: State[Movement, Unit]) =
    movement = state(movement)._1

  protected def stopAndTurn(wantedDirection: Direction) =
    for
      _ <- stop()
      _ <- turnTo(wantedDirection)
    yield ()

  protected def moveAndTurn(wantedDirection: Direction) =
    for
      _ <- moveState
      _ <- turnTo(wantedDirection)
    yield ()

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
