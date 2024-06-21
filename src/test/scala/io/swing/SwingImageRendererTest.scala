import SwingRendererTestUtilities.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

object SwingImageRendererTest:
  @main def testSwingRendererImagePlacement(): Unit =
    val img: String = "epic-crocodile.png"
    val w: Double = 1
    val h: Double = 1
    testSwingRendererPlacement(
      centered = imageRenderer(img, w, h, offset = (0, 0), position = (0, 0)),
      topLeft = imageRenderer(img, w, h, offset = (0, 0), position = (-2 + w / 2, 2 - h / 2)),
      topRight = imageRenderer(img, w, h, offset = (2 - w / 2, 2 - h / 2), position = (0, 0))
    )

  @main def testSwingRendererImage(): Unit =
    // it should display an image
    testSwingRenderer:
      imageRenderer("epic-crocodile.png", width = 3, height = 3, offset = (0, 0), position = (0, 0))

class SwingImageRendererTest extends AnyFlatSpec:

  "Swing Image" should "be initialized correctly" in:
    val image = SwingRendererTestUtilities.imageRenderer("epic-crocodile.png", width = 3, height = 3, offset = (0, 0))
    image.imageWidth shouldBe 3
    image.imageHeight shouldBe 3
    image.renderOffset shouldBe (0, 0)
    image.image shouldNot be theSameInstanceAs null

  "Swing Image" should "not be initialized with negative sizes" in :
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.imageRenderer("epic-crocodile.png", width = 0, height = 0)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.imageRenderer("epic-crocodile.png", width = -4, height = 5)
    }
    an [IllegalArgumentException] shouldBe thrownBy {
      SwingRendererTestUtilities.imageRenderer("epic-crocodile.png", width = 2, height = -6)
    }

  "Swing Image" should "be able to change its properties" in :
    val image = SwingRendererTestUtilities.imageRenderer("epic-crocodile.png", width = 3, height = 3, offset = (0, 0))
    image.imageWidth = 2
    image.imageWidth shouldBe 2
    image.imageHeight = 1.5
    image.imageHeight shouldBe 1.5
    image.renderOffset = (1, 9)
    image.renderOffset shouldBe(1, 9)

  "Swing Rectangle" should "not be able to change its properties to invalid values" in :
    val image = SwingRendererTestUtilities.imageRenderer("epic-crocodile.png", width = 3, height = 3, offset = (0, 0))
    an[IllegalArgumentException] shouldBe thrownBy {
      image.imageWidth = -2
    }
    an[IllegalArgumentException] shouldBe thrownBy {
      image.imageHeight = 0
    }