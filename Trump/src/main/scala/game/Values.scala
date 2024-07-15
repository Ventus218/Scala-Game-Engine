package game

import sge.core.*
import sge.swing.output.Text.*
import java.awt.Color

object Values:
  object Players:
    val p1PlayerName: String = "P1"
    val p2PlayerName: String = "P2"
    
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
    val field: String = "field"

  object Text:
    val fontName: FontName = "Arial"
    val textStyle: TextStyle = TextStyle.Plain
    val sizePixel: Int = 50
    val size: Double = 3
    val color: Color = Color.black
