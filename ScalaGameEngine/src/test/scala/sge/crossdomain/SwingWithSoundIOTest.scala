package sge.crossdomain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.core.IO
import sge.swing.SwingIO
import sge.audio.SoundIO
import java.awt.Color

class SwingWithSoundIOTest extends AnyFlatSpec:
  "SwingWithSoundIO" should "be an union of SwingIO and SoundIO" in:
    val io = SwingWithSoundIO(SwingIO("title", (800, 600)), SoundIO(0.5))
    io.title shouldBe "title"
    io.size shouldBe (800, 600)
    io.masterVolume shouldBe 0.5

    io shouldBe a[IO]
    io shouldBe a[SwingIO]
    io shouldBe a[SoundIO]

  it should "be created with parameters of SwingIO and SoundIO" in:
    val io = SwingWithSoundIO(
      title = "Game Window",
      size = (1024, 768),
      pixelsPerUnit = 100,
      center = (0, 0),
      background = Color.black,
      frameIconPath = "icon.png",
      masterVolume = 0.75
    )

    io.title shouldBe "Game Window"
    io.size shouldBe (1024, 768)
    io.pixelsPerUnit shouldBe 100
    io.center shouldBe (0, 0)
    io.backgroundColor shouldBe Color.black
    io.masterVolume shouldBe 0.75

    io shouldBe a[IO]
    io shouldBe a[SwingIO]
    io shouldBe a[SoundIO]

  it should "be created as factory" in:
    val io = SwingWithSoundIO
      .withTitle("title")
      .withSize(800, 600)
      .withBackgroundColor(Color.red)
      .withMasterVolume(0.3)
      .build()

    io.title shouldBe "title"
    io.size shouldBe (800, 600)
    io.backgroundColor shouldBe Color.red
    io.masterVolume shouldBe 0.3
    io shouldBe a[IO]
    io shouldBe a[SwingIO]
    io shouldBe a[SoundIO]
