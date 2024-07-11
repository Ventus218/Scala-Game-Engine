package ui

import entities.Player
import managers.*
import sge.core.*
import sge.swing.*
import output.overlay.UIAnchor

import java.awt.Color

class HealthText extends Behaviour with UITextRenderer(
    "",
    GameConstants.scoreTextFont,
    Color.white,
    textOffset = (-10, 10),
    textAnchor = UIAnchor.TopRight
  ):
  private val healthUnit: String = "■"

  override def onEarlyUpdate: Engine => Unit = 
    engine =>
      engine.find[Player]().headOption
        .map(_.health)
        .foreach: h => 
          textContent = healthUnit * h
      super.onEarlyUpdate(engine)
  
  
