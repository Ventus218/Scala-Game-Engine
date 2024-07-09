package model.logic

enum Direction:
  case TOP
  case BOTTOM
  case LEFT
  case RIGHT

sealed trait DirectionState:
  def direction: Direction
  def turnLeft(): DirectionState
  def turnRight(): DirectionState
  def move(): DirectionState

enum Movement extends DirectionState:
  case IDLE(override val direction: Direction)
  case MOVE(override val direction: Direction)
  case SPRINT(override val direction: Direction)

  import Privates.*
  import Direction.*

  override def turnLeft(): DirectionState = this match
    case IDLE(direction)   => IDLE(getLeftDirection(direction))
    case MOVE(direction)   => IDLE(direction).turnLeft().move()
    case SPRINT(direction) => SPRINT(getLeftDirection(direction))

  override def turnRight(): DirectionState = this match
    case IDLE(direction) => IDLE(getRightDirection(direction))
    case MOVE(direction) => IDLE(direction).turnRight().move()
    case SPRINT(direction) => SPRINT(getRightDirection(direction))
  
  override def move(): DirectionState = MOVE(direction)

  private object Privates:
    def getLeftDirection(direction: Direction): Direction =
      direction match
        case TOP    => LEFT
        case BOTTOM => RIGHT
        case LEFT   => BOTTOM
        case RIGHT  => TOP

    def getRightDirection(direction: Direction): Direction =
      direction match
        case TOP    => RIGHT
        case BOTTOM => LEFT
        case LEFT   => TOP
        case RIGHT  => BOTTOM
