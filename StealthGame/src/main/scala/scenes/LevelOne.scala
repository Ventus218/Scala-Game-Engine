package scenes

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import sge.swing.*

import config.*
import Difficulty.*
import Config.*

import model.behaviours.player.Player
import model.behaviours.enemies.Enemy
import model.behaviours.*
import enemies.patterns.*
import walls.*

import model.logic.{*, given}
import MovementStateImpl.*
import scenes.behaviours.LifesBehaviour

import java.awt.Color

object LevelOne extends Scene:

  override def apply(): Iterable[Behaviour] = Seq(
    Player(
      width = CHARACTERS_WIDTH,
      height = CHARACTERS_HEIGHT,
      currentScene = this,
      nextScene = this // TODO change
    )(
      speed = Vector2D.identity * PLAYER_SPEED,
      sprint = PLAYER_SPRINT
    ),
    new Enemy(
      CHARACTERS_WIDTH,
      CHARACTERS_HEIGHT,
      Vector2D.identity * PATROL_SPEED,
      "patrol.png"
    )() with MovingPattern with TurningLeftPattern(2) with StopThenTurnLeftOnCollidePattern(2),
    Stairs(STAIRS_WIDTH, STAIRS_HEIGHT, "stairs.png", (10, 10))(),
    LifesBehaviour(),
    TopBound(),
    LeftBound(),
    RightBound(),
    BottomBound(),
    new Wall(20d, 2d, (0, -30))() with RectRenderer(20d, 2d, Color.black)
  )
