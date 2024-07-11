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
  
  private val text = "Lifes: "

  private class LifesBehaviourImpl
      extends LifesBehaviour
      with Behaviour
      with UITextRenderer(text, UITextFontWithSize(20), Color.BLACK):

    override def onLateUpdate: Engine => Unit = engine =>
      textContent = text + engine.storage.get[Difficulty]("Difficulty").lifes
      super.onLateUpdate(engine)
