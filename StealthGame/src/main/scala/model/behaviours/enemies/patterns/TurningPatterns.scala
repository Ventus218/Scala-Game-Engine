package model.behaviours.enemies.patterns
import sge.core.*
import model.logic.{*, given}
import MovementStateImpl.*
import model.behaviours.enemies.*
import EnemyMovement.*

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

trait TurningLeftPattern(override val nSecondsToRepeat: Double)
    extends Enemy
    with TurningPattern:
  override val turnState =
    for _ <- turnLeft()
    yield ()

trait TurningRightPattern(override val nSecondsToRepeat: Double)
    extends Enemy
    with TurningPattern:
  override val turnState =
    for _ <- turnRight()
    yield ()
