package entities

import entities.enemies.Enemy
import managers.*
import sge.core.*
import sge.swing.*

import java.awt.Color

object EnemyTests:

  val engine: Engine = Engine(
    SwingIO
      .withTitle("Space Defender - Enemy Test")
      .withCenter(0, 0)
      .withSize(GameConstants.screenSize)
      .withPixelsPerUnitRatio(GameConstants.pixelsPerUnit)
      .withBackgroundColor(Color.black)
      .build(),
    Storage(),
    50
  )
  val player: Player = Player()
  def spawner(spawnFunction: => Enemy): Behaviour = new Behaviour with InputHandler:
    var inputHandlers: Map[InputButton, Handler] = Map(Space  -> spawn.onlyWhenPressed)
    def spawn(input: InputButton)(engine: Engine): Unit = engine.create(spawnFunction)

  @main def testDropper(): Unit =
    engine.run: () =>
      Seq(
        player,
        spawner(Enemy.dropper(0, 0))
      )

  @main def testRanger(): Unit =
    engine.run: () =>
      Seq(
        player,
        spawner(Enemy.ranger(0, 0))
      )

  @main def testTurret(): Unit =
    engine.run: () =>
      Seq(
        player,
        spawner(Enemy.turret(0, 0))
      )

  @main def testBeacon(): Unit =
    engine.run: () =>
      Seq(
        player,
        spawner(Enemy.beacon(0, 0))
      )