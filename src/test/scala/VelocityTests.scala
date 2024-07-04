import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Physics2D.Velocity
import TestUtils.testOnGameloopEvents

class VelocityTests extends AnyFlatSpec:
    val velocity = new Behaviour with Velocity with Positionable

    "velocity" should "be created with parameters" in:
        new Behaviour with Velocity(10, 5) with Positionable

    it should "be created with default values" in:
        velocity.velocity shouldBe (0, 0)

    it should "be able to change its velocity" in:
        velocity.velocity = (3, -5)
        velocity.velocity shouldBe (3, -5)

    it should "update the position of the behaviour" in:
        velocity.velocity = (2, 3)

        var x: Double = 0
        var y: Double = 0
        velocity.x = x
        velocity.y = y

        val engine = Engine(new IO {}, Storage())
        val scene = () => Seq(velocity)
        engine.testOnGameloopEvents(scene, nFramesToRun = 2):
            _.onLateUpdate:
                velocity.x shouldBe x + velocity.velocity._1 * engine.deltaTimeSeconds
                velocity.y shouldBe y + velocity.velocity._2 * engine.deltaTimeSeconds
            .onEarlyUpdate:
                x = velocity.x
                y = velocity.y