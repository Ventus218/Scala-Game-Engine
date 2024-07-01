import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Dimensions2D.PositionFollower
import TestUtils.testOnStart
import TestUtils.testOnEarlyUpdate
import TestUtils.testOnUpdate

class PositionFollowerTests extends AnyFlatSpec:
  val positionableX: Double = 1
  val positionableY: Double = 4

  val positionable = new Behaviour
    with Positionable(positionableX, positionableY):
    override def onEarlyUpdate: Engine => Unit =
      engine =>
        super.onEarlyUpdate(engine)
        this.x = 5
        this.y = 7

  val offsetX: Double = 10
  val offsetY: Double = 10

  val positionableFollower = new Behaviour
    with PositionFollower(followed = positionable, offset = (offsetX, offsetY))
    with Positionable(0, 0)

  val scene = () => Seq(positionableFollower, positionable)

  val engine = Engine(new IO {}, Storage())

  "positionFollower" should "follow the position of the Positionable after onInit, with an Offset" in:
    engine.testOnStart(scene):
      positionableFollower.x shouldBe positionable.x + offsetX
      positionableFollower.y shouldBe positionable.y + offsetY

  it should "follow the position of the Positionable after onLateUpdate, with an Offset" in:
    positionable.x = positionableX
    positionable.y = positionableY

    engine.testOnUpdate(scene):
      positionableFollower.x shouldBe positionableX + offsetX
      positionableFollower.y shouldBe positionableY + offsetY
      positionable.x should not be positionableX
      positionable.y should not be positionableY

    engine.testOnEarlyUpdate(scene):
      positionableFollower.x shouldBe positionable.x + offsetX
      positionableFollower.y shouldBe positionable.y + offsetY

  it should "be able to change its offset" in:
    positionableFollower.offset = (0, 20)
    positionableFollower.offset shouldBe (0, 20)
