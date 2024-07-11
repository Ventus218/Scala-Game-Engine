package scenes

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import sge.swing.behaviours.ingame.RectRenderer

import config.*
import Difficulty.*
import Config.*

import model.behaviours.player.Player
import model.behaviours.enemies.Enemy
import model.behaviours.*
import enemies.patterns.*

import model.logic.{*, given}
import MovementStateImpl.*
import scenes.behaviours.LifesBehaviour

object LevelOne extends Scene:

  override def apply(): Iterable[Behaviour] = Seq(
    Player(width = CHARACTERS_WIDTH, height = CHARACTERS_HEIGHT)(
      speed = Vector2D.identity * PLAYER_SPEED,
      sprint = PLAYER_SPRINT
    ),
    new Enemy(
      CHARACTERS_WIDTH,
      CHARACTERS_HEIGHT,
      Vector2D.identity * PATROL_SPEED,
      "patrol.png"
    )() with MovingPattern with TurningLeftPattern(2)
    ,
    Stairs(STAIRS_WIDTH, STAIRS_HEIGHT, "stairs.png", nextScene = LevelOne, (10, 10))(),
    LifesBehaviour()
  )

  def reset() = ()
