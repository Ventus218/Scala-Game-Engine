package entities

import sge.core.*
import behaviours.*
import managers.GameManager
import physics2d.*
import dimension2d.Positionable

import scala.concurrent.duration._
import scala.util.Random

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
  def dropperMovingTime: FiniteDuration = Random.between(1d, 2d).seconds
  def dropperShootingTime: FiniteDuration = 700.millis


  val rangerHealth: Int = 5
  val rangerScore: Int  = 20

  val beaconHealth: Int    = 10
  val beaconSpeed:  Double = 1
  val beaconScore:  Int    = 50
  def beaconMovingTime: FiniteDuration = Random.between(2.5, 4d).seconds
  def beaconShootingTime: FiniteDuration = 2.seconds
  
