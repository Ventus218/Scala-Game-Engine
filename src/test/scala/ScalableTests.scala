import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import org.scalatest.BeforeAndAfterEach
import Dimensions2D.Scalable

class ScalableTests extends AnyFlatSpec with BeforeAndAfterEach:
    private val scaleX: Double = 0.5
    private val scaleY: Double = 2

    private val singleScalable = new Behaviour with Scalable(scaleX)
    private val scalable = new Behaviour with Scalable(scaleX, scaleY)

    override protected def beforeEach(): Unit = 
        singleScalable.scale = scaleX
        scalable.scale = (scaleX, scaleY)

    "single scalable" should "have right scale" in:
        singleScalable.scale shouldBe scaleX

    it should "change scale" in:
        singleScalable.scale = 5
        singleScalable.scale shouldBe 5

        singleScalable.scale = 10
        singleScalable.scale shouldBe 10

    it should "not accept negative values or zero" in:
        singleScalable.scale = -2
        singleScalable.scale shouldBe scaleX

        an[IllegalArgumentException] shouldBe thrownBy:
            new Behaviour with Scalable(0d)

    "scalable" should "have right scaleX and scaleY" in:
        scalable.scale._1 shouldBe scaleX
        scalable.scale._2 shouldBe scaleY

    it should "change scaleX and scaleY" in:
        scalable.scale = (10.5, 2)
        scalable.scale._1 shouldBe 10.5
        scalable.scale._2 shouldBe 2

        scalable.scale = (5, 0.4)

        scalable.scale._1 shouldBe 5
        scalable.scale._2 shouldBe 0.4

    it should "not accept negative values or zero" in:
        scalable.scale = (-1, 0)

        scalable.scale._1 shouldBe scaleX
        scalable.scale._2 shouldBe scaleY

        an[IllegalArgumentException] shouldBe thrownBy:
            new Behaviour with Scalable(-5d, 4d)