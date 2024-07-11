package entities

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
  def spawner(spawnFunction: => Behaviour): Behaviour = new Behaviour with InputHandler:
    var inputHandlers: Map[InputButton, Handler] = Map(R -> spawn.onlyWhenPressed)
    def spawn(input: InputButton)(engine: Engine): Unit = engine.create(spawnFunction)

  @main def testDropper(): Unit =
    engine.run: () =>
      Seq(
        player,
        spawner(Dropper.dropper(0, 0))
      )