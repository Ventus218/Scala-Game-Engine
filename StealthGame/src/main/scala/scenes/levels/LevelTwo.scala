package scenes.levels

import sge.core.*

import config.Config.*
import model.behaviours.*
import enemies.*
import patterns.*
import model.logic.Direction
import sge.swing.*

object LevelTwo extends Scene:
  override def apply(): Iterable[Behaviour] =
    Level(
      this,
      LevelThree,
      stairsPosition = (SCENE_RIGHT_EDGE - STAIRS_WIDTH, 0)
    ) ++ Walls() ++ Enemies()

  private object Walls:
    def apply() = Seq(
      RendererWall(
        height = 40,
        initialPosition = (-CHARACTERS_WIDTH / 2 - CHARACTERS_WIDTH * 2, 0)
      )()
    )

  private object Enemies:
    def enemyAnimationController = AnimationController
        .builder()
        .withDefault("patrol")
        .addAnimation("patrol", Animation.uniform(Seq("patrol.png"), 1))

    val bottomLeftEnemyPosition = (
      -SCENE_RIGHT_EDGE + CHARACTERS_WIDTH,
      -SCENE_TOP_EDGE + CHARACTERS_HEIGHT
    )
    val topRightEnemyPosition = bottomLeftEnemyPosition * -1
    def apply() = Seq(
      new Enemy(enemyAnimationController, Direction.RIGHT)() with TurningRightPattern(5),
      new Enemy(
        enemyAnimationController,
        Direction.RIGHT,
        position = bottomLeftEnemyPosition
      )() with MovingPattern with TurnLeftOnCollidePattern,
      new Enemy(
        enemyAnimationController,
        Direction.LEFT,
        position = topRightEnemyPosition
      )() with MovingPattern with TurnLeftOnCollidePattern
    )
