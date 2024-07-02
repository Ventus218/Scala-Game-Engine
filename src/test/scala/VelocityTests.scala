import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Physics2D.Velocity

class VelocityTests extends AnyFlatSpec:
    val velocity = new Behaviour with Velocity with Positionable

    "velocity" should "be created with parameters" in:
        new Behaviour with Velocity(10, 5) with Positionable

    it should "be created with default values" in:
        velocity.velocity shouldBe (0, 0)

    it should "be able to change its velocity" in:
        velocity.velocity = (3, -5)
        velocity.velocity shouldBe (3, -5)

    it should "throw an excpetion if null is passed as velocity" in:
        an[IllegalArgumentException] shouldBe thrownBy:
            velocity.velocity = null

        an[IllegalArgumentException] shouldBe thrownBy:
            new Behaviour with Velocity(null) with Positionable

    