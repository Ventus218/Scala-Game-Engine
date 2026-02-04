package sge.audio

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.core.IO

class SoundIOTest extends AnyFlatSpec:
  "SoundIO" should "be an IO class" in:
    SoundIO() shouldBe a[IO]

  it should "always have a master volume between 0.0 and 1.0" in:
    an[IllegalArgumentException] should be thrownBy {
      val soundIO = SoundIO(1.1)
    }

    an[IllegalArgumentException] should be thrownBy {
      val soundIO = SoundIO(-0.1)
    }

  it should "be customizable" in:
    val soundIO = SoundIO.withMasterVolume(0.5).build()

    soundIO.masterVolume shouldBe 0.5

  it should "allow changing the master volume at runtime" in:
    val soundIO = SoundIO(0.7)
    soundIO.masterVolume = 0.3
    soundIO.masterVolume shouldBe 0.3