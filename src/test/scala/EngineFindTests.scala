import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import TestUtils.*
import Dimensions2D.Positionable

class EngineFindTests extends AnyFlatSpec:
  val mock1 = GameObjectMock()
  val mock2 = GameObjectMock()
  val id0 = new Behaviour with Identifiable("0")
  val mockId1 = new GameObjectMock with Identifiable("1")
  val mockId2 = new GameObjectMock with Identifiable("2")
  val gameObjects = Set(mock1, mock2, id0, mockId1, mockId2)
  val engine = Engine(new IO() {}, Storage())
  val scene: Scene = () => gameObjects

  // Grouped by Behaviour
  val gameObjectMocks = Set(mock1, mock2, mockId1, mockId2)
  val identifiables = Set(id0, mockId1, mockId2)

  "find" should "retrieve all objects with a given concrete behaviour" in:
    engine.testOnUpdate(scene):
      engine
        .find[GameObjectMock]() should contain theSameElementsAs gameObjectMocks

  it should "retrieve all objects with a given behaviour" in:
    engine.testOnUpdate(scene):
      engine.find[Identifiable]() should contain theSameElementsAs identifiables

  it should "retrieve all objects if Behaviour is given as type parameters" in:
    engine.testOnUpdateWithContext(scene): (testingContext) =>
      engine.find[
        Behaviour
      ]() should contain theSameElementsAs gameObjects + testingContext.testerObject

  it should "retrieve no objects if none implements the given behaviour" in:
    engine.testOnUpdate(scene):
      engine.find[Positionable]() should contain theSameElementsAs Seq()

  "find(id:)" should "retrieve an Identifiable object with the given identifier" in:
    engine.testOnUpdate(scene):
      engine.find[Identifiable](mockId1.id) shouldBe Some(mockId1)

  it should "retrieve no object if none is found with the given identifier" in:
    engine.testOnUpdate(scene):
      engine.find[Identifiable]("3") shouldBe None
