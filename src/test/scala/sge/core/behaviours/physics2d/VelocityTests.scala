package sge.core.behaviours.physics2d

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.core.*
import EngineUtils.*
import metrics.Vector2D.*
import behaviours.dimension2d.*
import sge.testing.TestUtils.*

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

    var initialPosition: Vector2D = (0, 0)
    velocity.position = initialPosition

    val engine = Engine(new IO {}, Storage())
    val scene = () => Seq(velocity)
    test(engine) on scene runningFor 2 frames so that:
      _.onEarlyUpdate:
        initialPosition = velocity.position
      .onLateUpdate:
        velocity.position shouldBe initialPosition + velocity.velocity * engine.deltaTimeSeconds
