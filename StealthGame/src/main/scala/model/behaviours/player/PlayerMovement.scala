package model.behaviours.player

import sge.core.*
import sge.swing.*
import model.logic.{*, given}
import Direction.*
import MovementStateImpl.*
import State.*
import Action.*
import model.behaviours.CharacterCollisions.collidesWithWalls

private object PlayerMovement extends MovementActions:
  def onMoveTop(player: Player)(input: InputButton): Engine => Unit =
    engine =>
      val turnState =
        for _ <- if collidesWithWalls(engine, player) then stop() else moveAndTurn(TOP)
        yield ()

      updateState(turnState)

  def onMoveBottom(player: Player)(input: InputButton): Engine => Unit =
    engine =>
      val turnState =
        for _ <- if collidesWithWalls(engine, player) then stop() else moveAndTurn(BOTTOM)
        yield ()

      updateState(turnState)

  def onMoveRight(player: Player)(input: InputButton): Engine => Unit =
    engine =>
      val turnState =
        for _ <- if collidesWithWalls(engine, player) then stop() else moveAndTurn(RIGHT)
        yield ()

      updateState(turnState)

  def onMoveLeft(player: Player)(input: InputButton): Engine => Unit =
    engine =>
      val turnState =
        for _ <- if collidesWithWalls(engine, player) then stop() else moveAndTurn(LEFT)
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
    engine => updateState(stop())
