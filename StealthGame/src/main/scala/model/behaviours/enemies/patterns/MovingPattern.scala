package model.behaviours.enemies.patterns

import sge.core.Engine
import model.logic.MovementStateImpl.*

/** Pattern that changes the action of the enemy that will mixin this to MOVE
  */
trait MovingPattern extends OnCollidePattern:
  override def onStart: Engine => Unit = engine =>
    super.onStart(engine)
    updateState(move())
