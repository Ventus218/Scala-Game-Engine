package sge.swing.behaviours.ingame

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.output.Animations.Animation
import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import sge.swing.output.Animations
import sge.swing.output.Animations.AnimationController
import sge.swing.behaviours.RendererTestUtilities.animationControllerRenderer

class ControlledAnimationRendererTests extends AnyFlatSpec:
  val builder = AnimationController
        .builder()
        .addAnimation(
          "default",
          Animation.uniform(Seq("epic-crocodile.png"), 0.01)
        )
        .addAnimation(
          "second",
          Animation.uniform(Seq("epic-crocodile.png"), 0.5)
        )
        .withDefault("default")

  "ControlledAnimationRenderer" should "be created" in:
    val renderer = animationControllerRenderer(
      builder,
      10,
      10
    )

    renderer.animationHeight shouldBe 10
    renderer.animationWidth shouldBe 10
    renderer.currentAnimationName shouldBe "default"
    renderer.hasAnimation("default") shouldBe true
    renderer.hasAnimation("second") shouldBe true
    renderer.hasAnimation("other") shouldBe false

  it should "not be initialized with negative size" in:
    an[IllegalArgumentException] shouldBe thrownBy:
      animationControllerRenderer(builder, 0, 10)

    an[IllegalArgumentException] shouldBe thrownBy:
      animationControllerRenderer(builder, 10, -10)

  it should "be able to change its size" in:
    val renderer =
      animationControllerRenderer(builder, 10, 10)
    renderer.animationHeight = 20
    renderer.animationHeight shouldBe 20

    renderer.animationWidth = 20
    renderer.animationWidth shouldBe 20

  it should "not be possible to change size to negative values" in:
    val renderer =
      animationControllerRenderer(builder, 10, 10)

    an[IllegalArgumentException] shouldBe thrownBy:
      renderer.animationHeight = -20

    an[IllegalArgumentException] shouldBe thrownBy:
      renderer.animationWidth = -10

  it should "change current animation" in:
    val renderer =
      animationControllerRenderer(builder, 10, 10)

    renderer.playAnimation("second")
    renderer.currentAnimationName shouldBe "second"

  it should "not change to an animation that does not exists" in:
    val renderer =
      animationControllerRenderer(builder, 10, 10)

    an[IllegalArgumentException] shouldBe thrownBy:
      renderer.playAnimation("other")
