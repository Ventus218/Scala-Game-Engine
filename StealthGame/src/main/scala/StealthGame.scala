import sge.core.*
import sge.swing.*

@main def main =
  val io = SwingIO
    .withTitle("Stealth Game")
    .withSize(1200, 720)
    .withPixelsPerUnitRatio(10)
    .build()

  MenuScene.buttonsWidth = 30
  MenuScene.buttonsHeight = 5

  Engine(io, Storage(), fpsLimit = 60).run(MenuScene)
