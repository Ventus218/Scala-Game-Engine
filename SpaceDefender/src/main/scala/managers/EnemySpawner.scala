package managers

import sge.core.*
import util.*
import Timer.*
import entities.*

import scala.concurrent.duration.*

/** The core of the enemy management. It instantiates the enemies in the scene,
  * according to its implementation.
  */
trait EnemySpawner extends Behaviour

object EnemySpawner:

  /** Create a basic enemy spawner
    * @return
    *   the enemy spawner
    */
  def apply(): EnemySpawner = EnemySpawnerTimer()

  private enum SpawnPhase(val duration: FiniteDuration):
    case Idle extends SpawnPhase(1.second)
    case Phase1 extends SpawnPhase(15.seconds)
    case Phase2 extends SpawnPhase(30.seconds)
    case Phase3 extends SpawnPhase(1.minute)

  extension (e: Engine) private def dt: FiniteDuration = e.deltaTimeNanos.nanos

  import GameConstants.*
  import SpawnPhase.*

  /** Enemy spawner that creates enemies based on timers
    */
  private class EnemySpawnerTimer extends Behaviour
    with EnemySpawner
    with TimerStateMachine[SpawnPhase](Idle forAbout Idle.duration):

    val dropperSpawnCycleTime: FiniteDuration = 4.seconds
    val rangerSpawnCycleTime:  FiniteDuration = 7.seconds
    val beaconSpawnCycleTime: FiniteDuration = 10.seconds

    var dropperTimer: Timer[Unit] = Timer.runEvery(dropperSpawnCycleTime, ())
    var rangerTimer:  Timer[Unit] = Timer.runEvery(rangerSpawnCycleTime, ())
    var beaconTimer:  Timer[Unit] = Timer.runEvery(beaconSpawnCycleTime, ())
    
    override def whileInState(state: SpawnPhase)(engine: Engine): Unit = state match
      case Phase1 =>
        spawnDropper(engine)

      case Phase2 =>
        spawnDropper(engine)
        spawnRanger(engine)

      case Phase3 =>
        spawnDropper(engine)
        spawnRanger(engine)
        spawnBeacon(engine)

      case Idle =>

    override def onStateChange(state: SpawnPhase)(engine: Engine): Timer[SpawnPhase] = state match
      case Idle            => Phase1 forAbout Phase1.duration
      case Phase1          => Phase2 forAbout Phase2.duration
      case Phase2 | Phase3 => Phase3.forever

    private def spawnDropper(engine: Engine): Unit =
      val pos = GameManager.rearEnemyRandomPosition()
      dropperTimer = dropperTimer.map { u => engine.create(Dropper.dropper(pos)) }.updated(engine.dt)
    private def spawnRanger(engine: Engine): Unit =
      val pos = GameManager.frontalEnemyRandomPosition()
      rangerTimer = rangerTimer.map { u => engine.create(Ranger(pos)) }.updated(engine.dt)
    private def spawnBeacon(engine: Engine): Unit =
      val pos = GameManager.rearEnemyRandomPosition()
      beaconTimer = beaconTimer.map { u => engine.create(Dropper.beacon(pos)) }.updated(engine.dt)