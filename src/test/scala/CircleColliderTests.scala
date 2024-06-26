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
