package managers

import entities.*
import sge.core.*

object SceneManager:

  val testScene: Scene = () =>
    Seq(
      GameManager,
      Player(0, 0),
      Ranger(0, 5)
    )
