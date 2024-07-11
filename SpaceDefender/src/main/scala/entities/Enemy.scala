package entities

import sge.core.*
import behaviours.*
import managers.GameManager
import physics2d.*
import sge.core.behaviours.dimension2d.Positionable

/** Flag trait for identifying an enemy.
  * An enemy death makes you gain a score.
  */
trait Enemy extends Behaviour 
  with CircleCollider
  with Positionable
  with Health
  with Score:
  override def onDeath(): Unit =
    super.onDeath()
    GameManager.addScore(score)

object Enemy:

  val enemySize: Double = 1

  val dropperHealth: Int   = 3
  val dropperSpeed: Double = 4
  val dropperScore: Int    = 10

  val rangerHealth: Int = 5
  val rangerScore: Int  = 20
  
  
