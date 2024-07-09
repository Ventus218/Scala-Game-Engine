package model.logic

enum Direction:
  case TOP
  case BOTTOM
  case LEFT
  case RIGHT

sealed trait State:
  def direction: Direction

enum Movement extends State:
  case IDLE(override val direction: Direction)
  case MOVE(override val direction: Direction)
  case SPRINT(override val direction: Direction)
  
  