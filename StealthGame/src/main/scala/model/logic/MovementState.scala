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

  def apply(direction: Direction): Movement

  def direction: NextState[Movement, Direction]
  def action: NextState[Movement, Action]

  def turnLeft(): NextState[Movement, Unit]
  def turnRight(): NextState[Movement, Unit]

  def move(): NextState[Movement, Unit]
  def sprint(): NextState[Movement, Unit]
  def stop(): NextState[Movement, Unit]

object MovementStateImpl extends MovementState:
  opaque type Movement = (Action, Direction)

  import Action.*
  import Privates.*

  override def apply(direction: Direction): Movement =
    (IDLE, direction)

  override def direction: NextState[Movement, Direction] =
    NextState(s => (s, s._2))

  override def action: NextState[Movement, Action] =
    NextState(s => (s, s._1))

  override def turnLeft(): NextState[Movement, Unit] =
    NextState((s, d) => ((s, getLeftDirection(d)), ()))

  override def turnRight(): NextState[Movement, Unit] =
    NextState((s, d) => ((s, getRightDirection(d)), ()))

  override def stop(): NextState[Movement, Unit] =
    NextState((_, d) => getState(IDLE, d))

  override def move(): NextState[Movement, Unit] =
    NextState((_, d) => getState(MOVE, d))

  override def sprint(): NextState[Movement, Unit] =
    NextState((_, d) => getState(SPRINT, d))

  private object Privates:
    import Direction.*

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
