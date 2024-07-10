package managers

import sge.core.*
import entities.*
import behaviours.*
import dimension2d.*
import sge.swing.*
import util.*

import java.awt.*
import scala.util.Random

/** Manager of the game
  */
object GameManager extends Behaviour:

  import GameConstants.*

  private var playerRef: Option[Player] = Option(Player(0, arenaBottomBorder + 1))
  private var enemiesRef: Seq[Enemy] = Seq.empty

  private var score: Int = 0
  private val scoreText: UITextRenderer =
    new Behaviour with UITextRenderer(
      s"Score: $score",
      scoreTextFont,
      Color.white,
      textOffset = (10, 10)
    )

  override def onStart: Engine => Unit =
    engine =>
      engine.create(scoreText)
      playerRef.foreach(engine.create)
  override def onEarlyUpdate: Engine => Unit =
    engine =>
      playerRef = engine.find[Player]("player")
      enemiesRef = engine.find[Enemy]().toSeq

  /** Adds score to the current one
    * @param points
    *   the score to add
    */
  def addScore(points: Int): Unit =
    score += points
    scoreText.textContent = s"Score: $score"

  /** Check if a positionable is inside/outside the arena, aka is still visible
   *
   * @param who
    * @return
    */
  def isOutsideArena(who: Positionable): Boolean =
    Math.abs(who.position.x) >= arenaWidth/2 + 1 || Math.abs(who.position.y) >= arenaHeight/2 + 1

  /** Adjust the given position to be inside the player movement area.
   *
   * @param position
    *   the position to adjust
    * @return
    *   the correct position
    */
  def adjustPlayerPosition(position: Vector2D): Vector2D =
    VectorUtils.clamp(
      position,
      (arenaLeftBorder, arenaBottomBorder),
      (arenaRightBorder, playerTopBorder)
    )

  /** Get a random position inside the enemy spawning space
   *
   * @return
    *   the position
    */
  def enemyRandomPosition(): Vector2D = (
    Random.between(arenaLeftBorder, arenaRightBorder),
    Random.between(0, arenaTopBorder)
  )

  /** Get the enemies references. It is updated at every early update.
   *
   * @return
    *   the enemies
    */
  def enemies: Seq[Enemy] = enemiesRef

  /** Get the player reference. It is updated at every early update.
   *
   * @return
    *   the player
    */
  def player: Option[Player] = playerRef