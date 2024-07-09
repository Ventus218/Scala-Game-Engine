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

  override def initialState(direction: Direction): MovementState = (IDLE, direction)

  override def direction: NextState[MovementState, Direction] = NextState(s => (s, s._2))

  override def action: NextState[MovementState, Action] = ???
  
  override def turnLeft(): NextState[MovementState, Unit] = ???

  override def turnRight(): NextState[MovementState, Unit] = ???

  override def move(): NextState[MovementState, Unit] = ???

  override def stop(): NextState[MovementState, Unit] = ???

  override def sprint(): NextState[MovementState, Unit] = ???

