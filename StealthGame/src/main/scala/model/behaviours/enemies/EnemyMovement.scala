package model.behaviours.enemies

import model.logic.{*, given}
import model.logic.MovementStateImpl.*
import model.behaviours.VisualRange

trait EnemyMovement extends MovementActions:
  def turn(initialDirection: Direction) =
    updateState(turnTo(initialDirection))
