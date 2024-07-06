package sge.core.behaviours.physics2d

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import sge.core.*
import EngineUtils.*
import metrics.Vector2D.*
import behaviours.dimension2d.*
import sge.testing.TestUtils.*

class AccelerationTests extends AnyFlatSpec:
  val acceleration = new Behaviour
    with Acceleration
    with Velocity
    with Positionable

  "acceleration" should "be created with parameters" in:
    new Behaviour with Acceleration(2, 2) with Velocity with Positionable

  it should "be created with default values" in:
    acceleration.acceleration shouldBe (0, 0)

  it should "be able to change its acceleration" in:
    acceleration.acceleration = (3, 1)
    acceleration.acceleration shouldBe (3, 1)

  it should "update the velocity of the behaviour" in:
    acceleration.acceleration = (2, 3)

    var initialVelocity: Vector2D = (0, 0)
    acceleration.velocity = initialVelocity

    val engine = Engine(new IO {}, Storage())
    val scene = () => Seq(acceleration)

    test(engine) on scene runningFor 2 frames so that:
      _.onUpdate:
        acceleration.velocity shouldBe initialVelocity + acceleration.acceleration * engine.deltaTimeSeconds
      .onLateUpdate:
        initialVelocity = acceleration.velocity
