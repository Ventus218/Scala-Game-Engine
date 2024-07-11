package managers

import entities.*
import sge.core.*

object SceneManager:

  val gameScene: Scene = () =>
    Seq(
      GameManager
    )
    
  val gameoverScene: Scene = () =>
    Seq(
      
    )
