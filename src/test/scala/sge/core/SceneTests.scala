package sge.core

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import behaviours.Identifiable
import BehaviourUtils.*
import mocks.*

class SceneTests extends AnyFlatSpec:
  private def makeMockWithId(id: String) =
    new GameObjectMock with Identifiable(id)

  val scene: Scene = () =>
    Set(
      makeMockWithId("1"),
      makeMockWithId("2"),
      makeMockWithId("3")
    )

  "Scene" should "instantiate game objects" in:
    val instantiatedObjects = scene()
    instantiatedObjects.find[Identifiable]("1").isDefined shouldBe true
    instantiatedObjects.find[Identifiable]("2").isDefined shouldBe true
    instantiatedObjects.find[Identifiable]("3").isDefined shouldBe true
