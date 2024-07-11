package managers

import managers.GameConstants.*
import ui.*
import sge.core.*

object SceneManager:
  
  val menuScene: Scene = () =>
    Seq(
      Background(),
      TitleText(0, 3),
      TitleScoreDisplay((0, 0), "Top score", topScoreStorageKey),
      GameButton("Play", (0, -3), onPress = _.loadScene(gameScene), color = playButtonColor),
      GameButton("Exit", (0, -5), onPress = _.stop())
    )

  val gameScene: Scene = () =>
    Seq(
      Background(),
      GameManager
    )
    
  val gameoverScene: Scene = () =>
    Seq(
      Background(),
      GameOverText(0, 2),
      TitleScoreDisplay((0, 0), "Your score", scoreStorageKey),
      GameButton("Menu", (0, -3), onPress = _.loadScene(menuScene))
    )
