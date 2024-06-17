import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*

class BehavioursTest extends AnyFlatSpec:
    val positionB = new Behaviour() with PositionB()

    "PositionB" should "have 0 as x and y as default values" in:
        positionB.x shouldBe 0
        positionB.y shouldBe 0

    it should "be created with other values other than 0 as x and y" in:
        val positionB2: PositionB = new Behaviour() with PositionB(5.5, 3)
        positionB2.x shouldBe 5.5
        positionB2.y shouldBe 3

        val positionB3: PositionB = new Behaviour() with PositionB(2, 7.8)
        positionB3.x shouldBe 2
        positionB3.y shouldBe 7.8
    
    it should "change x and y" in:
        positionB.x = 3
        positionB.y = -5
        positionB.x shouldBe 3
        positionB.y shouldBe -5

        positionB.x = -7
        positionB.y = 10
        positionB.x shouldBe -7
        positionB.y shouldBe 10