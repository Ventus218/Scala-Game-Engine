import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import org.scalatest.BeforeAndAfterEach

class DimensionableTests extends AnyFlatSpec with BeforeAndAfterEach:
    private val width: Long = 1
    private val height: Long = 5

    private val dimensionable = new Behaviour with Dimensionable(width, height)

    override protected def beforeEach(): Unit = 
        dimensionable.width = width
        dimensionable.height = height

    "dimensionable" should "have right width and height" in:
        dimensionable.width shouldBe width
        dimensionable.height shouldBe height

    it should "change width and height" in:
        dimensionable.width = 10.5
        dimensionable.height = 2
        dimensionable.width shouldBe 10.5
        dimensionable.height shouldBe 2

        dimensionable.width = 5
        dimensionable.width shouldBe 5
        dimensionable.height = 0.4
        dimensionable.height shouldBe 0.4

    it should "not accept negative values" in:
        dimensionable.width = -1
        dimensionable.height = -10
        dimensionable.width shouldBe width
        dimensionable.height shouldBe height

        val dimensionable2 = new Behaviour with Dimensionable(-5, -2)
        dimensionable2.width shouldBe 0
        dimensionable2.height shouldBe 0