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

  def runAfter[T](duration: FiniteDuration, state: T): Timer[T] = EnableAfterTimer(state, duration)
  def runOnceAfter[T](duration: FiniteDuration, state: T): Timer[T] = ???
  def runEvery[T](duration: FiniteDuration, state: T): Timer[T] = ???

  private case class EnableAfterTimer[T](state: T, duration: FiniteDuration, accTime: Long = 0) extends Timer[T]:
    override def updated(deltaT: FiniteDuration): Timer[T] = EnableAfterTimer(state, duration, accTime + deltaT.toMillis)
    
    override def map(mapper: T => T): Timer[T] =
      if accTime >= duration.toMillis then
        EnableAfterTimer(mapper(state), duration, accTime)
      else
        this
        
    override def flatMap(mapper: T => Timer[T]): Timer[T] =
      if accTime >= duration.toMillis then
        mapper(state)
      else
        this
        
    override def foreach(consumer: T => Unit): Unit =
      if accTime >= duration.toMillis then consumer(state)

