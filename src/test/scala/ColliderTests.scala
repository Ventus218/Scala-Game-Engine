import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*

class ColliderTests extends AnyFlatSpec:
  
  val collider = new Behaviour with Collider with Positionable with Dimensionable(5, 3)

  "collider" should "initially have its width and height set to dimensionable width and height" in:
    collider.cWidth shouldBe collider.width
    collider.cHeight shouldBe collider.height

    collider.width = 10
    collider.cWidth shouldBe 10

    collider.height = 20
    collider.cHeight shouldBe 20

  it should "be able to create a collider with different width and height" in:
    val collider2 = new Behaviour with Collider(4, 2) with Positionable with Dimensionable(5, 3)
    collider2.cWidth shouldBe 4
    collider2.cHeight shouldBe 2

    collider2.width = 10
    collider2 shouldNot be(10)