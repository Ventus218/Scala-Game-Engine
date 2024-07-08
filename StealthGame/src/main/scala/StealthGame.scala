import sge.core.{Engine, Storage}
import sge.swing.SwingIO

@main def main =
  val io = SwingIO
    .withTitle("Stealth Game")
    .withSize(1200, 720)
    .withPixelsPerUnitRatio(10)
    .build()

  Engine(io, Storage(), fpsLimit = 60).run(MenuScene)
