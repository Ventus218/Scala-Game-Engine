package model.behaviours.enemies

import model.logic.{*, given}
import model.logic.MovementStateImpl.*
import model.behaviours.VisualRange
import sge.core.*
import behaviours.physics2d.RectCollider

private trait EnemyMovement extends MovementActions:
  def turn(initialDirection: Direction) =
    updateState(turnTo(initialDirection))
