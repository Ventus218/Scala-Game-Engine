package managers

import sge.core.*
import behaviours.*
import dimension2d.*
import entities.*
import physics2d.*

import scala.util.Random

/** Object containing all game general info regarding the arena
  */
object GameManager extends Behaviour:
  val screenSize: (Int, Int) = (600, 900)
  val arenaWidth: Double = 10
  val pixelsPerUnit: Int = (screenSize._1.toDouble / arenaWidth).toInt
  val arenaHeight: Double = screenSize._2.toDouble / pixelsPerUnit.toDouble
  
  val arenaRightBorder: Double = arenaWidth / 2 - 1.5
  val arenaLeftBorder: Double = -arenaRightBorder
  val arenaTopBorder: Double = arenaHeight / 2 - 2.5

  /** Check if a positionable is inside/outside the arena, aka is still visible
    * @param who
    * @return
    */
  def isOutsideArena(who: Positionable): Boolean =
    Math.abs(who.position.x) >= arenaWidth/2 + 1 || Math.abs(who.position.y) >= arenaHeight/2 + 1

  /** Get a random position inside the enemy spawning space
    * @return
    *   the position
    */
  def enemyRandomPosition(): Vector2D = (
    Random.between(arenaLeftBorder, arenaRightBorder),
    Random.between(0, arenaTopBorder)
    )
    

  private var playerRef: Option[Player] = Option.empty
  private var enemiesRef: Seq[Enemy] = Seq.empty

  /** Get the player reference. It is updated at every early update.
    * @return
    *   the player
    */
  def player: Option[Player] = playerRef

  /** Get the enemies references. It is updated at every early update.
    * @return
    *   the enemies
    */
  def enemies: Seq[Enemy] = enemiesRef

  override def onEarlyUpdate: Engine => Unit =
    engine =>
      playerRef = engine.find[Player]("player")
      enemiesRef = engine.find[Enemy]().toSeq

