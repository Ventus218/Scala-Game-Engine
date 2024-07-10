package entities

import sge.core.*
import behaviours.*
import physics2d.*
import sge.core.behaviours.dimension2d.Positionable

/** Flag trait for identifying an enemy
 */
trait Enemy extends Behaviour 
  with CircleCollider 
  with Health 
  with Positionable

object Enemies:

  val enemySize: Double = 1
  val dropperHealth: Int = 3
  val dropperSpeed: Double = 4
  
  
