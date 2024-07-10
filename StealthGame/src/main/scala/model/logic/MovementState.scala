package model.logic

enum Direction:
  case TOP
  case BOTTOM
  case LEFT
  case RIGHT

enum Action:
  case IDLE
  case MOVE
  case SPRINT

sealed trait MovementState:
  type Movement
  def initialMovement: Movement

  def direction: State[Movement, Direction]
  def action: State[Movement, Action]

  def turnLeft(): State[Movement, Unit]
  def turnRight(): State[Movement, Unit]

  def move(): State[Movement, Unit]
  def sprint(): State[Movement, Unit]
  def stop(): State[Movement, Unit]

object MovementStateImpl extends MovementState:
  opaque type Movement = (Action, Direction)

  import Action.*
  import Direction.*
  import Privates.*

  override def initialMovement: Movement =
    (IDLE, BOTTOM)

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

  extension [T <: Direction | Action](m: State[Movement, T])
    def withFilter(p: T => Boolean): State[Movement, Unit] =
      State(s => if (p(m(s)._2)) then (s, ()) else ((IDLE, s._2), ()))

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
