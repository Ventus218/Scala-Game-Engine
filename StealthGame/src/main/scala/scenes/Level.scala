package scenes

import behaviours.LifesBehaviour
import config.Config.*

import model.behaviours.player.Player
import model.behaviours.*
import walls.*

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import sge.swing.*

object Level extends Scene:
  def apply(
      currentScene: Scene,
      nextScene: Scene,
      stairsPosition: Vector2D
  ): Iterable[Behaviour] =
    Seq(
      Player(
        width = CHARACTERS_WIDTH,
        height = CHARACTERS_HEIGHT,
        currentScene = currentScene,
        nextScene = nextScene
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
