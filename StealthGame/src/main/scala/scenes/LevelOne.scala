package scenes

import sge.core.*
import config.*
import Difficulty.*
import Config.*
import model.behaviours.player.Player
import model.behaviours.enemies.Patrol
import sge.core.behaviours.dimension2d.Positionable
import sge.core.behaviours.dimension2d.Scalable
import sge.swing.behaviours.ingame.RectRenderer

object LevelOne extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    Player(width = CHARACTERS_WIDTH, height = CHARACTERS_HEIGHT)(
      speed = Vector2D.identity * PLAYER_SPEED,
      sprint = PLAYER_SPRINT
    ),
    Patrol(CHARACTERS_WIDTH, CHARACTERS_HEIGHT, Vector2D.identity * PATROL_SPEED)()
  )
