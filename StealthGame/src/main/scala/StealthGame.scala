import sge.core.{Engine, Storage}
import sge.swing.SwingIO
import config.Config.{PIXEL_UNIT_RATIO, SCREEN_HEIGHT, SCREEN_WIDTH}
import scenes.StartingMenu
import sge.audio.SoundIO
import sge.crossdomain.SwingWithSoundIO

@main def main =
  val io = SwingIO
    .withTitle("Stealth Game")
    .withSize(SCREEN_WIDTH, SCREEN_HEIGHT)
    .withPixelsPerUnitRatio(PIXEL_UNIT_RATIO)
    .build()

  val audio = SoundIO.withMasterVolume(1).build()

  Engine(SwingWithSoundIO(io, audio), Storage(), fpsLimit = 60).run(
    StartingMenu
  )
