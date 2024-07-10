package model.behaviours.player

import sge.core.*
import sge.swing.*

private object PlayerMovement:
  import model.logic.{*, given}
  import Direction.*
  import MovementStateImpl.*
  import State.*
  import Action.*
  import Privates.*

  var movement = initialMovement

  def getDirection =
    for d <- direction
    yield d

  def getAction =
    for a <- action
    yield a

  def onMoveTop(input: InputButton): Engine => Unit =
    engine =>
      val turnState =
        for _ <- moveAndTurn(TOP)
        yield ()

      updateState(turnState)

  def onMoveBottom(input: InputButton): Engine => Unit =
    engine =>
      val turnState =
        for _ <- moveAndTurn(BOTTOM)
        yield ()

      updateState(turnState)

  def onMoveRight(input: InputButton): Engine => Unit =
    engine =>
      val turnState =
        for _ <- moveAndTurn(RIGHT)
        yield ()

      updateState(turnState)

  def onMoveLeft(input: InputButton): Engine => Unit =
    engine =>
      val turnState =
        for _ <- moveAndTurn(LEFT)
        yield ()

      updateState(turnState)

  def onSprint(input: InputButton): Engine => Unit =
    engine =>
      val sprintState =
        for
          a <- action
          _ <- if a == MOVE then sprint() else stop()
        yield ()

      updateState(sprintState)

  def resetSpeed() =
    val stopState =
      for _ <- stop()
      yield ()

    updateState(stopState)

  private object Privates:
    def updateState(state: State[Movement, Unit]) = movement = state(
      movement
    )._1

    def moveAndTurn(wantedDirection: Direction) =
      for
        _ <- moveState
        _ <- turnTo(wantedDirection)
      yield ()

    private def moveState =
      for
        a <- action
        _ <- if a == SPRINT then sprint() else move()
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
