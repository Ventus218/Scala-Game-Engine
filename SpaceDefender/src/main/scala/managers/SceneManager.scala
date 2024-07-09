package managers

import entities.Bullets
import sge.core.*
import sge.core.behaviours.dimension2d.*
import sge.swing.behaviours.ingame.SquareRenderer

import java.awt.Color

object SceneManager:
  
  val testScene: Scene = () =>
    Seq(
      new Behaviour 
        with SquareRenderer(1, Color.red) 
        with Positionable(0, 0) {}
    )
