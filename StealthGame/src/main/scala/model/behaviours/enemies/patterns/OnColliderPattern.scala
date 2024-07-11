package model.behaviours.enemies.patterns

import sge.core.Engine
import model.logic.{*, given}
import MovementStateImpl.*
import model.behaviours.enemies.*
import sge.core.EngineUtils.deltaTimeSeconds

trait TurnLeftOnCollidePattern extends OnCollidePattern:
  override protected val state: State[Movement, Unit] = turnLeft()
  override protected val doAfter: Engine => Unit = engine =>
    updateVisualRangeProperties()

trait TurnRightOnCollidePattern extends OnCollidePattern:
  override protected val state: State[Movement, Unit] = turnRight()
  override protected val doAfter: Engine => Unit = engine =>
    updateVisualRangeProperties()

trait StopThenTurnLeftOnCollidePattern(override protected val nSecondsToWait: Double)
    extends StopOnCollidePattern:
  override protected val resumingState: State[Movement, Unit] = turnLeft()

trait StopThenTurnRightOnCollidePattern(override protected val nSecondsToWait: Double)
    extends StopOnCollidePattern:
  override protected val resumingState: State[Movement, Unit] = turnRight()

private trait OnCollidePattern extends Enemy:
  protected val state: State[Movement, Unit]
  protected val doAfter: Engine => Unit
  override def onUpdate: Engine => Unit = engine =>
    super.onUpdate(engine)
    if visualRangeCollidesWithWalls(engine) then
      updateState(state)
      doAfter(engine)

private trait StopOnCollidePattern extends OnCollidePattern:
  protected val nSecondsToWait: Double
  protected val resumingState: State[Movement, Unit]

  override protected val state: State[Movement, Unit] = stop()
  override protected val doAfter: Engine => Unit = engine =>
    secondsPassed = secondsPassed + engine.deltaTimeSeconds

  override def onUpdate: Engine => Unit = engine =>
    super.onUpdate(engine)
    if secondsPassed > 0 then 
      updateState(stop())
      doAfter(engine)

    if secondsPassed >= nSecondsToWait
    then
      updateState(turnAndMove)
      updateVisualRangeProperties()
      secondsPassed = 0

  private var secondsPassed: Double = 0
  private def turnAndMove =
    for
      _ <- resumingState
      _ <- move()
    yield ()
