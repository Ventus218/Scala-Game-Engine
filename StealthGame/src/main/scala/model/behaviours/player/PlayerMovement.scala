package model.behaviours.player

import sge.core.*
import sge.swing.*
import model.logic.{*, given}
import Direction.*
import MovementStateImpl.*
import State.*
import Action.*

private object PlayerMovement extends MovementActions:
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
          _ <- if a != IDLE then sprint() else stop()
        yield ()

      updateState(sprintState)

  def onResetSpeed(input: InputButton): Engine => Unit =
    engine =>
      val stopState =
        for _ <- stop()
        yield ()

      updateState(stopState)
