import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Dimensions2D.PositionFollower
import TestUtils.testOnStart

class PositionFollowerTests extends AnyFlatSpec:
  val positionable = new Behaviour with Positionable(1, 4)
  val offsetX: Double = 10
  val offsetY: Double = 10

  val positionableFollower = new Behaviour
    with PositionFollower(position = positionable, offset = (offsetX, offsetY))
    with Positionable(0, 0)

  "positionFollower" should "follow the position of the Positionable after onInit, with an Offset" in:
    val engine = Engine(new IO {}, Storage())

    engine.testOnStart(() => Seq(positionableFollower, positionable)):
      positionableFollower.x shouldBe positionable.x + offsetX
      positionableFollower.y shouldBe positionable.y + offsetY
