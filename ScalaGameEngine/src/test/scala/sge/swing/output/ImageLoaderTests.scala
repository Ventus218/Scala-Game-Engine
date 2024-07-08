package sge.swing.output

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.output.Images.*
class ImageLoaderTests extends AnyFlatSpec:

  "ImageLoader" should "load correctly an image" in:
    val image = ImageLoader.load("epic-crocodile.png")

    image shouldBe a [java.awt.Image]

  it should "throw an error if the image is not in the resource folder" in:
    an[IllegalArgumentException] shouldBe thrownBy {
      ImageLoader.load("not-exists.png")
    }

  it should "cache all the loaded images" in:
    val image1 = ImageLoader.load("epic-crocodile.png")
    val image2 = ImageLoader.load("epic-crocodile.png")
    
    image1 shouldEqual image2
