import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Dimensions2D.PositionFollower
import Dimensions2D.Vector.*
import TestUtils.*
import org.scalatest.BeforeAndAfterEach

class PositionFollowerTests extends AnyFlatSpec with BeforeAndAfterEach:
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
    with Positionable

  val scene = () => Seq(positionableFollower, positionable)

  var engine = Engine(
    io = new IO() {},
    storage = Storage()
  )

  override protected def beforeEach(): Unit =
    engine = Engine(
      io = new IO() {},
      storage = Storage()
    )

  "positionFollower" should "follow the position of the Positionable after onInit, with an Offset" in:
    test(engine) on scene soThat:
      _.onStart:
        positionableFollower.position shouldBe positionable.position + offset

  it should "follow the position of the Positionable after onLateUpdate, with an Offset" in:
    positionable.position = positionablePosition

    test(engine) on scene soThat:
      _.onUpdate:
        positionableFollower.position shouldBe positionablePosition + offset
        positionable.position should not be positionablePosition

    engine = Engine(
      io = new IO() {},
      storage = Storage()
    )
    test(engine) on scene soThat:
      _.onEarlyUpdate:
        positionableFollower.position shouldBe positionable.position + offset

  it should "be able to change its offset" in:
    positionableFollower.positionOffset = (0, 20)
    positionableFollower.positionOffset shouldBe (0, 20)
