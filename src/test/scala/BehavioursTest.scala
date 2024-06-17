import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class BehavioursTest extends AnyFlatSpec:
    val positionB = new Behaviour() with PositionB()

    "positionB" should "have 0 as x and y as default values" in:
        positionB.x shouldBe 0
        positionB.y shouldBe 0
