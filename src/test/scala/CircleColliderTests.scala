import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Dimensions2D.Scalable
import Physics2D.CircleCollider

class CircleColliderTests extends AnyFlatSpec with BeforeAndAfterEach:
    val collider = new Behaviour with CircleCollider(r = 1.5) with Positionable with Scalable

    it should "initially have its radius" in:
        collider.radius shouldBe 1.5

        val collider2 = new Behaviour with CircleCollider(r = 3) with Positionable with Scalable
        collider2.radius shouldBe 3

    it should "throws an exception if initially its radius is less or equal to zero" in:
        assertThrows[IllegalArgumentException]:
            new Behaviour with CircleCollider(r = 0) with Positionable with Scalable

        assertThrows[IllegalArgumentException]:
            new Behaviour with CircleCollider(r = -5) with Positionable with Scalable

    it should "be able to change its radius but not accept negative or zero values" in:
        collider.radius = 20
        collider.radius shouldBe 20

        collider.radius = 10
        collider.radius shouldBe 10

        collider.radius = 0
        collider.radius shouldBe 10

        collider.radius = -15
        collider.radius shouldBe 10