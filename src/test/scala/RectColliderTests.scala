import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.*
import org.scalatest.BeforeAndAfterEach
import Physics2D.RectCollider
import Dimensions2D.Vector.*

class RectColliderTests extends AnyFlatSpec with BeforeAndAfterEach:
  val collider = new Behaviour
    with RectCollider(5, 3)
    with Positionable
    with Scalable(1d, 1d)

  val collider2 = new Behaviour
    with RectCollider(1, 2)
    with Positionable
    with Scalable(1d, 1d)

  override protected def beforeEach(): Unit =
    collider.colliderWidth = 5
    collider.colliderHeight = 3
    collider.position = (2, 1)

  "collider" should "initially have its width and height" in:
    collider.colliderWidth shouldBe 5
    collider.colliderHeight shouldBe 3

  it should "throws an exception if initially its width and height are less or equal to zero" in:
    assertThrows[IllegalArgumentException]:
      new Behaviour
        with RectCollider(-1, 1)
        with Positionable
        with Scalable(1d, 1d)

    assertThrows[IllegalArgumentException]:
      new Behaviour
        with RectCollider(5, 0)
        with Positionable
        with Scalable(1d, 1d)

  it should "be able to change its width and height but not accept negative or zero values" in:
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
    collider2.position = (2, -5)

    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

    collider2.position = (2, -1.5)

    collider.collides(collider2) shouldBe true
    collider2.collides(collider) shouldBe true

    collider2.position = collider2.position.setY(-2)

    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

  it should "collide with another collider on left" in:
    collider2.position = (-1, -1)

    collider.collides(collider2) shouldBe true

    collider2.position = collider2.position.setX(-2)

    collider.collides(collider2) shouldBe false

  it should "collide with another collider on bottom" in:
    collider2.position = (2, 3.5)

    collider.collides(collider2) shouldBe true

    collider2.position = collider2.position.setY(5)

    collider.collides(collider2) shouldBe false

  it should "collide with another collider on right" in:
    collider2.position = (5, 1)

    collider.collides(collider2) shouldBe true

    collider2.position = collider2.position.setX(6)

    collider.collides(collider2) shouldBe false

  it should "scale its dimension based on Scalable1 X and Y" in:
    collider.scaleWidth = 2
    collider.scaleHeight = 3

    collider.colliderWidth shouldBe 10
    collider.colliderHeight shouldBe 9

  it should "collides using the scaled dimensions" in:
    collider.position = (0, 0)
    collider.colliderWidth = 2
    collider.scaleWidth = 1
    collider.scaleHeight = 1

    collider2.position = (5, 0)
    collider2.colliderWidth = 4
    collider2.scaleWidth = 2
    collider2.scaleHeight = 1

    collider.collides(collider2) shouldBe true

    collider2.position = collider2.position.setX(6)

    collider.collides(collider2) shouldBe false
