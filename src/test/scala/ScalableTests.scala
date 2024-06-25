import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import org.scalatest.BeforeAndAfterEach

class ScalableTests extends AnyFlatSpec with BeforeAndAfterEach:
    private val scaleX: Long = 1
    private val scaleY: Long = 5

    private val dimensionable = new Behaviour with Scalable(scaleX, scaleY)

    override protected def beforeEach(): Unit = 
        dimensionable.scaleX = scaleX
        dimensionable.scaleY = scaleY

    "dimensionable" should "have right scaleX and scaleY" in:
        dimensionable.scaleX shouldBe scaleX
        dimensionable.scaleY shouldBe scaleY

    it should "change scaleX and scaleY" in:
        dimensionable.scaleX = 10.5
        dimensionable.scaleY = 2
        dimensionable.scaleX shouldBe 10.5
        dimensionable.scaleY shouldBe 2

        dimensionable.scaleX = 5
        dimensionable.scaleX shouldBe 5
        dimensionable.scaleY = 0.4
        dimensionable.scaleY shouldBe 0.4

    it should "not accept negative values" in:
        dimensionable.scaleX = -1
        dimensionable.scaleY = -10
        dimensionable.scaleX shouldBe scaleX
        dimensionable.scaleY shouldBe scaleY

        val dimensionable2 = new Behaviour with Scalable(-5, -2)
        dimensionable2.scaleX shouldBe 0
        dimensionable2.scaleY shouldBe 0