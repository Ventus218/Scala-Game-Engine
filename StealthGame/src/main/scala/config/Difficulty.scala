package config

enum Difficulty:
  case EASY
  case NORMAL
  case HARD
  case IMPOSSIBLE

  def text: String = this match
    case EASY       => "Easy"
    case NORMAL     => "Normal"
    case HARD       => "Hard"
    case IMPOSSIBLE => "Impossible"

  def lifes: Int = this match
    case EASY   => 5
    case NORMAL => 3
    case _      => 1
