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
      val turnTopState =
        for
          _ <- moveState
          d <- direction
          _ <- turnTo(d, TOP)
        yield ()

      movement = turnTopState(movement)._1

  def onMoveBottom(input: InputButton): Engine => Unit =
    engine =>
      val turnBottomState =
        for
          _ <- moveState
          d <- direction
          _ <- turnTo(d, BOTTOM)
        yield ()

      movement = turnBottomState(movement)._1

  def onMoveRight(input: InputButton): Engine => Unit =
    engine =>
      val turnRightState =
        for
          _ <- moveState
          d <- direction
          _ <- turnTo(d, RIGHT)
        yield ()

      movement = turnRightState(movement)._1

  def onMoveLeft(input: InputButton): Engine => Unit =
    engine =>
      val turnLeftState =
        for
          _ <- moveState
          d <- direction
          _ <- turnTo(d, LEFT)
        yield ()

      movement = turnLeftState(movement)._1

  def onSprint(input: InputButton): Engine => Unit = engine =>
    val sprintState =
      for
        a <- action
        _ <- if a == MOVE then sprint() else stop()
      yield ()

    movement = sprintState(movement)._1

  def resetSpeed() =
    val stopState =
      for _ <- stop()
      yield ()
    movement = stopState(movement)._1

  private object Privates:
    def moveState =
        for
          a <- action
          _ <- if a == SPRINT then sprint() else move()
        yield ()

    def turnTo(startingDirection: Direction, wantedDirection: Direction): State[Movement, Direction] =
      if startingDirection == wantedDirection
      then direction
      else
        for
          _ <- turnLeft()
          d <- direction
          _ <- turnTo(d, wantedDirection)
        yield d
