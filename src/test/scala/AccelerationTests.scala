import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Dimensions2D.Positionable
import Physics2D.Velocity
import Physics2D.Acceleration
import TestUtils.testOnGameloopEvents

class AccelerationTests extends AnyFlatSpec:
    val acceleration = new Behaviour with Acceleration with Velocity with Positionable

    "acceleration" should "be created with parameters" in:
        new Behaviour with Acceleration(2, 2) with Velocity with Positionable

    it should "be created with default values" in:
        acceleration.acceleration shouldBe (0, 0)

    it should "be able to change its acceleration" in:
        acceleration.acceleration = (3, 1)
        acceleration.acceleration shouldBe (3, 1)

    it should "throw an IllegalArgumentException if acceleration is null" in:
        an[IllegalArgumentException] shouldBe thrownBy:
            acceleration.acceleration = null

        an[IllegalArgumentException] shouldBe thrownBy:
            new Behaviour with Acceleration(null) with Velocity with Positionable

    it should "update the velocity of the behaviour" in:
        acceleration.acceleration = (2, 3)
        var velX: Double = 0
        var velY: Double = 0
        acceleration.velocity = (velX, velY)

        val engine = Engine(new IO {}, Storage())
        val scene = () => Seq(acceleration)
        
        engine.testOnGameloopEvents(scene, nFramesToRun = 2):
            _.onUpdate:
                acceleration.velocity._1 shouldBe velX + acceleration.acceleration._1 * engine.deltaTimeSeconds
                acceleration.velocity._2 shouldBe velY + acceleration.acceleration._2 * engine.deltaTimeSeconds
            .onLateUpdate:
                velX = acceleration.velocity._1
                velY = acceleration.velocity._2