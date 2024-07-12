package game

import sge.core.*

object Values:
  object ImagePaths:
    val cardBackside: String = "cards/backside.png"

  object Dimensions:
    object Cards:
      val width: Double = 10
      val height: Double = 16

  object Positions:
    val deck: Vector2D = (-40, 0)
    val trumpCard: Vector2D = (-35, 0)

  object Ids:
    val gameModel: String = "gameModel"