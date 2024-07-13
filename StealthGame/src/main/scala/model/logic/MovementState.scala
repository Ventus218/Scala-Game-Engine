package model.logic

/** Define the 4 direction of the characters: TOP, LEFT, BOTTOM and RIGHT
  */
enum Direction:
  case TOP
  case LEFT
  case BOTTOM
  case RIGHT

/** Define the three actions of the characters: IDLE, MOVE, SPRINT
  */
enum Action:
  case IDLE
  case MOVE
  case SPRINT

/** Gives the capability to get a Movement and then evolve it through the State
  * Monad
  */
sealed trait MovementState:
  /** Type of the State to evolve
    */
  type Movement

  /** Returns the first state of Movement
    */
  def initialMovement: Movement

  /** Returns a State with a Movement and Unit as result, used just to get the
    * current Movement
    */
  def movementState: State[Movement, Unit]

  /** Gets the current Direction
    *
    * @return
    *   a State with the Movement (unmodified) and the current Direction
    */
  def direction: State[Movement, Direction]

  /** Gets the current Action
    *
    * @return
    *   a State with the Movement (unmodified) and the current Direction
    */
  def action: State[Movement, Action]

  /** Turns to left the current direction of the movement
    *
    * @return
    *   a State with the new Movement and Unit as a result
    */
  def turnLeft(): State[Movement, Unit]

  /** Turns to right the current direction of the movement
    *
    * @return
    *   a State with the new Movement and Unit as a result
    */
  def turnRight(): State[Movement, Unit]

  /** Changes to MOVE the current action of the movement
    *
    * @return
    *   a State with the new Movement and Unit as a result
    */
  def move(): State[Movement, Unit]

  /** Changes to SPRINT the current action of the movement
    *
    * @return
    *   a State with the new Movement and Unit as a result
    */
  def sprint(): State[Movement, Unit]

  /** Changes to IDLE the current action of the movement
    *
    * @return
    *   a State with the new Movement and Unit as a result
    */
  def stop(): State[Movement, Unit]

/** Implementation of MovementState
  */
object MovementStateImpl extends MovementState:
  opaque type Movement = (Action, Direction)

  import Action.*
  import Direction.*
  import Privates.*

  override def initialMovement: Movement =
    (IDLE, BOTTOM)

  override def movementState: State[Movement, Unit] =
    State(s => (s, ()))

  override def direction: State[Movement, Direction] =
    State(s => (s, s._2))

  override def action: State[Movement, Action] =
    State(s => (s, s._1))

  override def turnLeft(): State[Movement, Unit] =
    State((s, d) => ((s, getLeftDirection(d)), ()))

  override def turnRight(): State[Movement, Unit] =
    State((s, d) => ((s, getRightDirection(d)), ()))

  override def stop(): State[Movement, Unit] =
    State((_, d) => getState(IDLE, d))

  override def move(): State[Movement, Unit] =
    State((_, d) => getState(MOVE, d))

  override def sprint(): State[Movement, Unit] =
    State((_, d) => getState(SPRINT, d))

  extension [T](m: State[Movement, T])
    /** Apply a predicate to the result of the state and if it turns true then
      * the state won't change, otherwise the movement will stop
      *
      * @param p
      *   predicate to evaluate
      */
    def withFilter(p: Unit => Boolean): State[Movement, Unit] =
      for
        _ <- m
        _ <- if p(movementState) then movementState else stop()
      yield ()

  private object Privates:
    def getLeftDirection(direction: Direction): Direction =
      direction match
        case RIGHT  => TOP
        case TOP    => LEFT
        case LEFT   => BOTTOM
        case BOTTOM => RIGHT

    def getRightDirection(direction: Direction): Direction =
      direction match
        case RIGHT  => BOTTOM
        case TOP    => RIGHT
        case LEFT   => TOP
        case BOTTOM => LEFT

    def getState(action: Action, direction: Direction) =
      ((action, direction), ())
