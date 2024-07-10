package entities

import sge.core.*
import util.*
import Timer.*
import behaviours.dimension2d.*

import scala.concurrent.duration.*

object EntityStateMachine:

  /** State of a generic game entity. It has the spawning state and destroyed state
    */
  enum EntityState:
    case Spawning()
    case Dying(destroy: Boolean = false)

  private val startingOffset: Vector2D = (0, 10)
  private  val spawnLerpFactor = 0.2
  private val deathScaleFactor = 1.5

import EntityStateMachine.*
import EntityState.*

/** Timer State Machine that models a generic game entity. It manages Spawning and Destruction of the entity.
  * The entity behaviour can be implemented in [[onEntityStateChange]] and [[whileInEntityState]].
  * @param startingPosition
  *   the spawn position
  * @param startingState
  *   the state to enter after the spawn
  * @tparam T
  *   The type of the state of the FS machine.
  */
abstract class EntityStateMachine[T](startingPosition: Vector2D, startingState: Timer[EntityState | T]) extends Behaviour
  with TimerStateMachine[EntityState | T](Spawning() forAbout 1500.millis)
  with SingleScalable
  with Positionable(startingPosition + startingOffset):

  /** Setup the new state to enter when the current state timer triggers.
    * This function will be called during [[onEarlyUpdate]]
    * @param state
    *   the state to elaborate
    * @param engine
    *   the engine
    * @return
    *   the new timer for the FS machine
    */
  def onEntityStateChange(state: T)(engine: Engine): Timer[T]

  /** Setup the actions to do while in a given state.
    * This function will be called during [[onUpdate]]
    *
    * @param state
    * the state to elaborate
    * @param engine
    * the engine
    */
  def whileInEntityState(state: T)(engine: Engine): Unit

  override def onStateChange(state: EntityState | T)(engine: Engine): Timer[EntityState | T] = state match
    case Spawning() => startingState

    case Dying(false) =>
      Dying(true) forAbout 500.millis

    case Dying(_) =>
      engine.destroy(this)
      Dying().forever

    case s => onEntityStateChange(s.asInstanceOf[T])(engine).asInstanceOf[Timer[EntityState | T]]

  override def whileInState(state: EntityState | T)(engine: Engine): Unit = state match
    case Spawning() =>
      position = VectorUtils.lerp(position, startingPosition, spawnLerpFactor)

    case Dying(true) =>
      scale = scale - deathScaleFactor * engine.deltaTimeSeconds

    case Dying(_) =>

    case s => whileInEntityState(s.asInstanceOf[T])(engine)

  protected def setDeathState(): Unit = state = Dying()