package managers

import sge.core.*
import entities.*
import behaviours.*
import dimension2d.*
import sge.swing.*
import util.*
import Timer.*
import ui.*

import scala.concurrent.duration.*
import scala.util.Random

private enum GameState:
  case Starting
  case EnterPlayer
  case ShowMissionText
  case HideMissionText
  case GameStart
  case PlayerDestroyed
  case GameEnding
  case GameTerminated

import GameState.*

/** Manager of the game
  */
object GameManager extends Behaviour with TimerStateMachine[GameState](Starting forAbout 1500.millis):

  import GameConstants.*

  private var playerRef:  Option[Player] = Option(Player())
  private var enemiesRef: Seq[Enemy]     = Seq.empty

  private var score: Int = 0

  private val scoreText:   UITextRenderer = ScoreText()
  private val missionText: MissionText    = MissionText()

  override def onStateChange(state: GameState)(engine: Engine): Timer[GameState] = state match
    case Starting =>
      engine.create(scoreText)
      playerRef.foreach(engine.create)
      EnterPlayer forAbout 1500.millis

    case EnterPlayer =>
      engine.create(missionText)
      ShowMissionText forAbout 1500.millis

    case ShowMissionText =>
      HideMissionText forAbout 1500.millis

    case HideMissionText =>
      engine.destroy(missionText)
      GameStart.forever

    case PlayerDestroyed =>
      GameEnding forAbout 1500.millis

    case GameEnding =>
      System.exit(0)
      GameTerminated.forever

    case GameStart      => GameStart.forever
    case GameTerminated => GameTerminated.forever

  override def whileInState(state: GameState)(engine: Engine): Unit = state match
    case GameStart =>
      playerRef = engine.find[Player]("player")
      enemiesRef = engine.find[Enemy]().toSeq

    case ShowMissionText => missionText.show()

    case HideMissionText => missionText.hide()

    case _ =>

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