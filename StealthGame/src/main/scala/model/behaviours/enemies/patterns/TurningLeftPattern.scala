package model.behaviours.enemies.patterns
import sge.core.*
import model.logic.{*, given}
import MovementStateImpl.*
import model.behaviours.enemies.*
import EnemyMovement.*

trait TurningLeftPattern(nSecondsToRepeat: Double) extends Enemy:
  var secondsPassed: Double = 0

  val turnLeftState =
    for 
      _ <- turnRight()
    yield ()

  override def onLateUpdate: Engine => Unit = engine =>
    super.onLateUpdate(engine)
    secondsPassed = secondsPassed + engine.deltaTimeSeconds
    if secondsPassed >= nSecondsToRepeat
      then
        secondsPassed = 0
        updateState(turnLeftState)
        swapVisualRangeDimension()
        updateVisualRangeOffset()
