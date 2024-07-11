package scenes.levels

import sge.core.*

import config.Config.*
import model.behaviours.*
import enemies.*
import patterns.*
import model.logic.Direction

object LevelTwo extends Scene:
  override def apply(): Iterable[Behaviour] =
    Level(
      this,
      LevelThree,
      stairsPosition = (SCENE_WIDTH / 2 - STAIRS_WIDTH, 0)
    ) ++ Walls() ++ Enemies()

  private object Walls:
    def apply() = Seq(
      RendererWall(
        height = 40,
        initialPosition = (-CHARACTERS_WIDTH / 2 - CHARACTERS_WIDTH * 2, 0)
      )()
    )

  private object Enemies:
    val bottomLeftEnemyPosition = (
      -SCENE_WIDTH / 2 + CHARACTERS_WIDTH,
      -SCENE_HEIGHT / 2 + CHARACTERS_HEIGHT
    )
    val topRightEnemyPosition = bottomLeftEnemyPosition * -1
    def apply() = Seq(
      new Enemy("patrol.png", Direction.RIGHT)() with TurningRightPattern(5),
      new Enemy(
        "patrol.png",
        Direction.RIGHT,
        position = bottomLeftEnemyPosition
      )() with MovingPattern with TurnLeftOnCollidePattern,
      new Enemy(
        "patrol.png",
        Direction.LEFT,
        position = topRightEnemyPosition
      )() with MovingPattern with TurnLeftOnCollidePattern
    )
