package model.behaviours.enemies.patterns
import sge.core.*
import model.logic.{*, given}
import MovementStateImpl.*
import model.behaviours.enemies.*

private trait TurningPattern extends Enemy:
  val nSecondsToRepeat: Double
  val turnState: State[Movement, Unit]

  var secondsPassed: Double = 0
  override def onLateUpdate: Engine => Unit = engine =>
    super.onLateUpdate(engine)
    secondsPassed = secondsPassed + engine.deltaTimeSeconds
    if secondsPassed >= nSecondsToRepeat
    then
      secondsPassed = 0
      updateState(turnState)
      updateVisualRangeProperties()

/** Turn the enemy to left after some seconds have passed
  *
  * @param nSecondsToRepeat
  *   seconds to wait until the next turn
  */
trait TurningLeftPattern(override val nSecondsToRepeat: Double)
    extends Enemy
    with TurningPattern:
  override val turnState = turnLeft()

/** Turn the enemy to right after some seconds have passed
  *
  * @param nSecondsToRepeat
  *   seconds to wait until the next turn
  */
trait TurningRightPattern(override val nSecondsToRepeat: Double)
    extends Enemy
    with TurningPattern:
  override val turnState = turnRight()
