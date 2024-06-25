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

  "create" should "instantiate a game object in the scene" in:
    engine.testOnLifecycleEvent()(
      onEarlyUpdate =
        engine.find[GameObjectMock]() should contain theSameElementsAs Seq(),
      onUpdate =
        engine.create(obj1),
      onLateUpdate =
        engine.find[GameObjectMock]() should contain only (obj1)
    )

  it should "allow to instantiate multiple objects in every phase of the loop" in:
    engine.testOnLifecycleEvent()(
      onInit =
        engine.find[GameObjectMock]() should contain theSameElementsAs Seq()
        engine.create(obj1),
      onStart =
        engine.find[GameObjectMock]() should contain only (obj1)
        engine.create(obj2),
      onUpdate =
        engine.find[GameObjectMock]() should contain only (obj1, obj2)
        engine.create(obj3),
      onDeInit =
        engine.find[GameObjectMock]() should contain only (obj1, obj2, obj3)
    )

  it should "not instantiate a game object already present in the scene" in:
    engine.testOnLifecycleEvent(scene)(
      onUpdate =
        engine.find[GameObjectMock]() should contain (obj1)
        engine.find[GameObjectMock]() should have size (3)
        engine.create(obj1),
      onLateUpdate =
        engine.find[GameObjectMock]() should have size (3)
    )
    
  it should "invoke the method onInit on the game object" in:
    ???
    

  "destroy" should "remove a game object from the scene" in:
    engine.testOnLifecycleEvent(scene)(
      onEarlyUpdate =
        engine.find[GameObjectMock]() should contain theSameElementsAs scene(),
      onUpdate =
        val removed = engine.find[Identifiable]("1").get
        engine.destroy(removed),
      onLateUpdate =
        engine.find[GameObjectMock]() should contain only (obj2, obj3)
    )

  it should "allow to remove multiple objects in every phase of the loop" in:
    engine.testOnLifecycleEvent(scene)(
      onInit =
        engine.find[GameObjectMock]() should contain theSameElementsAs scene()
        engine.destroy(obj1),
      onStart =
        engine.find[GameObjectMock]() should contain only (obj2, obj3)
        engine.destroy(obj2),
      onUpdate =
        engine.find[GameObjectMock]() should contain only (obj3)
        engine.destroy(obj3),
      onDeInit =
        engine.find[GameObjectMock]() should have size (0)
    )

  it should "remove an object only if already instantiated in the scene" in:
    engine.testOnLifecycleEvent(scene)(
      onUpdate =
        engine.destroy(new GameObjectMock())
        engine.destroy(new GameObjectMock() with Identifiable("1")),
      onDeInit =
        engine.find[GameObjectMock]() should contain theSameElementsAs scene()
    )