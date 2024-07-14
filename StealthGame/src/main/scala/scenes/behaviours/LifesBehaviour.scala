package scenes.behaviours

import sge.core.*
import sge.swing.*
import java.awt.Font
import config.Config.*
import java.awt.Color
import config.Difficulty
import model.behaviours.player.Player

/** UI Text of the lifes of the player
  */
trait LifesBehaviour extends Behaviour with UITextRenderer

/** Supply a LifesBehaviour on top left of the screen with a font size of 20,
  * black and bold, with Font Family Arial
  */
object LifesBehaviour:
  def apply(): LifesBehaviour = LifesBehaviourImpl()

  private val text = " with lifes: "

  private class LifesBehaviourImpl
      extends LifesBehaviour
      with Behaviour
      with UITextRenderer(text, UITextFontWithSize(20), Color.BLACK):

    override def onStart: Engine => Unit = engine =>
      val difficultyText =
        engine.storage.get[Difficulty]("Difficulty").text.toUpperCase()
      val difficultyLifes = engine.find[Player]().head.lifes
      textContent = difficultyText + text + difficultyLifes
      super.onStart(engine)
