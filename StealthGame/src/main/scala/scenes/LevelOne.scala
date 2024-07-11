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
    
  )
