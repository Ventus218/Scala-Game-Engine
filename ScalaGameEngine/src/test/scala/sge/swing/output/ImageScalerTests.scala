package sge.swing.output

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.swing.output.Images.*
class ImageScalerTests extends AnyFlatSpec:
  
  def createScaler: ImageScaler = ImageScaler(path = "epic-crocodile.png")
  
  "ImageScaler" should "resize correctly its image" in:
    val scaler = createScaler
    val awtImage = scaler.resize(20, 30)
    
    awtImage.getHeight(null) shouldBe 30
    awtImage.getWidth(null) shouldBe 20
  
  it should "cache the last resized image" in:
    val scaler = createScaler
    val awtImage1 = scaler.resize(15, 40)
    val awtImage2 = scaler.resize(15, 40)
    
    awtImage1 shouldEqual awtImage2

  it should "compute a new image if not in cache" in:
    val scaler = createScaler
    val awtImage1 = scaler.resize(20, 30)
    val awtImage2 = scaler.resize(40, 50)

    awtImage1 should not equal awtImage2