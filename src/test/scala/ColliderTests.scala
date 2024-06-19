import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*

class ColliderTests extends AnyFlatSpec:
  
  val collider = Collider(width = 5, height = 3)()

  "collider" should "initially have its width and height set to dimensionable width and height" in:
    collider.colliderWidth shouldBe 5
    collider.colliderHeight shouldBe 3

  it should "if initially its width and height are negative set them to dimensionable width and height" in:
    val collider = Collider(width = 1, height = 8)(colliderWidth = -5, colliderHeight = 4)
    collider.colliderWidth shouldBe 1
    collider.colliderHeight shouldBe 4

  it should "be able to create a collider with different width and height of dimensionable" in:
    val collider = Collider(width = 5, height = 3)(colliderWidth = 4, colliderHeight = 2)
    collider.colliderWidth shouldBe 4
    collider.colliderHeight shouldBe 2

  it should "be able to change its width and height but not accept negative values" in:
    collider.colliderWidth = 10
    collider.colliderHeight = 20

    collider.colliderWidth shouldBe 10
    collider.colliderHeight shouldBe 20

    collider.colliderWidth = 30
    collider.colliderHeight = -7

    collider.colliderWidth shouldBe 30
    collider.colliderHeight shouldBe 20