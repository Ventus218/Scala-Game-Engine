package managers

import entities.*
import sge.core.*

object SceneManager:

  val testScene: Scene = () =>
    Seq(
      Player(0, 0)
    )
