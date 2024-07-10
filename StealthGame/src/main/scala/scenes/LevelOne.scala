package scenes

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import config.*
import Difficulty.*
import Config.*
import model.behaviours.player.Player
import model.behaviours.enemies.Enemy
import sge.swing.behaviours.ingame.RectRenderer
import model.logic.{*, given}
import MovementStateImpl.*
import model.behaviours.enemies.patterns.MovingPattern

object LevelOne extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    Player(width = CHARACTERS_WIDTH, height = CHARACTERS_HEIGHT)(
      speed = Vector2D.identity * PLAYER_SPEED,
      sprint = PLAYER_SPRINT
    ),
    new Enemy(CHARACTERS_WIDTH, CHARACTERS_HEIGHT, Vector2D.identity * PATROL_SPEED, "patrol.png")() with MovingPattern
  )
