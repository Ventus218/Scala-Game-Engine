import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Dimensions2D.Scalable
import Physics2D.CircleCollider
import Physics2D.RectCollider

class CircleColliderTests extends AnyFlatSpec with BeforeAndAfterEach:
  val circle = new Behaviour
    with CircleCollider(r = 2)
    with Positionable
    with Scalable(1d)

  val rect = new Behaviour
    with RectCollider(2, 2)
    with Positionable(-10, -10)
    with Scalable((1d, 1d))

  override protected def beforeEach(): Unit =
    circle.radius = 2
    circle.x = 2
    circle.y = 2

  it should "initially have its radius" in:
    circle.radius shouldBe 2

    val collider2 = new Behaviour
      with CircleCollider(r = 3.5)
      with Positionable
      with Scalable(1d)

    collider2.radius shouldBe 3.5

  it should "throws an exception if initially its radius is less or equal to zero" in:
    assertThrows[IllegalArgumentException]:
      new Behaviour
        with CircleCollider(r = 0)
        with Positionable
        with Scalable(1d)

    assertThrows[IllegalArgumentException]:
      new Behaviour
        with CircleCollider(r = -5)
        with Positionable
        with Scalable(1d)

  it should "be able to change its radius but not accept negative or zero values" in:
    circle.radius = 20
    circle.radius shouldBe 20

    circle.radius = 10
    circle.radius shouldBe 10

    circle.radius = 0
    circle.radius shouldBe 10

    circle.radius = -15
    circle.radius shouldBe 10

  it should "collides with a Rectangle Collider on its top" in:
    circle.collides(rect) shouldBe false
    rect.collides(circle) shouldBe false

    rect.x = 2
    rect.y = 5

    circle.collides(rect) shouldBe true
    rect.collides(circle) shouldBe true

    rect.y = 6

    circle.collides(rect) shouldBe false
    rect.collides(circle) shouldBe false

  it should "collides with a Rectangle Collider on its right" in:
    rect.x = -1
    rect.y = 2

    circle.collides(rect) shouldBe true

    rect.x = -2

    circle.collides(rect) shouldBe false

  it should "collides with a Rectangle Collider on it bottom" in:
    rect.x = 2
    rect.y = -1

    circle.collides(rect) shouldBe true

    rect.y = -2

    circle.collides(rect) shouldBe false

  it should "collides with a Rectangle Collider on it left" in:
    rect.x = 5
    rect.y = 2

    circle.collides(rect) shouldBe true

    rect.x = 6

    circle.collides(rect) shouldBe false

  it should "collides with a Rectangle Collider also in diagonal" in:
    rect.x = 5
    rect.y = 5

    circle.collides(rect) shouldBe false

    rect.x = 3 + Math.sqrt(2)
    rect.y = 3 + Math.sqrt(2)

    circle.collides(rect) shouldBe true

    rect.x = rect.x + 0.1
    rect.y = rect.y + 0.1

    circle.collides(rect) shouldBe false

  it should "collides with a Circle Collider" in:
    val circle2 = new Behaviour
      with CircleCollider(2)
      with Positionable(-6, -6)
      with Scalable(1d)

    circle.collides(circle2) shouldBe false
    circle2.collides(circle) shouldBe false

    circle2.x = 6
    circle2.y = 2

    circle.collides(circle2) shouldBe true
    circle2.collides(circle) shouldBe true

    circle2.x = 2 + 2 * Math.sqrt(2)
    circle2.y = 2 + 2 * Math.sqrt(2)

    circle.collides(circle2) shouldBe true

    circle2.x = circle2.x + 0.1
    circle2.y = circle2.y + 0.1

    circle.collides(circle2) shouldBe false

  it should "scale its radius based on Scalable(1d)" in:
    circle.scale = 2
    circle.radius shouldBe 4

    circle.scale = 3
    circle.radius shouldBe 6
