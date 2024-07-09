package managers
import sge.core.*
import behaviours.dimension2d.Positionable

/** Object containing all game general info regarding the arena
  */
object GameManager:
  val screenSize: (Int, Int) = (600, 900)
  val arenaWidth: Double = 10
  val pixelsPerUnit: Int = (screenSize._1.toDouble / arenaWidth).toInt
  val arenaHeight: Double = screenSize._2.toDouble / pixelsPerUnit.toDouble

  /** Check if a positionable is inside/outside the arena, aka is still visible
   * @param who
   * @return
   */
  def isOutsideArena(who: Positionable): Boolean =
    Math.abs(who.position.x) >= arenaWidth/2 + 1 || Math.abs(who.position.y) >= arenaHeight/2 + 1

