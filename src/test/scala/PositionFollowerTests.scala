import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Dimensions2D.PositionFollower
import Dimensions2D.Vector.*
import TestUtils.*

class PositionFollowerTests extends AnyFlatSpec:
  val positionablePosition: Vector = (1, 4)

  val positionable = new Behaviour with Positionable(positionablePosition):
    override def onEarlyUpdate: Engine => Unit =
      engine =>
        super.onEarlyUpdate(engine)
        this.position = (5, 7)

  val offset: Vector = (10, 10)

  val positionableFollower = new Behaviour
    with PositionFollower(
      followed = positionable,
      positionOffset = offset
    )
    with Positionable(0, 0)

  val scene = () => Seq(positionableFollower, positionable)

  val engine = Engine(new IO {}, Storage())

  "positionFollower" should "follow the position of the Positionable after onInit, with an Offset" in:
    engine.testOnGameloopEvents(scene):
      _.onStart:
        positionableFollower.position shouldBe positionable.position + offset

  it should "follow the position of the Positionable after onLateUpdate, with an Offset" in:
    positionable.position = positionablePosition

    engine.testOnGameloopEvents(scene):
      _.onUpdate:
        positionableFollower.position shouldBe positionablePosition + offset
        positionable.position should not be positionablePosition

    engine.testOnGameloopEvents(scene):
      _.onEarlyUpdate:
        positionableFollower.position shouldBe positionable.position + offset

  it should "be able to change its offset" in:
    positionableFollower.positionOffset = (0, 20)
    positionableFollower.positionOffset shouldBe (0, 20)
