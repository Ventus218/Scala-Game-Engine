import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import org.scalatest.BeforeAndAfterEach

class ScalableTests extends AnyFlatSpec with BeforeAndAfterEach:
    private val scaleX: Double = 0.5
    private val scaleY: Double = 2

    private val scalable = new Behaviour with Scalable(scaleX, scaleY)

    override protected def beforeEach(): Unit = 
        scalable.scaleX = scaleX
        scalable.scaleY = scaleY

    "scalable" should "have right scaleX and scaleY" in:
        scalable.scaleX shouldBe scaleX
        scalable.scaleY shouldBe scaleY

    it should "change scaleX and scaleY" in:
        scalable.scaleX = 10.5
        scalable.scaleY = 2
        scalable.scaleX shouldBe 10.5
        scalable.scaleY shouldBe 2

        scalable.scaleX = 5
        scalable.scaleX shouldBe 5
        scalable.scaleY = 0.4
        scalable.scaleY shouldBe 0.4

    it should "not accept negative values or zero" in:
        scalable.scaleX = -1
        scalable.scaleY = 0
        scalable.scaleX shouldBe scaleX
        scalable.scaleY shouldBe scaleY

        val scalable2 = new Behaviour with Scalable(-5, 0)
        scalable2.scaleX shouldBe 1
        scalable2.scaleY shouldBe 1