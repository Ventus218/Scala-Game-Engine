package scenes.levels

import scenes.behaviours.LifesBehaviour
import config.Config.*

import model.behaviours.player.Player
import model.behaviours.*

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import sge.swing.*
import model.behaviours.{TopBound, BottomBound, RightBound, LeftBound}

/** Scene containing the items that will be on every level, like the Player, the
  * UI of the lifes, the stair and the bounds of the map
  */
object Level extends Scene:
  def apply(
      currentScene: Scene,
      nextScene: Scene,
      stairsPosition: Vector2D,
      playerPosition: Vector2D = (-SCENE_RIGHT_EDGE + CHARACTERS_WIDTH / 2, 0)
  ): Iterable[Behaviour] =
    Seq(
      Player(
        currentScene = currentScene,
        nextScene = nextScene,
        initialPosition = playerPosition
      )(
        speed = Vector2D.identity * PLAYER_SPEED,
        sprint = PLAYER_SPRINT
      ),
      LifesBehaviour(),
      Stairs(STAIRS_WIDTH, STAIRS_HEIGHT, "stairs.png", stairsPosition)()
    ) ++ this()

  override def apply(): Iterable[Behaviour] = Seq(
    TopBound(),
    LeftBound(),
    RightBound(),
    BottomBound()
  )
