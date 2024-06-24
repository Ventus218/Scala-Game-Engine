import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import TestUtils.*
import TestUtils.Testers.*

class EngineObjectCreationTests extends AnyFlatSpec:
  val obj1 = new GameObjectMock() with Identifiable("1")
  val obj2 = new GameObjectMock() with Identifiable("2")
  val obj3 = new GameObjectMock() with Identifiable("3")
  val engine = Engine(new IO() {}, StorageMock())

  val scene: Scene = () => Seq(obj1, obj2, obj3)
  val objectsWithoutRemoved = Seq(obj2, obj3)

  "create" should "instantiate a game object in the scene" in:
    engine.testOnUpdate():
      engine.find[GameObjectMock]() should contain theSameElementsAs Seq()

      val obj = new GameObjectMock() with Identifiable("obj")
      engine.create(obj)
      engine.find[GameObjectMock]() should contain theSameElementsAs Seq(obj)

  it should "allow to instantiate multiple objects" in:
    engine.testOnUpdate():
      scene().foreach(engine.create)
      engine.find[GameObjectMock]() should contain theSameElementsAs scene()

  "destroy" should "remove a game object from the scene" in:
    engine.testOnUpdate(scene):
      engine.find[GameObjectMock]() should contain theSameElementsAs scene()

      val removed = engine.find[Identifiable]("1").get
      engine.destroy(removed)
      engine.find[GameObjectMock]() should contain theSameElementsAs objectsWithoutRemoved

  it should "allow to remove multiple objects" in:
    engine.testOnUpdate(scene):
      objectsWithoutRemoved.foreach(engine.destroy)
      engine.find[GameObjectMock]() should contain theSameElementsAs Seq(obj1)

  it should "remove an object only if present" in:
    engine.testOnUpdate(scene):
      engine.destroy(new GameObjectMock())
      engine.destroy(new GameObjectMock() with Identifiable("1"))
      engine.find[GameObjectMock]() should contain theSameElementsAs scene()