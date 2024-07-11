package model.behaviours.enemies.patterns

import sge.core.Engine
import model.logic.MovementStateImpl.*
import model.behaviours.enemies.EnemyMovement.*

trait MovingPattern extends OnCollidePattern:
  private var secondsPassed: Double = 0
  override def onStart: Engine => Unit = engine =>
    super.onStart(engine)
    updateState(move())