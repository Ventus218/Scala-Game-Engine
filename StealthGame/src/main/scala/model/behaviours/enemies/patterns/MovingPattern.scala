package model.behaviours.enemies.patterns
import sge.core.*
import model.logic.{*, given}
import MovementStateImpl.*
import model.behaviours.enemies.*
import EnemyMovement.*

trait MovingPattern extends Enemy:
  val movingState =
    for
      _ <- move()
    yield ()

  updateState(movingState)
