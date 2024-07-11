package model.behaviours.enemies.patterns

import sge.core.Engine
import model.logic.MovementStateImpl.*

trait MovingPattern extends OnCollidePattern:
  override def onStart: Engine => Unit = engine =>
    super.onStart(engine)
    updateState(move())