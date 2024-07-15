import sge.core.{Engine, Storage}
import sge.swing.SwingIO
import config.Config.{SCREEN_HEIGHT, SCREEN_WIDTH, PIXEL_UNIT_RATIO}
import scenes.StartingMenu
import scenes.LoseGame
import scenes.WinGame
import scenes.DifficultyMenu

@main def main =
  val io = SwingIO
    .withTitle("Stealth Game")
    .withSize(SCREEN_WIDTH, SCREEN_HEIGHT)
    .withPixelsPerUnitRatio(PIXEL_UNIT_RATIO)
    .build()

  Engine(io, Storage(), fpsLimit = 60).run(StartingMenu)
