package sge.swing.output

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.output.Animations.AnimationFrame
import sge.swing.output.Images.ImageResizer
import sge.swing.output.Animations.Animation
import org.scalatest.BeforeAndAfterEach
import sge.swing.output.GameElements.BaseGameElement

class AnimationsTest extends AnyFlatSpec with BeforeAndAfterEach:
  val animationFrame1 = AnimationFrame("epic-crocodile.png", 0.1)
  val animationFrame2 = AnimationFrame("epic-crocodile_0.png", 0.2)
  var animation = Animation(Seq(animationFrame1, animationFrame2))

  override protected def beforeEach(): Unit =
    animation = Animation(Seq(animationFrame1, animationFrame2))

  "AnimationFrame" should "have initial values" in:
    animationFrame1.imagePath shouldBe "epic-crocodile.png"
    animationFrame1.duration shouldBe 0.1
    animationFrame1.image shouldBe ImageResizer("epic-crocodile.png")

  it should "have a positive duration" in:
    an[IllegalArgumentException] shouldBe thrownBy:
      AnimationFrame("epic-crocodile.png", 0)

  "Animation" should "have initial values" in:
    animation.frames shouldBe Seq(animationFrame1, animationFrame2)
    animation.loop shouldBe true
    animation.currentFrame shouldBe animationFrame1
    animation.currentFrameIndex shouldBe 0
    animation.isFinished shouldBe false
    animation.totalDuration shouldBe animationFrame1.duration + animationFrame2.duration

  it should "go to another frame" in:
    animation.goToFrame(1)
    animation.currentFrame shouldBe animationFrame2
    animation.currentFrameIndex shouldBe 1
    animation.isFinished shouldBe false

  it should "update state within a loop" in:
    animation.update(0.05)
    animation.currentFrame shouldBe animationFrame1
    animation.isFinished shouldBe false
    animation.currentFrameIndex shouldBe 0

    animation.update(0.05)
    animation.isFinished shouldBe false
    animation.currentFrame shouldBe animationFrame2
    animation.currentFrameIndex shouldBe 1

    animation.update(0.2)
    animation.currentFrame shouldBe animationFrame1
    animation.isFinished shouldBe false
    animation.currentFrameIndex shouldBe 0

  it should "finish if not a loop animation" in:
    val animation = Animation(Seq(animationFrame1, animationFrame2), false)
    animation.update(animation.totalDuration)
    animation.isFinished shouldBe true
    animation.currentFrame shouldBe animationFrame2
    animation.currentFrameIndex shouldBe 1

  it should "be restarted" in:
    val animation = Animation(Seq(animationFrame1, animationFrame2), false)
    animation.update(animation.totalDuration)
    animation.reset()

    animation.isFinished shouldBe false
    animation.currentFrame shouldBe animationFrame1
    animation.currentFrameIndex shouldBe 0

  it should "be possible to create a copy of the animation" in:
    val animation2 = animation.copy()
    animation2.frames shouldBe animation.frames
    animation2.loop shouldBe animation.loop
    animation2.currentFrame shouldBe animation.currentFrame
    animation2.currentFrameIndex shouldBe animation.currentFrameIndex
    animation2.isFinished shouldBe animation.isFinished
    animation2 shouldNot be(animation)

  it should "be possible to create an animation from a uniform sequence of frames" in:
    val animation =
      Animation.uniform(Seq("epic-crocodile.png", "epic-crocodile_0.png"), 0.1, false)

    val animation2 =
      Animation.uniform(Seq("epic-crocodile.png"), 0.1)

    animation.frames shouldBe Seq(AnimationFrame("epic-crocodile.png", 0.1), AnimationFrame("epic-crocodile_0.png", 0.1))
    animation.currentFrame shouldBe AnimationFrame("epic-crocodile.png", 0.1)
    animation.loop shouldBe false

    animation2.loop shouldBe true

  it should "be possible to create an animation from a pattern of frames" in:
    val animation =
      Animation.fromPattern("epic-crocodile", "png", 2, 0.1, false)

    val animation2 =
      Animation.fromPattern("epic-crocodile", "png", 2, 0.1, startIndex = 1)

    animation.loop shouldBe false
    animation.currentFrameIndex shouldBe 0
    animation.currentFrame shouldBe AnimationFrame("epic-crocodile_0.png", 0.1)
    animation.frames shouldBe Seq(AnimationFrame("epic-crocodile_0.png", 0.1), AnimationFrame("epic-crocodile_1.png", 0.1))

    animation2.loop shouldBe true
    animation2.currentFrameIndex shouldBe 0
    animation2.currentFrame shouldBe AnimationFrame("epic-crocodile_1.png", 0.1)
    animation2.frames shouldBe Seq(AnimationFrame("epic-crocodile_1.png", 0.1), AnimationFrame("epic-crocodile_2.png", 0.1))

  "AnimatedImage" should "be created" in:
    val image = Animations.animatedImage(animation, 1, 1)
    image.animation shouldBe animation
    image.elementWidth shouldBe 1
    image.elementHeight shouldBe 1
    image shouldBe an[BaseGameElement]
