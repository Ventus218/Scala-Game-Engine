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
      val moveState =
        for
          a <- action
          _ <- if a == SPRINT then sprint() else move()
        yield ()

      val turnTopState =
        for
          _ <- moveState
          d <- direction
          _ <- turnToTop(d)
        yield ()

      movement = turnTopState(movement)._1

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
    def turnToTop(d: Direction): State[Movement, Direction] =
      if d == TOP
      then direction
      else
        for
          _ <- turnLeft()
          d <- direction
          _ <- turnToTop(d)
        yield d
