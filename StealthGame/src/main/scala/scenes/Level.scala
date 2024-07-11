package scenes

import behaviours.LifesBehaviour
import config.Config.*

import model.behaviours.player.Player
import model.behaviours.*
import walls.*

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import sge.swing.*
import model.behaviours.{TopBound, BottomBound, RightBound, LeftBound}

object Level extends Scene:
  def apply(
      currentScene: Scene,
      nextScene: Scene,
      stairsPosition: Vector2D,
      playerPosition: Vector2D = (-SCENE_WIDTH / 2 + CHARACTERS_WIDTH / 2, 0)
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
