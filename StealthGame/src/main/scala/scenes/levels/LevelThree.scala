package scenes.levels

import config.Config.*
import sge.core.*
import model.logic.Direction
import model.behaviours.*
import enemies.*
import patterns.*
import scenes.WinGame

object LevelThree extends Scene:
  override def apply(): Iterable[Behaviour] = Level(
    this,
    WinGame,
    playerPosition = (0, SCENE_TOP_EDGE - CHARACTERS_HEIGHT / 2),
    stairsPosition = (0, -SCENE_TOP_EDGE + STAIRS_HEIGHT)
  ) ++ Walls() ++ Enemies()

object Walls:
  import Enemies.*

  val wallsWidth = 30
  val rightWallX = SCENE_RIGHT_EDGE - wallsWidth / 2
  val wallsY = CHARACTERS_HEIGHT * 2.5 + movingEnemyY

  def apply() = Seq(
    RendererWall(
      width = wallsWidth,
      initialPosition = (rightWallX, wallsY)
    )(),
    RendererWall(
      width = wallsWidth,
      initialPosition = (-rightWallX, wallsY)
    )()
  )

object Enemies:
  import Walls.*
  val movingEnemyX = SCENE_RIGHT_EDGE - CHARACTERS_WIDTH / 2
  val movingEnemyY = -10

  val rightEnemyX = rightWallX - wallsWidth - CHARACTERS_WIDTH / 2

  def apply() = Seq(
    new Enemy(
      "patrol.png",
      Direction.LEFT,
      (movingEnemyX, movingEnemyY)
    )() with MovingPattern with TurnLeftOnCollidePattern,
    new Enemy("patrol.png", Direction.TOP, (rightEnemyX, wallsY))()
      with TurningLeftPattern(4),
    new Enemy("patrol.png", Direction.TOP, (-rightEnemyX, wallsY))()
      with TurningRightPattern(4)
  )
