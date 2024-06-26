import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import Dimensions2D.Positionable
import Dimensions2D.Scalable
import Physics2D.CircleCollider

class ColliderCircleTests extends AnyFlatSpec with BeforeAndAfterEach:
    "collider" should "be created" in:
        new Behaviour with CircleCollider(r = 1.5) with Positionable with Scalable
