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
  override def apply(): Iterable[Behaviour] = Level(this, LevelTwo, (0, SCENE_HEIGHT / 2 - STAIRS_HEIGHT)) ++ Seq(
    RendererWall(height = 11.5, initialPosition = (-STAIRS_WIDTH, SCENE_HEIGHT / 2 - STAIRS_HEIGHT))(),
    RendererWall(height = 25, initialPosition = (-30,0))(),
    RendererWall(width = 20, initialPosition = (10 - STAIRS_WIDTH, SCENE_HEIGHT / 2 - STAIRS_HEIGHT - STAIRS_HEIGHT / 2 - WALL_SIZE))(),
    RendererWall(width = 50, initialPosition = (-5, 5))()
  )
