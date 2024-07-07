package sge.core.behaviours.physics2d

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterEach
import sge.core.*
import behaviours.dimension2d.*

class CircleColliderTests extends AnyFlatSpec with BeforeAndAfterEach:
  val circle = new Behaviour
    with CircleCollider(r = 2)
    with Positionable
    with SingleScalable(1d)

  val rect = new Behaviour
    with RectCollider(2, 2)
    with Positionable(-10, -10)
    with Scalable(1d, 1d)

  override protected def beforeEach(): Unit =
    circle.radius = 2
    circle.position = (2, 2)

  it should "initially have its radius" in:
    circle.radius shouldBe 2

    val collider2 = new Behaviour
      with CircleCollider(r = 3.5)
      with Positionable
      with SingleScalable(1d)

    collider2.radius shouldBe 3.5

  it should "throws an exception if initially its radius is less or equal to zero" in:
    assertThrows[IllegalArgumentException]:
      new Behaviour
        with CircleCollider(r = 0)
        with Positionable
        with SingleScalable(1d)

    assertThrows[IllegalArgumentException]:
      new Behaviour
        with CircleCollider(r = -5)
        with Positionable
        with SingleScalable(1d)

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

    rect.position = (2, 5)

    circle.collides(rect) shouldBe true
    rect.collides(circle) shouldBe true

    rect.position = rect.position.setY(6)

    circle.collides(rect) shouldBe false
    rect.collides(circle) shouldBe false

  it should "collides with a Rectangle Collider on its right" in:
    rect.position = (-1, 2)

    circle.collides(rect) shouldBe true

    rect.position = rect.position.setX(-2)

    circle.collides(rect) shouldBe false

  it should "collides with a Rectangle Collider on it bottom" in:
    rect.position = (2, -1)

    circle.collides(rect) shouldBe true

    rect.position = rect.position.setY(-2)

    circle.collides(rect) shouldBe false

  it should "collides with a Rectangle Collider on it left" in:
    rect.position = (5, 2)

    circle.collides(rect) shouldBe true

    rect.position = rect.position.setX(6)

    circle.collides(rect) shouldBe false

  it should "collides with a Rectangle Collider also in diagonal" in:
    rect.position = (5, 5)

    circle.collides(rect) shouldBe false

    rect.position = Vector2D.identity * (3 + Math.sqrt(2))

    circle.collides(rect) shouldBe true

    rect.position = rect.position + Vector2D.identity * 0.1

    circle.collides(rect) shouldBe false

  it should "collides with a Circle Collider" in:
    val circle2 = new Behaviour
      with CircleCollider(2)
      with Positionable(-6, -6)
      with SingleScalable(1d)

    circle.collides(circle2) shouldBe false
    circle2.collides(circle) shouldBe false

    circle2.position = (6, 2)

    circle.collides(circle2) shouldBe true
    circle2.collides(circle) shouldBe true

    circle2.position = Vector2D.identity * (2 + 2 * Math.sqrt(2))

    circle.collides(circle2) shouldBe true

    circle2.position = circle2.position + Vector2D.identity * 0.1

    circle.collides(circle2) shouldBe false

  it should "scale its radius based on Scalable1(1d)" in:
    circle.scale = 2
    circle.radius shouldBe 4

    circle.scale = 3
    circle.radius shouldBe 6
