package sge.core.behaviours.dimension2d

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterEach
import sge.core.*
import behaviours.*

class ScalableTests extends AnyFlatSpec with BeforeAndAfterEach:
  private val scaleWidth: Double = 0.5
  private val scaleHeight: Double = 2

  private val singleScalable = new Behaviour with SingleScalable(scaleWidth)
  private val scalable = new Behaviour with Scalable(scaleWidth, scaleHeight)

  override protected def beforeEach(): Unit =
    singleScalable.scale = scaleWidth
    scalable.scaleWidth = scaleWidth
    scalable.scaleHeight = scaleHeight

  "single scalable" should "have right scale" in:
    singleScalable.scale shouldBe scaleWidth

  it should "change scale" in:
    singleScalable.scale = 5
    singleScalable.scale shouldBe 5

    singleScalable.scale = 10
    singleScalable.scale shouldBe 10

  it should "not accept negative values or zero" in:
    an[IllegalArgumentException] shouldBe thrownBy:
      singleScalable.scale = -1.0

    an[IllegalArgumentException] shouldBe thrownBy:
      new Behaviour with SingleScalable(0d)

  "scalable" should "have right scaleWidth and scaleHeight" in:
    scalable.scaleWidth shouldBe scaleWidth
    scalable.scaleHeight shouldBe scaleHeight

  it should "change scaleWidth and scaleHeight" in:
    scalable.scaleWidth = 10.5
    scalable.scaleHeight = 2
    scalable.scaleWidth shouldBe 10.5
    scalable.scaleHeight shouldBe 2

    scalable.scaleWidth = 5
    scalable.scaleHeight = 0.4

    scalable.scaleWidth shouldBe 5
    scalable.scaleHeight shouldBe 0.4

  it should "not accept negative values or zero" in:
    an[IllegalArgumentException] shouldBe thrownBy:
      scalable.scaleWidth = -1.0

    an[IllegalArgumentException] shouldBe thrownBy:
      scalable.scaleHeight = 0

    an[IllegalArgumentException] shouldBe thrownBy:
      new Behaviour with Scalable(-5d, 4d)
