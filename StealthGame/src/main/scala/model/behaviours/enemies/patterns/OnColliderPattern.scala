package model.behaviours.enemies.patterns

import sge.core.Engine
import model.logic.{*, given}
import MovementStateImpl.*
import model.behaviours.enemies.*
import EnemyMovement.*

private trait OnCollidePattern extends Enemy:
  protected val state: State[Movement, Unit]
  override def onUpdate: Engine => Unit = engine =>
    super.onUpdate(engine)
    if visualRangeCollidesWithWalls(engine) then
      updateState(state)
      updateVisualRangeProperties()

trait TurnLeftOnCollidePattern extends OnCollidePattern:
  override protected val state: State[Movement, Unit] = turnLeft()

trait TurnRightOnCollidePattern extends OnCollidePattern:
  override protected val state: State[Movement, Unit] = turnRight()