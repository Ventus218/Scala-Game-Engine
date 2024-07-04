import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import Dimensions2D.Positionable

class PositionableTests extends AnyFlatSpec:
  val positionable = new Behaviour() with Positionable()

  "Positionable" should "have 0 as x and y as default values" in:
    positionable.position shouldBe (0, 0)

  it should "be created with other values other than 0 as x and y" in:
    val positionable2: Positionable = new Behaviour() with Positionable(5.5, 3)
    positionable2.position shouldBe (5.5, 3)

    val positionable3: Positionable = new Behaviour() with Positionable(2, 7.8)
    positionable3.position shouldBe (2, 7.8)

  it should "change x and y" in:
    positionable.position = (3, -5)
    positionable.position shouldBe (3, -5)

    positionable.position = (-7, 10)
    positionable.position shouldBe (-7, 10)
