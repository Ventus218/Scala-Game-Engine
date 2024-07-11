package scenes.behaviours

import sge.core.*
import sge.swing.*
import java.awt.Font
import config.Config.*
import java.awt.Color
import config.Difficulty

trait LifesBehaviour extends Behaviour with UITextRenderer

object LifesBehaviour:
  def apply(): LifesBehaviour = LifesBehaviourImpl()
  
  private val text = " with lifes: "

  private class LifesBehaviourImpl
      extends LifesBehaviour
      with Behaviour
      with UITextRenderer(text, UITextFontWithSize(20), Color.BLACK):

    override def onInit: Engine => Unit = engine =>
      val difficultyText = engine.storage.get[String]("Difficulty").toUpperCase()
      val difficultyLifes = engine.storage.get[Int]("Lifes")
      textContent = difficultyText + text + difficultyLifes
      super.onInit(engine)
