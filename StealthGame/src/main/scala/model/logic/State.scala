package model.logic

enum Direction:
  case TOP
  case BOTTOM
  case LEFT
  case RIGHT

sealed trait State:
  def turnLeft(): State

trait Directionable:
  def direction: Direction

enum Movement extends State with Directionable:
  case IDLE(override val direction: Direction)
  case MOVE(override val direction: Direction)
  case SPRINT(override val direction: Direction)

  import Privates.*
  import Direction.*

  override def turnLeft(): State = this match
    case IDLE(direction)   => IDLE(getLeftDirection(direction))
    case MOVE(direction)   => MOVE(getLeftDirection(direction))
    case SPRINT(direction) => SPRINT(getLeftDirection(direction))

  private object Privates:
    def getLeftDirection(direction: Direction): Direction =
      direction match
        case TOP    => LEFT
        case BOTTOM => RIGHT
        case LEFT   => BOTTOM
        case RIGHT  => TOP
