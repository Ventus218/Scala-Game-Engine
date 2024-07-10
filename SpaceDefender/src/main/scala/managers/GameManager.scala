package managers

import sge.core.*
import behaviours.*
import dimension2d.*
import entities.*
import physics2d.*

/** Object containing all game general info regarding the arena
  */
object GameManager extends Behaviour:
  val screenSize: (Int, Int) = (600, 900)
  val arenaWidth: Double = 10
  val pixelsPerUnit: Int = (screenSize._1.toDouble / arenaWidth).toInt
  val arenaHeight: Double = screenSize._2.toDouble / pixelsPerUnit.toDouble
  
  val arenaRightBorder: Double = arenaWidth / 2 - 1
  val arenaLeftBorder: Double = -arenaRightBorder

  /** Check if a positionable is inside/outside the arena, aka is still visible
    * @param who
    * @return
    */
  def isOutsideArena(who: Positionable): Boolean =
    Math.abs(who.position.x) >= arenaWidth/2 + 1 || Math.abs(who.position.y) >= arenaHeight/2 + 1
    
  private var playerRef: Option[CircleCollider & Health] = Option.empty
  private var enemiesRef: Set[CircleCollider & Health] = Set.empty

  /** Get the player reference. It is updated at the very start of the game.
    * @return
    *   the player
    */
  def player: Option[CircleCollider & Health] = playerRef

  /** Get the enemies references. It is updated at every early update.
    * @return
    *   the enemies
    */
  def enemies: Set[CircleCollider & Health] = enemiesRef

  override def onStart: Engine => Unit =
    engine =>
      playerRef = engine.find[CircleCollider & Health & Identifiable]("player")
  override def onEarlyUpdate: Engine => Unit =
    engine =>
      enemiesRef = engine.find[CircleCollider & Health]().toSet.filterNot(player.contains)

