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

  private var playerRef:  Option[Player] = Option.empty

  private var score: Int = 0

  private val scoreText:   ScoreText   = ScoreText()
  private val healthText:  HealthText  = HealthText()
  private val missionText: MissionText = MissionText()

  override def onStateChange(state: GameState)(engine: Engine): Timer[GameState] = state match
    case Starting =>
      playerRef = Option(Player())
      score = 0
      scoreText.setScore(0)
      player.foreach(engine.create)
      engine.create(scoreText)
      engine.create(healthText)
      EnterPlayer forAbout 1500.millis

    case EnterPlayer =>
      engine.create(missionText)
      ShowMissionText forAbout 1500.millis

    case ShowMissionText =>
      HideMissionText forAbout 1500.millis

    case HideMissionText =>
      engine.destroy(missionText)
      engine.create(EnemySpawner())
      GameStart.forever

    case PlayerDestroyed =>
      saveScore(score)(engine)
      GameEnding forAbout 1500.millis

    case GameEnding =>
      engine.loadScene(SceneManager.gameoverScene)
      GameTerminated.forever

    case GameStart      => GameStart.forever
    case GameTerminated => GameTerminated.forever

  override def whileInState(state: GameState)(engine: Engine): Unit = state match
    case GameStart       => playerRef = engine.find[Player]("player")
    case ShowMissionText => missionText.show()
    case HideMissionText => missionText.hide()
    case _ =>

  /** Adds score to the current one
    * @param points
    *   the score to add
    */
  def addScore(points: Int): Unit =
    score += points
    scoreText.setScore(score)
    
  def onPlayerDeath(): Unit = state = PlayerDestroyed

  /** Check if a positionable is inside/outside the arena, aka is still visible
    * @param who
    * @return
    */
  def isOutsideArena(who: Positionable): Boolean =
    Math.abs(who.position.x) >= arenaWidth/2 + 1 || Math.abs(who.position.y) >= arenaHeight/2 + 1

  /** Adjust the given position to be inside the player movement area.
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

  /** Get a random position inside the enemy spawning frontal space
    * @return
    *   the frontal position
    */
  def frontalEnemyRandomPosition(): Vector2D = (
    Random.between(arenaLeftBorder, arenaRightBorder),
    Random.between(0, arenaTopBorder)
  )

  /** Get a random position inside the enemy spawning rear space
    * @return
    *   the position in the rear
    */
  def rearEnemyRandomPosition(): Vector2D = (
    Random.between(arenaLeftBorder, arenaRightBorder),
    arenaTopBorder
  )

  /** Get the player reference. It is updated at every early update.
    * @return
    *   the player
    */
  def player: Option[Player] = playerRef
  
  private def saveScore(s: Int)(e: Engine): Unit =
    e.storage.set(scoreStorageKey, s)
    if !e.storage.getOption[Int](topScoreStorageKey).exists(_ >= s) then
      e.storage.set(topScoreStorageKey, s)