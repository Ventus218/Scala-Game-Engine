import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Vector.*

class VectorTests extends AnyFlatSpec:
  "Vector" should "provide up versor" in:
    Versor.up shouldBe (0, 1)

  it should "provide down versor" in:
    Versor.down shouldBe (0, -1)

  it should "provide left versor" in:
    Versor.left shouldBe (-1, 0)

  it should "provide right versor" in:
    Versor.right shouldBe (1, 0)
  
  it should "provide a \"x\" accessor" in:
    Versor.up.x shouldBe 0
    Versor.right.x shouldBe 1

  it should "provide a \"y\" accessor" in:
    Versor.up.y shouldBe 1
    Versor.right.y shouldBe 0

  it should "provide a scalar product (*)" in:
    Versor.up * 3 shouldBe (0, 3)
    Versor.up * -3 shouldBe (0, -3)
    (2d, -3d) * 3 shouldBe (6, -9)

  it should "provide a scalar division (/)" in:
    Versor.up / 3 shouldBe (0, 1d / 3)
    Versor.up / -3 shouldBe (0, 1d / -3)
    (2d, -3d) / 3 shouldBe (2d / 3, -3d / 3)

  it should "provide a sum (+) operation with another one" in:
    Versor.up + Versor.up shouldBe (0, 2)
    Versor.up + Versor.down shouldBe (0, 0)
    (2d, -3d) + (2, 3) shouldBe (4, 0)

  it should "provide a subtraction (-) operation with another one" in:
    Versor.up - Versor.up shouldBe (0, 0)
    Versor.up - Versor.down shouldBe (0, 2)
    (2d, -3d) - (2, 3) shouldBe (0, -6)

    Versor.up - Versor.right shouldBe Versor.up + (Versor.right * -1)
