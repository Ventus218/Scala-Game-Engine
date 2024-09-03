package util

import sge.core.*
import util.Timer.*

import scala.concurrent.duration.*

/** Behaviour that represents a finite state machine, with built-in timers to execute delayed
  * state changes.
  * It doesn't update its current state at every frame, instead it goes according to the timer
  * declared in the [[onStateChange]] method
  * @tparam T
  *   The type of the state of the FS machine.
  */
trait TimerStateMachine[T](initialState: Timer[T]) extends Behaviour:
  private var timer: Timer[T] = initialState

  /** The state of the machine
    */
  def state: T = timer.state

  /** Set the current state. The state is effectively set in the next frame.
    * @param s
    *   the new state
    */
  protected def state_=(s: T): Unit = timer = s.immediately

  /** Setup the actions to do while in a given state.
    * This function will be called during [[onUpdate]]
    * @param state
    *   the state to elaborate
    * @param engine
    *   the engine
    */
  def whileInState(state: T)(engine: Engine): Unit

  /** Setup the new state to enter when the current state timer triggers.
    * This function will be called during [[onEarlyUpdate]]
    * @param state
    *   the state to elaborate
    * @param engine
    *   the engine
    * @return
    *   the new timer for the FS machine
    */
  def onStateChange(state: T)(engine: Engine): Timer[T]

  override def onInit: Engine => Unit =
    engine =>
      super.onInit(engine)
      timer = initialState

  override def onEarlyUpdate: Engine => Unit =
    engine =>
      super.onEarlyUpdate(engine)
      timer = timer.updated(engine.deltaTimeNanos.nanos).flatMap(onStateChange(_)(engine))

  override def onUpdate: Engine => Unit =
    engine =>
      super.onUpdate(engine)
      whileInState(state)(engine)
