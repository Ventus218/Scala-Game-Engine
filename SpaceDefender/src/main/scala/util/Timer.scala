package util

import scala.concurrent.duration._

/** Monad for managing a state evolving during some time. The changes are applied only when
  * the timer activation condition triggers.
  * @tparam T
  *   the type of the wrapped state
  */
trait Timer[T]:
  /** The state of the timer. Remains always the same until the timer is triggered
    * @return
    *   the state
    */
  def state: T

  /** The duration used as reference for triggering the timer (activation depends on implementation)
    * @return
    *   the duration of the timer
    */
  def duration: FiniteDuration

  /** Updates the timer and return the updated timer. This method is responsible for triggering the timer.
    * @param deltaT
    *   the delta time, to indicate how much time has passed since the last call
    * @return
    *   the updated timer
    */
  def updated(deltaT: FiniteDuration): Timer[T]

  /** Apply a mapping function to the state of the timer (if triggered)
    * @param mapper
    *   the mapping function
    * @return
    *   the new timer
    */
  def map(mapper: T => T): Timer[T]

  /** Apply a mapping function to the state of the timer (if triggered)
    * @param mapper
    *   the mapping function
    * @return
    *   the new timer
    */
  def flatMap(mapper: T => Timer[T]): Timer[T]

  /** Apply a consumer function to the state of the timer (if triggered)
    * @param mapper
    *   the mapping function
    */
  def foreach(consumer: T => Unit): Unit

object Timer:

  /** Get a timer that enables after a given duration.
    * @param duration
    * @param state
    * @return
    */
  def runAfter[T](duration: FiniteDuration, state: T): Timer[T] = EnableAfterTimer(state, duration)

  /** Get a timer that enables only once after a given duration.
    * @param duration
    * @param state
    * @return
    */
  def runOnceAfter[T](duration: FiniteDuration, state: T): Timer[T] = EnableOnceAfterTimer(state, duration)

  /** Get a cyclic timer that enables once every duration time passes.
    * @param duration
    * @param state
    * @return
    */
  def runEvery[T](duration: FiniteDuration, state: T): Timer[T] = EnableOnceEveryTimer(state, duration)

  private case class AlwaysDisableTimer[T](state: T) extends Timer[T]:
    override val duration: FiniteDuration = 0.millis
    override def updated(deltaT: FiniteDuration): Timer[T] = this
    override def map(mapper: T => T): Timer[T] = this
    override def flatMap(mapper: T => Timer[T]): Timer[T] = this
    override def foreach(consumer: T => Unit): Unit = ()

  private class EnableAfterTimer[T](val state: T, val duration: FiniteDuration, val accTime: Long = 0) extends Timer[T]:
    override def updated(deltaT: FiniteDuration): Timer[T] = EnableAfterTimer(state, duration, accTime + deltaT.toMillis)

    protected def condition: Boolean = accTime >= duration.toMillis

    override def map(mapper: T => T): Timer[T] =
      if condition then
        EnableAfterTimer(mapper(state), duration, accTime)
      else
        this

    override def flatMap(mapper: T => Timer[T]): Timer[T] =
      if condition then
        mapper(state)
      else
        this

    override def foreach(consumer: T => Unit): Unit =
      if condition then consumer(state)


  private class EnableOnceAfterTimer[T](state: T, duration: FiniteDuration, accTime: Long = 0)
    extends EnableAfterTimer(state, duration, accTime):

    override def updated(deltaT: FiniteDuration): Timer[T] =
      if condition then
        AlwaysDisableTimer(state)
      else
        EnableOnceAfterTimer(state, duration, accTime + deltaT.toMillis)

    override def map(mapper: T => T): Timer[T] =
      if condition then
        EnableOnceAfterTimer(mapper(state), duration, accTime)
      else
        this

  private case class EnableOnceEveryTimer[T](state: T, duration: FiniteDuration, accTime: Long = 0, enabled: Boolean = true) extends Timer[T]:
    override def updated(deltaT: FiniteDuration): Timer[T] =
      if accTime + deltaT.toMillis >= duration.toMillis then
        EnableOnceEveryTimer(state, duration)
      else 
        EnableOnceEveryTimer(state, duration, accTime + deltaT.toMillis, false)

    override def map(mapper: T => T): Timer[T] =
      if enabled then
        EnableOnceEveryTimer(mapper(state), duration, accTime)
      else
        this

    override def flatMap(mapper: T => Timer[T]): Timer[T] =
      if enabled then
        mapper(state)
      else
        this

    override def foreach(consumer: T => Unit): Unit =
      if enabled then consumer(state)