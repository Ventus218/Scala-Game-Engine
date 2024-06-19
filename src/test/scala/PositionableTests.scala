import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*

class PositionableTests extends AnyFlatSpec:
    val positionable = new Behaviour() with Positionable()

    "Positionable" should "have 0 as x and y as default values" in:
        positionable.x shouldBe 0
        positionable.y shouldBe 0

    it should "be created with other values other than 0 as x and y" in:
        val positionable2: Positionable = new Behaviour() with Positionable(5.5, 3)
        positionable2.x shouldBe 5.5
        positionable2.y shouldBe 3

        val positionable3: Positionable = new Behaviour() with Positionable(2, 7.8)
        positionable3.x shouldBe 2
        positionable3.y shouldBe 7.8
    
    it should "change x and y" in:
        positionable.x = 3
        positionable.y = -5
        positionable.x shouldBe 3
        positionable.y shouldBe -5

        positionable.x = -7
        positionable.y = 10
        positionable.x shouldBe -7
        positionable.y shouldBe 10