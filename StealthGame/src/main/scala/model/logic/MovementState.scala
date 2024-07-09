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

sealed trait State:
  type MovementState

  def initialState(direction: Direction): MovementState

  def direction: NextState[MovementState, Direction]
  def action: NextState[MovementState, Action]

  def turnLeft(): NextState[MovementState, Unit]
  def turnRight(): NextState[MovementState, Unit]

  def move(): NextState[MovementState, Unit]
  def sprint(): NextState[MovementState, Unit]
  def stop(): NextState[MovementState, Unit]

object Movement extends State:
  opaque type MovementState = (Action, Direction)

  import Action.*
  import Privates.*

  override def initialState(direction: Direction): MovementState =
    (IDLE, direction)

  override def direction: NextState[MovementState, Direction] =
    NextState(s => (s, s._2))

  override def action: NextState[MovementState, Action] =
    NextState(s => (s, s._1))

  override def turnLeft(): NextState[MovementState, Unit] =
    NextState((s, d) => ((s, getLeftDirection(d)), ()))

  override def turnRight(): NextState[MovementState, Unit] =
    NextState((s, d) => ((s, getRightDirection(d)), ()))

  override def stop(): NextState[MovementState, Unit] =
    NextState((_, d) => getState(IDLE, d))

  override def move(): NextState[MovementState, Unit] =
    NextState((_, d) => getState(MOVE, d))

  override def sprint(): NextState[MovementState, Unit] =
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
