import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Dimensions2D.Scalable
import Physics2D.CircleCollider
import Physics2D.RectCollider
import Dimensions2D.SingleScalable

class CircleColliderTests extends AnyFlatSpec with BeforeAndAfterEach:
  val collider = new Behaviour
    with CircleCollider(r = 2)
    with Positionable
    with SingleScalable

  val collider2 = new Behaviour
    with RectCollider(2, 2)
    with Positionable(-10, -10)
    with Scalable

  override protected def beforeEach(): Unit =
    collider.radius = 2
    collider.x = 2
    collider.y = 2

  it should "initially have its radius" in:
    collider.radius shouldBe 2

    val collider2 = new Behaviour
      with CircleCollider(r = 3.5)
      with Positionable
      with SingleScalable

    collider2.radius shouldBe 3.5

  it should "throws an exception if initially its radius is less or equal to zero" in:
    assertThrows[IllegalArgumentException]:
      new Behaviour with CircleCollider(r = 0) with Positionable with SingleScalable

    assertThrows[IllegalArgumentException]:
      new Behaviour with CircleCollider(r = -5) with Positionable with SingleScalable

  it should "be able to change its radius but not accept negative or zero values" in:
    collider.radius = 20
    collider.radius shouldBe 20

    collider.radius = 10
    collider.radius shouldBe 10

    collider.radius = 0
    collider.radius shouldBe 10

    collider.radius = -15
    collider.radius shouldBe 10

  it should "collides with a Rectangle Collider on its top" in:
    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

    collider2.x = 2
    collider2.y = 5

    collider.collides(collider2) shouldBe true
    collider2.collides(collider) shouldBe true

    collider2.y = 6

    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

  it should "collides with a Rectangle Collider on its right" in:
    collider2.x = -1
    collider2.y = 2

    collider.collides(collider2) shouldBe true

    collider2.x = -2

    collider.collides(collider2) shouldBe false

  it should "collides with a Rectangle Collider on it bottom" in:
    collider2.x = 2
    collider2.y = -1

    collider.collides(collider2) shouldBe true

    collider2.y = -2

    collider.collides(collider2) shouldBe false

  it should "collides with a Rectangle Collider on it left" in:
    collider2.x = 5
    collider2.y = 2

    collider.collides(collider2) shouldBe true

    collider2.x = 6

    collider.collides(collider2) shouldBe false

  it should "collides with a Rectangle Collider also in diagonal" in:
    collider2.x = 5
    collider2.y = 5

    collider.collides(collider2) shouldBe false

    collider2.x = 3 + Math.sqrt(2)
    collider2.y = 3 + Math.sqrt(2)

    collider.collides(collider2) shouldBe true

    collider2.x = collider2.x + 0.1
    collider2.y = collider2.y + 0.1

    collider.collides(collider2) shouldBe false

  it should "collides with a Circle Collider" in:
    val collider2 = new Behaviour
      with CircleCollider(2)
      with Positionable(-6, -6)
      with SingleScalable

    collider.collides(collider2) shouldBe false
    collider2.collides(collider) shouldBe false

    collider2.x = 6
    collider2.y = 2

    collider.collides(collider2) shouldBe true
    collider2.collides(collider) shouldBe true

    collider2.x = 2 + 2 * Math.sqrt(2)
    collider2.y = 2 + 2 * Math.sqrt(2)

    collider.collides(collider2) shouldBe true

    collider2.x = collider2.x + 0.1
    collider2.y = collider2.y + 0.1

    collider.collides(collider2) shouldBe false

    it should "scale its radius based on SingleScalable" in:
        collider.scale = 2
        collider.radius shouldBe 4

        collider.scale = 3
        collider.radius shouldBe 6