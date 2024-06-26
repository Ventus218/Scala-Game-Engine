import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.*
import org.scalatest.BeforeAndAfterEach
import Physics2D.RectCollider

class ColliderRectTests extends AnyFlatSpec with BeforeAndAfterEach:
  val collider = new Behaviour with RectCollider(5, 3) with Positionable with Scalable
  val collider2 = new Behaviour with RectCollider(1, 2) with Positionable with Scalable

  override protected def beforeEach(): Unit = 
    collider.colliderWidth = 5
    collider.colliderHeight = 3
    collider.x = 0
    collider.y = 0
    collider2.x = 2
    collider2.y = -5

  "collider" should "initially have its width and height" in:
    collider.colliderWidth shouldBe 5
    collider.colliderHeight shouldBe 3

  it should "throws an exception if initially its width and height are less or equal to zero" in:
    assertThrows[IllegalArgumentException]:
      new Behaviour with RectCollider(-1, 1) with Positionable with Scalable

    assertThrows[IllegalArgumentException]:
      new Behaviour with RectCollider(5, 0) with Positionable with Scalable

  it should "be able to change its width and height but not accept negative values" in:
    collider.colliderWidth = 10
    collider.colliderHeight = 20

    collider.colliderWidth shouldBe 10
    collider.colliderHeight shouldBe 20

    collider.colliderWidth = 30
    collider.colliderHeight = -7

    collider.colliderWidth shouldBe 30
    collider.colliderHeight shouldBe 20

    collider.colliderWidth = 0
    collider.colliderWidth shouldBe 30
    collider.colliderHeight = 0
    collider.colliderHeight shouldBe 20

  it should "collide with another collider on top" in:
    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

    collider2.x = 2
    collider2.y = -2

    collider.collides(collider2) shouldBe true
    collider2.collides(collider) shouldBe true

    collider2.y = -3

    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

  it should "collide with another collider on left" in:
    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

    collider2.y = 0
    collider2.x = -1

    collider.collides(collider2) shouldBe true
    collider2.collides(collider) shouldBe true

    collider2.x = -2

    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

  it should "collide with another collider on bottom" in:
    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

    collider2.y = 3
    collider2.x = 2

    collider.collides(collider2) shouldBe true
    collider2.collides(collider) shouldBe true

    collider2.y = 4

    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

  it should "collide with another collider on right" in:
    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

    collider2.y = 0
    collider2.x = 5

    collider.collides(collider2) shouldBe true
    collider2.collides(collider) shouldBe true

    collider2.x = 6

    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false
    
  it should "scale its dimension based on Scalable X and Y" in:
    collider.scaleX = 2
    collider.colliderWidth shouldBe 10

    collider.scaleX = 3
    collider.colliderWidth shouldBe 15

    collider.scaleY = 3
    collider.colliderHeight shouldBe 9