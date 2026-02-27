package sge.swing.behaviours.ingame

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.output.Animations.Animation
import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import sge.swing.output.Animations
import sge.swing.behaviours.RendererTestUtilities.animationRenderer

class AnimatedImageRendererTests extends AnyFlatSpec:
  "AnimatedImageRenderer" should "be created" in:
    val renderer =
      animationRenderer(Seq("epic-crocodile.png"), 0.01, 10, 10, false)

    renderer.animationHeight shouldBe 10
    renderer.animationWidth shouldBe 10
    renderer.animation shouldBe a[Animation]
    renderer.animation.frames shouldBe Seq(
      Animations.AnimationFrame("epic-crocodile.png", 0.01)
    )
    renderer.frames shouldBe renderer.animation.frames
    renderer.animation.loop shouldBe false
    renderer.loop shouldBe renderer.animation.loop
    renderer.animation.totalDuration shouldBe renderer.frames
      .map(_.duration)
      .sum
    renderer.totalDuration shouldBe renderer.animation.totalDuration

    all(
      renderer.animation.frames.map(_.duration shouldBe 0.01)
    )

  it should "not be initialized with negative size" in:
    an[IllegalArgumentException] shouldBe thrownBy:
      animationRenderer(Seq("epic-crocodile.png"), 0.01, 0, 10)

    an[IllegalArgumentException] shouldBe thrownBy:
      animationRenderer(Seq("epic-crocodile.png"), 0.01, 10, -10)

  it should "be able to change its size" in:
    val renderer =
      animationRenderer(Seq("epic-crocodile.png"), 0.01, 10, 10, false)
    renderer.animationHeight = 20
    renderer.animationHeight shouldBe 20

    renderer.animationWidth = 20
    renderer.animationWidth shouldBe 20

  it should "not be possible to change size to negative values" in:
    val renderer =
      animationRenderer(Seq("epic-crocodile.png"), 0.01, 10, 10, false)

    an[IllegalArgumentException] shouldBe thrownBy:
      renderer.animationHeight = -20

    an[IllegalArgumentException] shouldBe thrownBy:
      renderer.animationWidth = -10