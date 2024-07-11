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
  private val stairsY: Double = SCENE_HEIGHT / 2 - STAIRS_HEIGHT
  override def apply(): Iterable[Behaviour] =
    Level(this, LevelTwo, stairsPosition = (0, stairsY)) ++ Walls() ++ Enemies()

  private object Walls:
    val topVerticalWallHeight: Double = 11.5
    val topVerticalWallPosition: Vector2D =
      (-STAIRS_WIDTH, SCENE_HEIGHT / 2 - STAIRS_HEIGHT)

    val topHorizzontalWallWidth: Double = 20
    val topHorizzontalWallX: Double = topHorizzontalWallWidth / 2 - STAIRS_WIDTH
    val topHorizzontalWallY: Double =
      stairsY - STAIRS_HEIGHT / 2 - WALL_SIZE

    val centerHorizzontalWallWidth: Double = 50
    val centerHorizzontalWallPosition: Vector2D = (-5, 5)

    val centerVerticalWallWidth: Double = 32
    val centerVerticalWallPosition: Vector2D = (-30, -10.25)

    def apply() = Seq(
      RendererWall(
        height = topVerticalWallHeight,
        initialPosition = topVerticalWallPosition
      )(),
      RendererWall(
        height = centerVerticalWallWidth,
        initialPosition = centerVerticalWallPosition
      )(),
      RendererWall(
        width = topHorizzontalWallWidth,
        initialPosition = (topHorizzontalWallX, topHorizzontalWallY)
      )(),
      RendererWall(
        width = centerHorizzontalWallWidth,
        initialPosition = centerHorizzontalWallPosition
      )()
    )

  private object Enemies:
    import Walls.*

    val bottomEnemyX: Double = centerVerticalWallPosition._1 + CHARACTERS_WIDTH
    val bottomEnemyY: Double = -SCENE_HEIGHT / 2 + CHARACTERS_HEIGHT

    val rightEnemyX: Double = SCENE_WIDTH / 2 - CHARACTERS_WIDTH * 3
    val rightEnemyY: Double = topHorizzontalWallY - CHARACTERS_WIDTH

    def apply() = Seq(
      new Enemy(
        "patrol.png",
        Direction.TOP,
        (bottomEnemyX, bottomEnemyY)
      )() with MovingPattern with StopThenTurnRightOnCollidePattern(1),
      new Enemy(
        "patrol.png",
        Direction.LEFT,
        (rightEnemyX, rightEnemyY)
      )() with TurningLeftPattern(2)
    )
