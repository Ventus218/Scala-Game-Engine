package game

import sge.core.*
import java.awt.Color

object Values:
  object ImagePaths:
    val cardBackside: String = "cards/backside.png"

  object Dimensions:
    object Cards:
      val width: Double = 10
      val height: Double = 16
    object Buttons:
      val width: Double = 25
      val height: Double = 8

  object Positions:
    val deck: Vector2D = (-40, 0)
    val trumpCard: Vector2D = (-35, 0)

  object Ids:
    val gameModel: String = "gameModel"
    val playerReadyButton: String = "playerReadyButton"

  object Text:
    val size: Double = 3
    val color: Color = Color.black
