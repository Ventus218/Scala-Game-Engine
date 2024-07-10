package managers

import entities.*
import sge.core.*

object SceneManager:

  val testScene: Scene = () =>
    Seq(
      GameManager,
      Player(0, -4),
      Dropper(0, 5),
      Ranger(0, 5)
    )
