import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import org.scalatest.BeforeAndAfterEach
import Dimensions2D.Scalable
import Dimensions2D.Vector.*
import Dimensions2D.SingleScalable

class ScalableTests extends AnyFlatSpec with BeforeAndAfterEach:
  private val scaleX: Double = 0.5
  private val scaleY: Double = 2

  private val singleScalable = new Behaviour with SingleScalable(scaleX)
  private val scalable = new Behaviour with Scalable(scaleX, scaleY)

  override protected def beforeEach(): Unit =
    singleScalable.scale = scaleX
    scalable.scaleX = scaleX
    scalable.scaleY = scaleY

  "single scalable" should "have right scale" in:
    singleScalable.scale shouldBe scaleX

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

  "scalable" should "have right scaleX and scaleY" in:
    scalable.scaleX shouldBe scaleX
    scalable.scaleY shouldBe scaleY

  it should "change scaleX and scaleY" in:
    scalable.scaleX = 10.5 
    scalable.scaleY = 2
    scalable.scaleX shouldBe 10.5
    scalable.scaleY shouldBe 2

    scalable.scaleX = 5
    scalable.scaleY = 0.4

    scalable.scaleX shouldBe 5
    scalable.scaleY shouldBe 0.4

  it should "not accept negative values or zero" in:
    an[IllegalArgumentException] shouldBe thrownBy:
      scalable.scaleX = -1.0

    an[IllegalArgumentException] shouldBe thrownBy:
      scalable.scaleY = 0

    an[IllegalArgumentException] shouldBe thrownBy:
      new Behaviour with Scalable(-5d, 4d)
