package managers

import entities.*
import sge.core.*
import ui.*

object SceneManager:

  val gameScene: Scene = () =>
    Seq(
      GameManager
    )
    
  val gameoverScene: Scene = () =>
    Seq(
      GameOverText(0, 2),
      YourScoreDisplay(0, 0),
      GameButton("Exit", (0, -3), onPress = _.stop())
    )
