import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*

class ColliderTests extends AnyFlatSpec:
  "collider" should "be a mixin of Behaviour" in:
      val collider = new Behaviour with Collider with Positionable with Dimensionable(0, 0)
