package sge.core.metrics

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Vector2D.*

class VectorTests extends AnyFlatSpec:
  "Vector2D" should "provide up versor" in:
    Versor2D.up shouldBe (0, 1)

  it should "provide down versor" in:
    Versor2D.down shouldBe (0, -1)

  it should "provide left versor" in:
    Versor2D.left shouldBe (-1, 0)

  it should "provide right versor" in:
    Versor2D.right shouldBe (1, 0)

  it should "provide x versor" in:
    Versor2D.x shouldBe Versor2D.right

  it should "provide y versor" in:
    Versor2D.y shouldBe Versor2D.up

  it should "provide identity vector" in:
    Vector2D.identity shouldBe (1, 1)

  it should "provide a \"x\" accessor" in:
    Versor2D.up.x shouldBe 0
    Versor2D.right.x shouldBe 1

  it should "provide a \"y\" accessor" in:
    Versor2D.up.y shouldBe 1
    Versor2D.right.y shouldBe 0

  it should "provide a \"setX\" utility" in:
    Versor2D.right.setX(3) shouldBe (3, 0)
    Versor2D.right.setX(0) shouldBe (0, 0)
    Versor2D.up.setX(1) shouldBe (1, 1)

  it should "provide a \"setY\" utility" in:
    Versor2D.up.setY(3) shouldBe (0, 3)
    Versor2D.up.setY(0) shouldBe (0, 0)
    Versor2D.right.setY(1) shouldBe (1, 1)

  it should "provide a scalar product (*)" in:
    Versor2D.up * 3 shouldBe (0, 3)
    Versor2D.up * -3 shouldBe (0, -3)
    (2d, -3d) * 3 shouldBe (6, -9)

  it should "provide a scalar division (/)" in:
    Versor2D.up / 3 shouldBe (0, 1d / 3)
    Versor2D.up / -3 shouldBe (0, 1d / -3)
    (2d, -3d) / 3 shouldBe (2d / 3, -3d / 3)

  it should "provide a sum (+) operation with another one" in:
    Versor2D.up + Versor2D.up shouldBe (0, 2)
    Versor2D.up + Versor2D.down shouldBe (0, 0)
    (2d, -3d) + (2, 3) shouldBe (4, 0)

  it should "provide a subtraction (-) operation with another one" in:
    Versor2D.up - Versor2D.up shouldBe (0, 0)
    Versor2D.up - Versor2D.down shouldBe (0, 2)
    (2d, -3d) - (2, 3) shouldBe (0, -6)

    Versor2D.up - Versor2D.right shouldBe Versor2D.up + (Versor2D.right * -1)
